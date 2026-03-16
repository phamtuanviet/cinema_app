package com.example.myapplication.presentation.screen.movie.other_options

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.VoucherDiscountType
import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.data.remote.dto.BookingComboRequest
import com.example.myapplication.data.remote.dto.CreateBookingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MovieOtherOptionsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieOtherOptionsState())
    val state = _state.asStateFlow()

    fun loadData(sessionId: String) {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            val combos = repository.getCombos()
            val vouchers = repository.getAvailableVouchers()
            val points = repository.getAvailablePoints()

            _state.update {
                it.copy(
                    combos = combos,
                    vouchers = vouchers,
                    availablePoints = points,
                    isLoading = false
                )
            }
        }
    }

    fun increaseCombo(comboId: String) {

        _state.update { state ->

            val newMap = state.selectedCombos.toMutableMap()
            val current = newMap[comboId] ?: 0

            newMap[comboId] = current + 1

            state.copy(selectedCombos = newMap)
        }

        calculateLocalPrice()
    }

    fun decreaseCombo(comboId: String) {

        _state.update { state ->

            val newMap = state.selectedCombos.toMutableMap()
            val current = newMap[comboId] ?: 0

            if (current <= 1) {
                newMap.remove(comboId)
            } else {
                newMap[comboId] = current - 1
            }

            state.copy(selectedCombos = newMap)
        }

        calculateLocalPrice()
    }

    fun selectVoucher(voucherId: String?) {

        _state.update {
            it.copy(selectedVoucherId = voucherId)
        }

        calculateLocalPrice()
    }

    fun changeUsedPoints(points: Int) {

        _state.update {
            it.copy(usedPoints = points)
        }

        calculateLocalPrice()
    }

    private fun calculateLocalPrice() {

        val state = _state.value

        val comboAmount = state.selectedCombos.entries.fold(BigDecimal.ZERO) { acc, entry ->

            val comboId = entry.key
            val quantity = entry.value

            val combo = state.combos.find { it.id == comboId }

            if (combo != null) {
                acc + combo.price.multiply(BigDecimal(quantity))
            } else {
                acc
            }
        }

        val seatAmount = state.seatAmount

        val subtotal = seatAmount + comboAmount

        val voucher = state.vouchers.find { it.id == state.selectedVoucherId }

        var voucherDiscount = BigDecimal.ZERO

        if (voucher != null) {

            val minOrder = voucher.minOrderAmount ?: BigDecimal.ZERO

            if (subtotal >= minOrder) {

                voucherDiscount = when (voucher.discountType) {

                    VoucherDiscountType.FIXED_AMOUNT ->
                        voucher.discountValue

                    VoucherDiscountType.PERCENTAGE -> {

                        var discount = subtotal
                            .multiply(voucher.discountValue)
                            .divide(BigDecimal(100))

                        val max = voucher.maxDiscount

                        if (max != null && discount > max) {
                            discount = max
                        }

                        discount
                    }
                }
            }
        }

        val pointDiscount = BigDecimal(state.usedPoints)

        var total = subtotal
            .subtract(voucherDiscount)
            .subtract(pointDiscount)

        if (total < BigDecimal.ZERO) {
            total = BigDecimal.ZERO
        }

        _state.update {
            it.copy(
                comboAmount = comboAmount,
                voucherDiscount = voucherDiscount,
                pointDiscount = pointDiscount,
                totalAmount = total
            )
        }
    }

    fun createBooking(seatHoldSessionId: String,onSuccess : (String) -> Unit){
        Log.d("MovieOtherOptionsViewModel", "createBooking: $seatHoldSessionId")
        val state = _state.value

        val combos = state.selectedCombos.map { entry ->
            BookingComboRequest(
                comboId = entry.key,
                quantity = entry.value
            )
        }

        val request = CreateBookingRequest(
            sessionId = seatHoldSessionId,
            combos = combos,
            voucherId = state.selectedVoucherId,
            usedPoints = state.usedPoints
        )

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            try {

                val booking = repository.createBooking(request)

                _state.update { it.copy(isLoading = false) }

                onSuccess(booking.id)

            } catch (e: Exception) {

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