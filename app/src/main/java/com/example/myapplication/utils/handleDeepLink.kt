package com.example.myapplication.utils

import android.content.Intent
import android.util.Log
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.app.AppViewModel

fun handleDeepLink(
    intent: Intent,
    appViewModel: AppViewModel
) {

    val action = intent.getStringExtra("action")
    val bookingId = intent.getStringExtra("bookingId")

    Log.d("DEBUG_APP", "Action nhận được: $action, BookingID: $bookingId")

    if (action == "OPEN_BOOKING_DETAIL" && bookingId != null) {
        Log.d("DEEP_LINK", "Bắt được thông báo! Mở vé số: $bookingId")
        appViewModel.setDeepLinkNavigationRoute("ticket_detail/$bookingId")
        return // Xử lý xong Push thì return để không chạy xuống dưới nữa
    }

    val uri = intent.data ?: return


    if (uri.scheme == "myapp" && uri.host == "payment-result") {
        Log.d("handleDeepLink", "handleDeepLink: $uri")

        val code = uri.getQueryParameter("vnp_ResponseCode")
        val txnRef = uri.getQueryParameter("vnp_TxnRef")

        appViewModel.onPaymentResult(code, txnRef)
    }
}