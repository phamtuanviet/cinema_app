package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.LoyaltyAccountResponse
import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse
import retrofit2.http.GET

interface LoyaltyApi {
    @GET("loyalty/account")
    suspend fun getLoyaltyAccount(): LoyaltyAccountResponse

    @GET("loyalty/transactions")
    suspend fun getLoyaltyTransactions(): List<LoyaltyTransactionResponse>
}