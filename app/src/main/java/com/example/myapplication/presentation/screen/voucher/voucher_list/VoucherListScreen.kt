package com.example.myapplication.presentation.screen.voucher.voucher_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VoucherListScreen (
    viewModel: VoucherListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}