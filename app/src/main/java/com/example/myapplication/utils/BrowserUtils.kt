package com.example.myapplication.utils

import androidx.browser.customtabs.CustomTabsIntent
import android.net.Uri
import android.content.Context

fun openCustomTab(context: Context, url: String) {

    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()

    customTabsIntent.intent.setPackage("com.android.chrome")

    customTabsIntent.launchUrl(context, Uri.parse(url))
}

fun openPayment(context: Context, paymentUrl: String) {

    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()

    customTabsIntent.launchUrl(
        context,
        Uri.parse(paymentUrl)
    )
}

fun openTab(context: Context, paymentUrl: String) {

    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()

    customTabsIntent.launchUrl(
        context,
        Uri.parse(paymentUrl)
    )
}