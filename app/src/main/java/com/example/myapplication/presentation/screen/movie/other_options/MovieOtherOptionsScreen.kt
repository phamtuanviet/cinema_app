package com.example.myapplication.presentation.screen.movie.other_options

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.ComboSection
import com.example.myapplication.presentation.component.PaymentButton
import com.example.myapplication.presentation.component.PointsSection
import com.example.myapplication.presentation.component.PriceSummary
import com.example.myapplication.presentation.component.VoucherSection

@Composable
fun MovieOtherOptionsScreen(
    seatHoldSessionId : String,
    onSuccessResult : (bookingId :String) -> Unit,
    viewModel: MovieOtherOptionsViewModel = hiltViewModel()
) {



    val state by viewModel.state.collectAsState()

    LaunchedEffect(seatHoldSessionId) {
        viewModel.loadData(seatHoldSessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        ComboSection(
            combos = state.combos,
            selectedCombos = state.selectedCombos,
            onIncrease = viewModel::increaseCombo,
            onDecrease = viewModel::decreaseCombo
        )

        Spacer(modifier = Modifier.height(16.dp))

        VoucherSection(
            vouchers = state.vouchers,
            selectedVoucher = state.selectedVoucherId,
            onSelect = viewModel::selectVoucher
        )

        Spacer(modifier = Modifier.height(16.dp))

        PointsSection(
            availablePoints = state.availablePoints,
            usedPoints = state.usedPoints,
            onChange = viewModel::changeUsedPoints
        )

        Spacer(modifier = Modifier.height(16.dp))

        PriceSummary(state)

        Spacer(modifier = Modifier.height(24.dp))


        PaymentButton(
            total = state.totalAmount ,
            onClick = {
                Log.d("MovieOtherOptionsViewModel", "createBooking: $seatHoldSessionId")

                viewModel.createBooking(seatHoldSessionId,onSuccessResult)
            }
        )
    }
}