package com.example.myapplication.presentation.component

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.math.BigDecimal

@Composable
fun PaymentButton(
    total: BigDecimal,
    onClick : () -> Unit
) {

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            Log.d("TEST_BUTTON", "Button clicked")
            onClick()
            Log.d("TEST_BUTTON", "After invoke onClick")
        }
    ) {

        Text("Thanh toán $total đ")
    }
}