package com.example.myapplication.presentation.screen.movie.checkout

import androidx.activity.ComponentActivity
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
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.presentation.app.AppViewModel
import com.example.myapplication.utils.openPayment

@Composable
fun MovieCheckoutScreen(
    bookingId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    viewModel: MovieCheckoutViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val activity = LocalContext.current as ComponentActivity
    val appViewModel: AppViewModel = hiltViewModel(activity)

    val paymentResult by appViewModel.paymentResult.collectAsState()

    // =========================
    // 🔥 OPEN VNPAY
    // =========================
    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            openPayment(context, url)
            viewModel.clearPaymentUrl()
        }
    }

    // =========================
    // 🔥 HANDLE RETURN
    // =========================
    LaunchedEffect(paymentResult?.txnRef) {
        val result = paymentResult ?: return@LaunchedEffect

        // ✅ đúng booking
        if (result.txnRef != bookingId) return@LaunchedEffect

        if (result.code == "00") {
            onPaymentSuccess()
        } else {
            onPaymentFailed()
        }

        // 🔥 clear ở đúng chỗ
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