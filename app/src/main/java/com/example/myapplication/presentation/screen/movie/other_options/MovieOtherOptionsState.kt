package com.example.myapplication.presentation.screen.movie.other_options

import com.example.myapplication.data.remote.dto.ComboDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.ShowtimeDetailDto
import com.example.myapplication.data.remote.dto.VoucherDto

data class MovieOtherOptionsState (
    val isLoading: Boolean = false,
    val error: String? = null,

    // data từ server
    val movie : MovieDto ? = null,
    val showtime : ShowtimeDetailDto ? = null,
    val combos: List<ComboDto> = emptyList(),
    val vouchers: List<VoucherDto> = emptyList(),
    val availablePoints: Int = 0,

    // user selection
    val selectedCombos: Map<String, Int> = emptyMap(),
    val selectedVoucherId: String? = null,
    val usedPoints: Int = 0,

    val voucherInput: String = "",

    // price preview
    val seatAmount: Double = 0.0,
    val comboAmount: Double = 0.0,
    val voucherDiscount: Double = 0.0,
    val pointDiscount: Double = 0.0,
    val totalAmount: Double = 0.0,
)