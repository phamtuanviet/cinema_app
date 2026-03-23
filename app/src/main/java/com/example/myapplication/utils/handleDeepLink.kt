package com.example.myapplication.utils

import android.content.Intent
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.app.AppViewModel

fun handleDeepLink(
    intent: Intent,
    appViewModel: AppViewModel
) {
    val uri = intent.data ?: return

    if (uri.scheme == "myapp" && uri.host == "payment-result") {

        val code = uri.getQueryParameter("vnp_ResponseCode")
        val txnRef = uri.getQueryParameter("vnp_TxnRef")

        appViewModel.onPaymentResult(code, txnRef)
    }
}