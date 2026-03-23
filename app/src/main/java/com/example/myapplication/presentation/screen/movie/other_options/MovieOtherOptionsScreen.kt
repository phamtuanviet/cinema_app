package com.example.myapplication.presentation.screen.movie.other_options

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.ComboSection
import com.example.myapplication.presentation.component.MovieHeaderOption
import com.example.myapplication.presentation.component.PaymentButton
import com.example.myapplication.presentation.component.PointsSection
import com.example.myapplication.presentation.component.PriceSummary
import com.example.myapplication.presentation.component.VoucherInputSection
import com.example.myapplication.presentation.component.VoucherSection

@Composable
fun MovieOtherOptionsScreen(
    seatHoldSessionId: String,
    onSuccessResult: (bookingId: String) -> Unit,
    viewModel: MovieOtherOptionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(seatHoldSessionId) {
        viewModel.loadData(seatHoldSessionId)
    }

    // 🔥 Loading
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ❌ Error
    state.error?.let {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = it)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .pointerInput(Unit) {               // 👈 detect tap gestures
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus() // 👈 tắt keyboard khi click ra ngoài
                    }
                )
            }
    ) {

        // 🎬 Movie info
        MovieHeaderOption(
            movie = state.movie,
            showtime = state.showtime
        )

        // 🍿 Combo
        ComboSection(
            combos = state.combos,
            selectedCombos = state.selectedCombos,
            onIncrease = viewModel::increaseCombo,
            onDecrease = viewModel::decreaseCombo
        )

        Spacer(Modifier.height(16.dp))

        // 🎟 Voucher input (NEW)
        VoucherInputSection(
            voucherInput = state.voucherInput,
            onInputChange = viewModel::onVoucherInputChange,
            onAddClick = { viewModel.addVoucher() }
        )

        Spacer(Modifier.height(16.dp)) // Tăng khoảng cách cho dễ nhìn

        // 🎟 Voucher list (Đã cải thiện UI)
        VoucherSection(
            vouchers = state.vouchers,
            selectedVoucher = state.selectedVoucherId,
            onSelect = viewModel::selectVoucher
        )


        Spacer(Modifier.height(16.dp))

        // 💰 Points
        PointsSection(
            availablePoints = state.availablePoints,
            usedPoints = state.usedPoints,
            onChange = viewModel::changeUsedPoints
        )

        Spacer(Modifier.height(16.dp))

        // 💵 Price
        PriceSummary(state)

        Spacer(Modifier.height(24.dp))

        // 💳 Button
        Button(
            onClick = {
                viewModel.createBooking(seatHoldSessionId, onSuccessResult)
            },
            enabled = state.totalAmount > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thanh toán: ${state.totalAmount.toInt()}đ")
        }
    }
}