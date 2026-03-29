package com.example.myapplication.presentation.screen.voucher.voucher_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.LoyaltyTab
import com.example.myapplication.presentation.component.VoucherTab

@Composable
fun VoucherListScreen(
    viewModel: VoucherListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // --- MAIN TAB ---
        TabRow(selectedTabIndex = state.selectedMainTab) {
            Tab(
                selected = state.selectedMainTab == 0,
                onClick = { viewModel.onMainTabChange(0) },
                text = { Text("Voucher") }
            )
            Tab(
                selected = state.selectedMainTab == 1,
                onClick = { viewModel.onMainTabChange(1) },
                text = { Text("Điểm của bạn") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- MAIN TAB CONTENT ---
        when (state.selectedMainTab) {
            0 -> VoucherTab(state, viewModel)
            1 -> LoyaltyTab(state)
        }
    }
}