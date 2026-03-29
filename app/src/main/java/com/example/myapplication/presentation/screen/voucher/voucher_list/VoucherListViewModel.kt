package com.example.myapplication.presentation.screen.voucher.voucher_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.VoucherStatus
import com.example.myapplication.domain.repository.LoyaltyRepository
import com.example.myapplication.domain.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherListViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository,
    private val loyaltyRepository: LoyaltyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VoucherListState())
    val state: StateFlow<VoucherListState> = _state

    init {
        loadVouchers(VoucherStatus.AVAILABLE)
        loadLoyalty()
    }

    fun onMainTabChange(index: Int) {
        _state.update { it.copy(selectedMainTab = index) }

        if (index == 0) {
            loadVouchers(_state.value.selectedVoucherTab)
        } else {
            loadLoyalty()
        }
    }

    fun onVoucherTabChange(status: VoucherStatus) {
        _state.update { it.copy(selectedVoucherTab = status) }
        loadVouchers(status)
    }

    private fun loadVouchers(status: VoucherStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            voucherRepository.getVouchers(status)
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            vouchers = data
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    private fun loadLoyalty() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

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

    fun addVoucher(code: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAddingVoucher = true) }

            voucherRepository.addVoucher(code)
                .onSuccess {
                    loadVouchers(_state.value.selectedVoucherTab)
                    _state.update { it.copy(isAddingVoucher = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isAddingVoucher = false,
                            error = e.message
                        )
                    }
                }
        }
    }
}