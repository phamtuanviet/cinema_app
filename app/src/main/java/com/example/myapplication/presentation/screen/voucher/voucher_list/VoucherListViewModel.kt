package com.example.myapplication.presentation.screen.voucher.voucher_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.VoucherStatus
import com.example.myapplication.domain.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VoucherListViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VoucherListState())
    val state: StateFlow<VoucherListState> = _state

    init {
        loadVouchers(VoucherStatus.AVAILABLE)
    }

    fun onVoucherTabChange(status: VoucherStatus) {
        _state.update { it.copy(selectedVoucherTab = status) }
        loadVouchers(status)
    }

    private fun loadVouchers(status: VoucherStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            voucherRepository.getVouchers(status)
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            vouchers = data
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    fun addVoucher(code: String) {
        if (code.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập mã giảm giá") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAddingVoucher = true) }

            voucherRepository.addVoucher(code.trim())
                .onSuccess {
                    loadVouchers(_state.value.selectedVoucherTab)
                    _state.update {
                        it.copy(
                            isAddingVoucher = false,
                            error = "Lưu mã giảm giá thành công!"
                        )
                    }
                }
                .onFailure { e ->
                    val friendlyMessage = getFriendlyErrorMessage(e)
                    _state.update {
                        it.copy(
                            isAddingVoucher = false,
                            error = friendlyMessage
                        )
                    }
                }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun getFriendlyErrorMessage(e: Throwable): String {
        val responseBody = if (e is HttpException) {
            try { e.response()?.errorBody()?.string() ?: "" } catch (ex: Exception) { "" }
        } else ""

        val fullMessage = "${e.message} $responseBody".lowercase()

        return when {
            "not found" in fullMessage -> "Mã giảm giá không tồn tại. Vui lòng kiểm tra lại."
            "expired" in fullMessage -> "Rất tiếc! Mã giảm giá này đã hết hạn sử dụng."
            "out of stock" in fullMessage || "limit" in fullMessage -> "Mã giảm giá này đã hết lượt lưu mất rồi!"
            "already added" in fullMessage -> "Bạn đã lưu mã giảm giá này trong ví rồi."
            "not active" in fullMessage -> "Mã giảm giá này hiện không khả dụng."
            e is HttpException -> {
                when (e.code()) {
                    403 -> "Mã giảm giá không hợp lệ hoặc bạn không đủ điều kiện."
                    404 -> "Không tìm thấy mã giảm giá."
                    else -> "Lỗi hệ thống (${e.code()}). Vui lòng thử lại sau."
                }
            }
            e is IOException -> "Lỗi kết nối mạng. Vui lòng kiểm tra Wifi/4G."
            else -> "Không thể lưu mã lúc này. Vui lòng thử lại sau."
        }
    }
}