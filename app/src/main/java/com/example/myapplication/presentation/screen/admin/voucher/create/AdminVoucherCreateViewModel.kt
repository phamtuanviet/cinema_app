package com.example.myapplication.presentation.screen.admin.voucher.create


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminVoucherCreateRequest
import com.example.myapplication.domain.repository.AdminVoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminVoucherCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val code: String = "",
    val discountType: String = "PERCENT", // PERCENT hoặc FIXED
    val discountValueStr: String = "",
    val minOrderValueStr: String = "",
    val maxDiscountStr: String = "",
    val usageLimitStr: String = "",
    val expiryDateStr: String = "", // Định dạng nhập: yyyy-MM-dd HH:mm
    val isActive: Boolean = true
)

sealed class VoucherCreateEvent {
    data class CodeChanged(val code: String) : VoucherCreateEvent()
    data class TypeChanged(val type: String) : VoucherCreateEvent()
    data class ValueChanged(val value: String) : VoucherCreateEvent()
    data class MinOrderChanged(val minOrder: String) : VoucherCreateEvent()
    data class MaxDiscountChanged(val maxDiscount: String) : VoucherCreateEvent()
    data class UsageLimitChanged(val limit: String) : VoucherCreateEvent()
    data class ExpiryDateChanged(val date: String) : VoucherCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : VoucherCreateEvent()
    object SaveClicked : VoucherCreateEvent()
}

@HiltViewModel
class AdminVoucherCreateViewModel @Inject constructor(
    private val repository: AdminVoucherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminVoucherCreateState())
    val state = _state.asStateFlow()

    fun onEvent(event: VoucherCreateEvent) {
        when (event) {
            is VoucherCreateEvent.CodeChanged -> _state.update { it.copy(code = event.code.uppercase()) }
            is VoucherCreateEvent.TypeChanged -> _state.update {
                it.copy(discountType = event.type, maxDiscountStr = if (event.type == "FIXED") "" else it.maxDiscountStr)
            }
            is VoucherCreateEvent.ValueChanged -> _state.update { it.copy(discountValueStr = event.value) }
            is VoucherCreateEvent.MinOrderChanged -> _state.update { it.copy(minOrderValueStr = event.minOrder) }
            is VoucherCreateEvent.MaxDiscountChanged -> _state.update { it.copy(maxDiscountStr = event.maxDiscount) }
            is VoucherCreateEvent.UsageLimitChanged -> _state.update { it.copy(usageLimitStr = event.limit) }
            is VoucherCreateEvent.ExpiryDateChanged -> _state.update { it.copy(expiryDateStr = event.date) }
            is VoucherCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is VoucherCreateEvent.SaveClicked -> saveVoucher()
        }
    }

    private fun saveVoucher() {
        val st = _state.value

        if (st.code.isBlank() || st.discountValueStr.isBlank()) {
            _state.update { it.copy(error = "Mã Voucher và Mức giảm không được trống") }
            return
        }

        val discountValue = st.discountValueStr.toDoubleOrNull() ?: -1.0
        if (discountValue <= 0 || (st.discountType == "PERCENT" && discountValue > 100)) {
            _state.update { it.copy(error = "Mức giảm không hợp lệ (Phần trăm phải từ 1-100)") }
            return
        }

        var formattedExpiryDate: String? = null
        if (st.expiryDateStr.isNotBlank()) {
            try {
                val parts = st.expiryDateStr.trim().split(" ")
                val datePart = parts[0] // yyyy-MM-dd
                val timePart = if (parts.size > 1) parts[1] else "23:59"
                formattedExpiryDate = "${datePart}T${timePart}:00"
            } catch (e: Exception) {
                _state.update { it.copy(error = "Sai định dạng ngày (VD: 2026-12-31 23:59)") }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminVoucherCreateRequest(
                code = st.code.trim(),
                discountType = st.discountType,
                discountValue = discountValue,
                minOrderValue = st.minOrderValueStr.toDoubleOrNull().takeIf { it != null && it > 0 },
                maxDiscount = if (st.discountType == "PERCENT") st.maxDiscountStr.toDoubleOrNull().takeIf { it != null && it > 0 } else null,
                usageLimit = st.usageLimitStr.toIntOrNull().takeIf { it != null && it > 0 },
                expiryDate = formattedExpiryDate,
                active = st.isActive
            )

            val result = repository.createVoucher(request)
            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}