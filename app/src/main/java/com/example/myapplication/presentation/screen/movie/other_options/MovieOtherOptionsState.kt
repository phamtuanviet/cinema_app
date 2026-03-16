package com.example.myapplication.presentation.screen.movie.other_options

import com.example.myapplication.data.remote.dto.ComboDto
import com.example.myapplication.data.remote.dto.VoucherDto
import java.math.BigDecimal

data class MovieOtherOptionsState (
    val isLoading: Boolean = false,
    val error: String? = null,

    // data từ server
    val combos: List<ComboDto> = emptyList(),
    val vouchers: List<VoucherDto> = emptyList(),
    val availablePoints: Int = 0,

    // user selection
    val selectedCombos: Map<String, Int> = emptyMap(),
    val selectedVoucherId: String? = null,
    val usedPoints: Int = 0,

    // price preview
    val seatAmount: BigDecimal = BigDecimal.ZERO,
    val comboAmount: BigDecimal = BigDecimal.ZERO,
    val voucherDiscount: BigDecimal = BigDecimal.ZERO,
    val pointDiscount: BigDecimal = BigDecimal.ZERO,
    val totalAmount: BigDecimal = BigDecimal.ZERO
)