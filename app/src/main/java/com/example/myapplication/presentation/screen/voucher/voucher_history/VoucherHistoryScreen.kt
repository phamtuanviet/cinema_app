package com.example.myapplication.presentation.screen.voucher.voucher_history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VoucherHistoryScreen (
    viewModel: VoucherHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}