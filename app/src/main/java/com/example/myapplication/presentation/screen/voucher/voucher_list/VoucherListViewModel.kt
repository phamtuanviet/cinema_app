package com.example.myapplication.presentation.screen.voucher.voucher_list

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class VoucherListViewModel(): ViewModel() {

    private val _state = MutableStateFlow(VoucherListState())
    val state = _state.asStateFlow()
}