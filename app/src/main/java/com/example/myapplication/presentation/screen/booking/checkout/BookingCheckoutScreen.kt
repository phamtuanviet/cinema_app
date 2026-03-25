package com.example.myapplication.presentation.screen.booking.checkout

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.MainActivity
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.utils.openPayment

@Composable
fun BookingCheckoutScreen(
    bookingId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    viewModel: BookingCheckoutViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val appViewModel = (LocalContext.current as MainActivity).appViewModel

    val paymentResult by appViewModel.paymentResult.collectAsState()

    LaunchedEffect(paymentResult) {
        Log.d("CHECK_PAYMENT", "paymentResult = $paymentResult")
    }


    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            openPayment(context, url)
            viewModel.clearPaymentUrl()
        }
    }

    // =========================
    // 🔥 HANDLE RETURN
    // =========================
    LaunchedEffect(paymentResult) {
        Log.d("CHECK_PAYMENT", "paymentResult2 = $paymentResult")

        val result = paymentResult ?: return@LaunchedEffect

        if (result.code == "00") {
            onPaymentSuccess()
        } else {
            onPaymentFailed()
        }

        appViewModel.clearPaymentResult()
    }

    // =========================
    // UI
    // =========================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Select Payment Method")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.selectPaymentMethod(PaymentMethod.VNPAY)
            }
        ) {
            Text("VNPAY")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.createPayment(bookingId)
            },
            enabled = state.selectedPaymentMethod != null
        ) {
            Text("Pay Now")
        }
    }
}