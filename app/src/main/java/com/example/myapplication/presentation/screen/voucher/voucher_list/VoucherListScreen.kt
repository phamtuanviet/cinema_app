package com.example.myapplication.presentation.screen.voucher.voucher_list


import android.widget.Toast
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

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
    val context = LocalContext.current

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mã giảm giá", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Nền Primary
                    titleContentColor = MaterialTheme.colorScheme.onPrimary // Chữ trắng
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            VoucherTab(state, viewModel)
        }
    }
}