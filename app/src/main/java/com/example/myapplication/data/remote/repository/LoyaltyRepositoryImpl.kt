package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.LoyaltyApi
import com.example.myapplication.data.remote.dto.LoyaltyAccountResponse
import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse
import com.example.myapplication.domain.repository.LoyaltyRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class LoyaltyRepositoryImpl @Inject constructor(
    private val loyaltyApi: LoyaltyApi
) : LoyaltyRepository {
    override suspend fun getLoyaltyAccount(): Result<LoyaltyAccountResponse> {
        return safeApiCall {
            loyaltyApi.getLoyaltyAccount()
        }
    }

    override suspend fun getLoyaltyTransactions(): Result<List<LoyaltyTransactionResponse>> {
        return safeApiCall {
            loyaltyApi.getLoyaltyTransactions()
        }
    }
}