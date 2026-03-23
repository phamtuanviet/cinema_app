package com.example.myapplication.presentation.screen.payment.sucess

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PaymentSuccessScreen(onBackHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Payment Success 🎉")
        Button(onClick = onBackHome) {
            Text("Back to Home")
        }
    }
}