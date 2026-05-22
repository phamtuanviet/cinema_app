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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// --- STATE ---
data class AdminVoucherEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Dùng cho Toast chung

    val code: String = "",
    val codeError: String? = null,

    val discountType: String = "PERCENT",

    val discountValueStr: String = "",
    val discountValueError: String? = null,

    val minOrderValueStr: String = "",
    val maxDiscountStr: String = "",
    val usageLimitStr: String = "",

    val expiryDateStr: String = "",
    val expiryDateError: String? = null,

    val isActive: Boolean = true,
    val usedCount: Int = 0
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
                fun formatNum(num: Double?) = if (num == null) "" else if (num % 1 == 0.0) num.toLong().toString() else num.toString()

                // Parse ngày về chuẩn yyyy-MM-dd HH:mm cho DatePicker hiển thị
                var rawDate = ""
                if (!v.expiryDate.isNullOrEmpty()) {
                    try {
                        // Thử parse nếu backend trả về chuẩn ISO (yyyy-MM-dd'T'HH:mm:ss)
                        val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        val formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        rawDate = LocalDateTime.parse(v.expiryDate.take(19), formatterIn).format(formatterOut)
                    } catch (e: Exception) {
                        // Fallback logic cũ nếu API trả về format custom "HH:mm - dd/MM/yyyy"
                        if (v.expiryDate.length == 18) {
                            val time = v.expiryDate.substring(0, 5)
                            val day = v.expiryDate.substring(8, 10)
                            val month = v.expiryDate.substring(11, 13)
                            val year = v.expiryDate.substring(14, 18)
                            rawDate = "$year-$month-$day $time"
                        } else {
                            rawDate = v.expiryDate
                        }
                    }
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
            // 🔥 Tự xóa viền đỏ khi người dùng gõ sửa lại
            is VoucherEditEvent.CodeChanged -> _state.update { it.copy(code = event.code.uppercase().trim(), codeError = null) }
            is VoucherEditEvent.ValueChanged -> _state.update { it.copy(discountValueStr = event.value, discountValueError = null) }
            is VoucherEditEvent.ExpiryDateChanged -> _state.update { it.copy(expiryDateStr = event.date, expiryDateError = null) }

            is VoucherEditEvent.TypeChanged -> _state.update {
                it.copy(discountType = event.type, maxDiscountStr = if (event.type == "FIXED") "" else it.maxDiscountStr, discountValueError = null)
            }
            is VoucherEditEvent.MinOrderChanged -> _state.update { it.copy(minOrderValueStr = event.minOrder) }
            is VoucherEditEvent.MaxDiscountChanged -> _state.update { it.copy(maxDiscountStr = event.maxDiscount) }
            is VoucherEditEvent.UsageLimitChanged -> _state.update { it.copy(usageLimitStr = event.limit) }
            is VoucherEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is VoucherEditEvent.SaveClicked -> saveVoucher()
        }
    }

    // Dọn lỗi để Toast không lặp lại
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun saveVoucher() {
        val st = _state.value
        var isValid = true

        if (st.code.isBlank()) {
            _state.update { it.copy(codeError = "Mã Code không được để trống") }
            isValid = false
        }

        val discountValue = st.discountValueStr.toDoubleOrNull() ?: -1.0
        if (st.discountValueStr.isBlank() || discountValue <= 0 || (st.discountType == "PERCENT" && discountValue > 100)) {
            val errorMsg = if (st.discountType == "PERCENT") "Phần trăm giảm phải từ 1 đến 100" else "Mức giảm tiền mặt không hợp lệ"
            _state.update { it.copy(discountValueError = errorMsg) }
            isValid = false
        }

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

        if (!isValid) return

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
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Cập nhật thất bại") }
            }
        }
    }
}