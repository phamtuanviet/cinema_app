package com.example.myapplication.presentation.screen.voucher.add_voucher

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AddVoucherViewModel(): ViewModel() {

    private val _state = MutableStateFlow(AddVoucherState())
    val state = _state.asStateFlow()
}