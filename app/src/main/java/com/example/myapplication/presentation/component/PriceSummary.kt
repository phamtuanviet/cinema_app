package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.myapplication.presentation.screen.movie.other_options.MovieOtherOptionsState

@Composable
fun PriceSummary(state: MovieOtherOptionsState) {

    Column {

        PriceRow("Tổng tiền", state.seatAmount + state.comboAmount)

        PriceRow(
            "Số tiền được giảm",
            state.voucherDiscount + state.pointDiscount
        )

        PriceRow(
            "Số tiền cần thanh toán",
            state.totalAmount,
            highlight = true
        )
    }
}