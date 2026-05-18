package com.example.myapplication.utils

import android.content.Intent
import android.util.Log
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.app.AppViewModel

fun handleDeepLink(
    intent: Intent,
    appViewModel: AppViewModel,
    userRole: String?
) {

    val action = intent.getStringExtra("action")
    val bookingId = intent.getStringExtra("bookingId")
    val movieId = intent.getStringExtra("movieId")

    Log.d("DEBUG_APP", "Action nhận được: $action, BookingID: $bookingId")

    if (action == "OPEN_BOOKING_DETAIL" && !bookingId.isNullOrBlank()) {
        if (userRole == "ADMIN") {
            // Nếu có màn hình chi tiết vé cho Admin thì đổi route ở đây
        } else {
            appViewModel.setDeepLinkNavigationRoute("ticket_detail/$bookingId")
        }
        return
    }

    if (action == "OPEN_MOVIE_DETAIL" && !movieId.isNullOrBlank()) {
        if (userRole == "ADMIN") {

        } else {
            Log.d("DEEP_LINK", "User bấm vào phim. Chuyển đến màn đặt vé.")
            appViewModel.setDeepLinkNavigationRoute("movie_detail/$movieId")
        }
        return
    }

    if (action == "OPEN_NEWS_DETAIL" && !movieId.isNullOrBlank()) {
        if (userRole == "ADMIN") {

        } else {
            Log.d("DEEP_LINK", "User bấm vào phim. Chuyển đến màn post.")
            appViewModel.setDeepLinkNavigationRoute("promotion_detail/$movieId")
        }
        return
    }

    val uri = intent.data ?: return


    if (uri.scheme == "myapp" && uri.host == "payment-result") {
        Log.d("handleDeepLink", "handleDeepLink: $uri")

        val code = uri.getQueryParameter("vnp_ResponseCode")
        val txnRef = uri.getQueryParameter("vnp_TxnRef")

        appViewModel.onPaymentResult(code, txnRef)
    }
}