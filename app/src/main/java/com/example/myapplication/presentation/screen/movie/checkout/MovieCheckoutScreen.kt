package com.example.myapplication.presentation.screen.movie.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.presentation.component.PaymentMethodItem
import com.example.myapplication.presentation.screen.cinema.showtime.CinemaShowtimeViewModel

@Composable
fun MovieCheckoutScreen(
    bookingId: String,
    onPaymentCreated: (String) -> Unit,
    viewModel: MovieCheckoutViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.init(bookingId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Chọn phương thức thanh toán",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        PaymentMethodItem(
            title = "ZaloPay",
            selected = state.selectedPaymentMethod == PaymentMethod.ZALOPAY,
            onClick = {
                viewModel.selectPayment(PaymentMethod.ZALOPAY)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethodItem(
            title = "VNPay",
            selected = state.selectedPaymentMethod == PaymentMethod.VNPAY,
            onClick = {
                viewModel.selectPayment(PaymentMethod.VNPAY)
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.selectedPaymentMethod != null && !state.isLoading,
            onClick = {

                viewModel.createPayment(
                    onPaymentCreated
                )
            }
        ) {

            Text("Thanh toán")

        }

    }
}