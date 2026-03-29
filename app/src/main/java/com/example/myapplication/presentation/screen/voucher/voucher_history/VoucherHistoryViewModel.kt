package com.example.myapplication.presentation.screen.voucher.voucher_history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VoucherHistoryViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(VoucherHistoryState())
    val state = _state.asStateFlow()
}