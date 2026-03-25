package com.example.myapplication.presentation.screen.booking.other_options

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.MovieRepository

import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.domain.repository.SeatHoldSessionRepository
import com.example.myapplication.domain.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingOtherOptionsViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val seatHoldSessionRepository: SeatHoldSessionRepository,
    private val voucherRepository: VoucherRepository,
    private val bookingRepository: BookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BookingOtherOptionsState())
    val state = _state.asStateFlow()


    fun increaseCombo(comboId: String) {
        _state.update { state ->
            val newMap = state.selectedCombos.toMutableMap()
            newMap[comboId] = (newMap[comboId] ?: 0) + 1

            recalculate(state.copy(selectedCombos = newMap))
        }
    }

    fun decreaseCombo(comboId: String) {
        _state.update { state ->
            val newMap = state.selectedCombos.toMutableMap()
            val current = newMap[comboId] ?: 0

            if (current <= 1) newMap.remove(comboId)
            else newMap[comboId] = current - 1

            recalculate(state.copy(selectedCombos = newMap))
        }
    }

    fun selectVoucher(voucherId: String?) {
        _state.update { state ->
            recalculate(state.copy(selectedVoucherId = voucherId))
        }
    }

    fun changeUsedPoints(points: Int) {
        _state.update { state ->
            val safePoints = points.coerceIn(0, state.availablePoints)
            recalculate(state.copy(usedPoints = safePoints))
        }
    }

    private fun recalculate(state: BookingOtherOptionsState): BookingOtherOptionsState {

        // 🍿 combo amount
        val comboAmount = state.selectedCombos.entries.sumOf { (id, qty) ->
            val combo = state.combos.find { it.id == id }
            (combo?.price?.toDouble() ?: 0.0) * qty
        }

        // 💺 subtotal
        val subtotal = state.seatAmount + comboAmount

        // 🎟 voucher
        val voucher = state.vouchers.find { it.id == state.selectedVoucherId }
        val voucherDiscount = if (voucher != null && voucher.isUsable == true) {
            when (voucher.discountType.name) {
                "PERCENT" -> {
                    val discount = subtotal * voucher.discountValue.toDouble() / 100
                    minOf(discount, voucher.maxDiscount?.toDouble() ?: discount)
                }

                "FIXED" -> voucher.discountValue.toDouble()
                else -> 0.0
            }
        } else 0.0

        // 💰 point discount (ví dụ 1 point = 1 VND)
        val pointDiscount = state.usedPoints.toDouble()

        // 💵 total
        val total = (subtotal - voucherDiscount - pointDiscount)
            .coerceAtLeast(0.0)

        return state.copy(
            comboAmount = comboAmount,
            voucherDiscount = voucherDiscount,
            pointDiscount = pointDiscount,
            totalAmount = total
        )
    }

    fun onVoucherInputChange(value: String) {
        _state.update { it.copy(voucherInput = value) }
    }

    fun addVoucher() {
        val code = state.value.voucherInput.trim()
        if (code.isEmpty()) return

        viewModelScope.launch {

            voucherRepository.addVoucher(code)
                .onSuccess { voucher ->

                    _state.update { state ->

                        val newList: List<VoucherDto> =
                            (state.vouchers + voucher)
                                .distinctBy { it.id }

                        recalculate(
                            state.copy(
                                vouchers = newList,
                                voucherInput = ""
                            )
                        )
                    }

                }
                .onFailure { e ->
                    _state.update {
                        it.copy(error = e.message ?: "Add voucher failed")
                    }
                }
        }
    }

    fun loadData(sessionId: String) {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            seatHoldSessionRepository
                .getSeatHoldSessionInfo(sessionId)
                .onSuccess { res ->
                    Log.d("SeatHoldSessionInfo", res.toString())

                    _state.update {
                        recalculate(
                            it.copy(
                                isLoading = false,
                                movie = res.movie,
                                showtime = res.showtime,
                                seatAmount = res.seatAmount,
                                combos = res.combos,
                                vouchers = res.vouchers,
                                availablePoints = res.availablePoints
                            )
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

    fun createBooking(sessionId: String, onSuccessResult: (bookingId: String) -> Unit) {
        viewModelScope.launch {
            Log.d("MovieOtherOptionsViewModel", "createBooking")
            _state.update { it.copy(isLoading = true) }
            bookingRepository.booking(
                sessionId,
                state.value.selectedCombos,
                state.value.selectedVoucherId,
                state.value.usedPoints
            )
                .onSuccess { booking ->
                    _state.update { it.copy(isLoading = false) }
                    onSuccessResult(booking.bookingId)
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
}