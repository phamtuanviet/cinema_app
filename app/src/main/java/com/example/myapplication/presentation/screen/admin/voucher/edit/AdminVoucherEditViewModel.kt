package com.example.myapplication.presentation.screen.admin.voucher.edit

import com.example.myapplication.domain.repository.AdminVoucherRepository


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminVoucherUpdateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- STATE ---
data class AdminVoucherEditState(
    val isLoadingData: Boolean = true,
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
    val isActive: Boolean = true,

    val usedCount: Int = 0 // Chỉ hiển thị, không được sửa
)

// --- EVENT ---
sealed class VoucherEditEvent {
    data class CodeChanged(val code: String) : VoucherEditEvent()
    data class TypeChanged(val type: String) : VoucherEditEvent()
    data class ValueChanged(val value: String) : VoucherEditEvent()
    data class MinOrderChanged(val minOrder: String) : VoucherEditEvent()
    data class MaxDiscountChanged(val maxDiscount: String) : VoucherEditEvent()
    data class UsageLimitChanged(val limit: String) : VoucherEditEvent()
    data class ExpiryDateChanged(val date: String) : VoucherEditEvent()
    data class IsActiveChanged(val isActive: Boolean) : VoucherEditEvent()
    object SaveClicked : VoucherEditEvent()
}

@HiltViewModel
class AdminVoucherEditViewModel @Inject constructor(
    private val repository: AdminVoucherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val voucherId: String = checkNotNull(savedStateHandle["voucherId"])

    private val _state = MutableStateFlow(AdminVoucherEditState())
    val state = _state.asStateFlow()

    init {
        loadVoucherData()
    }

    private fun loadVoucherData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }
            val result = repository.getVoucherById(voucherId)

            if (result.isSuccess) {
                val v = result.getOrNull()!!

                // Helper format số thập phân .0
                fun formatNum(num: Double?) = if (num == null) "" else if (num % 1 == 0.0) num.toLong().toString() else num.toString()

                // Đổi ngày từ "HH:mm - dd/MM/yyyy" sang "yyyy-MM-dd HH:mm" để Admin dễ sửa
                var rawDate = ""
                if (!v.expiryDate.isNullOrEmpty() && v.expiryDate.length == 18) {
                    // Ví dụ: 15:30 - 20/12/2026 -> 2026-12-20 15:30
                    val time = v.expiryDate.substring(0, 5)
                    val day = v.expiryDate.substring(8, 10)
                    val month = v.expiryDate.substring(11, 13)
                    val year = v.expiryDate.substring(14, 18)
                    rawDate = "$year-$month-$day $time"
                }

                _state.update {
                    it.copy(
                        isLoadingData = false,
                        code = v.code,
                        discountType = v.discountType,
                        discountValueStr = formatNum(v.discountValue),
                        minOrderValueStr = formatNum(v.minOrderValue),
                        maxDiscountStr = formatNum(v.maxDiscount),
                        usageLimitStr = v.usageLimit?.toString() ?: "",
                        expiryDateStr = rawDate,
                        isActive = v.active,
                        usedCount = v.usedCount
                    )
                }
            } else {
                _state.update { it.copy(isLoadingData = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun onEvent(event: VoucherEditEvent) {
        when (event) {
            is VoucherEditEvent.CodeChanged -> _state.update { it.copy(code = event.code.uppercase()) }
            is VoucherEditEvent.TypeChanged -> _state.update {
                // Khi đổi loại, nếu đổi sang FIXED thì clear MaxDiscount đi
                it.copy(discountType = event.type, maxDiscountStr = if (event.type == "FIXED") "" else it.maxDiscountStr)
            }
            is VoucherEditEvent.ValueChanged -> _state.update { it.copy(discountValueStr = event.value) }
            is VoucherEditEvent.MinOrderChanged -> _state.update { it.copy(minOrderValueStr = event.minOrder) }
            is VoucherEditEvent.MaxDiscountChanged -> _state.update { it.copy(maxDiscountStr = event.maxDiscount) }
            is VoucherEditEvent.UsageLimitChanged -> _state.update { it.copy(usageLimitStr = event.limit) }
            is VoucherEditEvent.ExpiryDateChanged -> _state.update { it.copy(expiryDateStr = event.date) }
            is VoucherEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is VoucherEditEvent.SaveClicked -> saveVoucher()
        }
    }

    private fun saveVoucher() {
        val st = _state.value

        if (st.code.isBlank() || st.discountValueStr.isBlank()) {
            _state.update { it.copy(error = "Mã Code và Giá trị giảm không được để trống") }
            return
        }

        val discountValue = st.discountValueStr.toDoubleOrNull() ?: -1.0
        if (discountValue <= 0 || (st.discountType == "PERCENT" && discountValue > 100)) {
            _state.update { it.copy(error = "Giá trị giảm không hợp lệ (Phần trăm phải từ 1-100)") }
            return
        }

        // Format lại ngày tháng để gửi cho Spring Boot (ISO-8601)
        var formattedExpiryDate: String? = null
        if (st.expiryDateStr.isNotBlank()) {
            try {
                // Giả sử user nhập "2026-12-20 15:30"
                val parts = st.expiryDateStr.split(" ")
                val datePart = parts[0] // 2026-12-20
                val timePart = if (parts.size > 1) parts[1] else "23:59"
                formattedExpiryDate = "${datePart}T${timePart}:00"
            } catch (e: Exception) {
                _state.update { it.copy(error = "Sai định dạng ngày (VD: 2026-12-31 23:59)") }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminVoucherUpdateRequest(
                code = st.code,
                discountType = st.discountType,
                discountValue = discountValue,
                minOrderValue = st.minOrderValueStr.toDoubleOrNull().takeIf { it != null && it > 0 },
                maxDiscount = if (st.discountType == "PERCENT") st.maxDiscountStr.toDoubleOrNull().takeIf { it != null && it > 0 } else null,
                usageLimit = st.usageLimitStr.toIntOrNull().takeIf { it != null && it > 0 },
                expiryDate = formattedExpiryDate,
                active = st.isActive
            )

            val result = repository.updateVoucher(voucherId, request)
            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}