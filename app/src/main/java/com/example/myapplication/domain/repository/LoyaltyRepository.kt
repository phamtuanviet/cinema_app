package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.LoyaltyAccountResponse
import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse
import retrofit2.http.GET

interface LoyaltyRepository {

    suspend fun getLoyaltyAccount(): Result<LoyaltyAccountResponse>

    suspend fun getLoyaltyTransactions(): Result<List<LoyaltyTransactionResponse>>
}
