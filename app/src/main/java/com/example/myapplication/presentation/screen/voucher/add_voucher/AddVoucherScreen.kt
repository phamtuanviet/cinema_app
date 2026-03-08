package com.example.myapplication.presentation.screen.voucher.add_voucher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddVoucherScreen (
    viewModel: AddVoucherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}