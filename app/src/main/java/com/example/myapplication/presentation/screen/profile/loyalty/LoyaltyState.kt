
package com.example.myapplication.presentation.screen.profile.loyalty

import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse

data class LoyaltyState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Thông tin điểm và lịch sử giao dịch
    val loyaltyPoint: Int = 0,
    val transactions: List<LoyaltyTransactionResponse> = emptyList()
)