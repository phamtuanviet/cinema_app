package com.example.myapplication.presentation.screen.voucher.voucher_list


import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.LoyaltyTab
import com.example.myapplication.presentation.component.VoucherTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherListScreen(
    viewModel: VoucherListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ưu đãi & Điểm thưởng", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp) // Vẫn giữ dòng này để chữ không bị tụt xuống dưới nhé
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- MAIN TAB ---
            TabRow(
                selectedTabIndex = state.selectedMainTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = state.selectedMainTab == 0,
                    onClick = { viewModel.onMainTabChange(0) },
                    text = {
                        Text(
                            "Mã giảm giá",
                            fontWeight = if(state.selectedMainTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = state.selectedMainTab == 1,
                    onClick = { viewModel.onMainTabChange(1) },
                    text = {
                        Text(
                            "Điểm thưởng",
                            fontWeight = if(state.selectedMainTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // --- MAIN TAB CONTENT ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (state.selectedMainTab) {
                    0 -> VoucherTab(state, viewModel)
                    1 -> LoyaltyTab(state)
                }
            }
        }
    }
}