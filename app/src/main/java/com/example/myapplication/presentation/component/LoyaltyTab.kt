package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.presentation.screen.voucher.voucher_list.VoucherListState



@Composable
fun LoyaltyTab(state: VoucherListState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- POINT ---
        Text(
            text = "Điểm của bạn: ${state.loyaltyPoint}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- TRANSACTION LIST ---
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có giao dịch nào")
            }
        } else {
            LazyColumn {
                items(state.transactions) { transaction ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(transaction.description, style = MaterialTheme.typography.bodyLarge)

                            Text(
                                text = if (transaction.points > 0) "+${transaction.points}" else "${transaction.points}",
                                color = if (transaction.points > 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            transaction.movieTitle?.let { Text("Phim: $it") }
                            transaction.cinemaName?.let { Text("Rạp: $it") }
                            transaction.showtime?.let { Text("Suất chiếu: $it") }

                            Text(
                                text = transaction.createdAt,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}