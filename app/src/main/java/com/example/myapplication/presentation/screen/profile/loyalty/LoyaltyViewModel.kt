package com.example.myapplication.presentation.screen.profile.loyalty


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.LoyaltyRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject
@HiltViewModel
class LoyaltyViewModel @Inject constructor(
    private val loyaltyRepository: LoyaltyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoyaltyState())
    val state: StateFlow<LoyaltyState> = _state

    init {
        loadLoyalty()
    }

    private fun loadLoyalty() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Gọi song song hoặc tuần tự tùy logic của bạn, ở đây giữ nguyên logic cũ
            val accountResult = loyaltyRepository.getLoyaltyAccount()
            val transactionResult = loyaltyRepository.getLoyaltyTransactions()

            accountResult.onSuccess { account ->
                transactionResult.onSuccess { transactions ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loyaltyPoint = account.availablePoints,
                            transactions = transactions
                        )
                    }
                }.onFailure {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Load loyalty transactions failed"
                        )
                    }
                }
            }.onFailure {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Load loyalty transactions failed"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}