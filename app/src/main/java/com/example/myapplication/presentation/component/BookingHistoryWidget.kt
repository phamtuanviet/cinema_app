package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.BookingHistoryResponse

@Composable
fun BookingHistoryWidget(
    bookings: List<BookingHistoryResponse>,
    onTicketClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Vé của bạn gần đây:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bookings) { ticket ->
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .clickable {
                            // SỬA LỖI: Thêm ?: "" để đảm bảo không truyền null vào callback
                            onTicketClick(ticket.ticketCode ?: "")
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            // SỬA LỖI: Thêm ?: "" cho Text
                            Text(
                                text = ticket.movieTitle ?: "Phim không xác định",
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                        Text(text = "Rạp: ${ticket.cinemaName ?: ""}", style = MaterialTheme.typography.bodySmall)
                        // Nếu startTime là LocalDateTime, cần .toString() hoặc formatter
                        Text(text = "Suất: ${ticket.startTime?.toString() ?: ""}", style = MaterialTheme.typography.bodySmall)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Ghế: ${ticket.seats ?: ""}", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = ticket.statusDisplay ?: "",
                                color = if (ticket.statusDisplay == "Thành công") Color(0xFF4CAF50) else Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}