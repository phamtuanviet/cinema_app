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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class AdminVoucherCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Dùng cho Toast lỗi chung

    val code: String = "",
    val codeError: String? = null,

    val discountType: String = "PERCENT", // PERCENT hoặc FIXED

    val discountValueStr: String = "",
    val discountValueError: String? = null,

    val minOrderValueStr: String = "",
    val maxDiscountStr: String = "",
    val usageLimitStr: String = "",

    val expiryDateStr: String = "",
    val expiryDateError: String? = null,

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
            // 🔥 Tự xóa lỗi viền đỏ khi người dùng bắt đầu gõ sửa lại
            is VoucherCreateEvent.CodeChanged -> _state.update { it.copy(code = event.code.uppercase().trim(), codeError = null) }
            is VoucherCreateEvent.ValueChanged -> _state.update { it.copy(discountValueStr = event.value, discountValueError = null) }
            is VoucherCreateEvent.ExpiryDateChanged -> _state.update { it.copy(expiryDateStr = event.date, expiryDateError = null) }

            is VoucherCreateEvent.TypeChanged -> _state.update {
                it.copy(discountType = event.type, maxDiscountStr = if (event.type == "FIXED") "" else it.maxDiscountStr, discountValueError = null)
            }
            is VoucherCreateEvent.MinOrderChanged -> _state.update { it.copy(minOrderValueStr = event.minOrder) }
            is VoucherCreateEvent.MaxDiscountChanged -> _state.update { it.copy(maxDiscountStr = event.maxDiscount) }
            is VoucherCreateEvent.UsageLimitChanged -> _state.update { it.copy(usageLimitStr = event.limit) }
            is VoucherCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }

            is VoucherCreateEvent.SaveClicked -> saveVoucher()
        }
    }

    // Dọn lỗi để Toast không hiện lại
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun saveVoucher() {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra Mã Code
        if (st.code.isBlank()) {
            _state.update { it.copy(codeError = "Mã Voucher không được để trống") }
            isValid = false
        }

        // 2. Kiểm tra Mức giảm
        val discountValue = st.discountValueStr.toDoubleOrNull() ?: -1.0
        if (st.discountValueStr.isBlank() || discountValue <= 0 || (st.discountType == "PERCENT" && discountValue > 100)) {
            val errorMsg = if (st.discountType == "PERCENT") "Phần trăm giảm phải từ 1 đến 100" else "Mức giảm tiền mặt không hợp lệ"
            _state.update { it.copy(discountValueError = errorMsg) }
            isValid = false
        }

        // 3. Xử lý thời gian từ Picker (yyyy-MM-dd HH:mm) sang chuẩn ISO gửi Server (yyyy-MM-ddTHH:mm:ss)
        var formattedExpiryDate: String? = null
        if (st.expiryDateStr.isNotBlank()) {
            try {
                val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val parsedDate = LocalDateTime.parse(st.expiryDateStr, formatterIn)
                formattedExpiryDate = parsedDate.toString()
            } catch (e: Exception) {
                _state.update { it.copy(expiryDateError = "Định dạng ngày không hợp lệ") }
                isValid = false
            }
        }

        // Nếu có bất kì lỗi nào, dừng lại không gọi API
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminVoucherCreateRequest(
                code = st.code,
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
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Phát hành Voucher thất bại") }
            }
        }
    }
}