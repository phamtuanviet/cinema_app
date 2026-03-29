package com.example.myapplication.data.remote.enums

enum class VoucherStatus {
    AVAILABLE, USED, EXPIRED;

    fun displayName(): String = when(this) {
        AVAILABLE -> "Available"
        USED -> "Used"
        EXPIRED -> "Expired"
    }
}