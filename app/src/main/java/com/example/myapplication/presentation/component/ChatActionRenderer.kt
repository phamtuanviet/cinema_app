package com.example.myapplication.presentation.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.ChatUiAction

@Composable
fun ChatActionRenderer(
    action: ChatUiAction,
    onMovieClick: (movieId: String) -> Unit,
    onBookTicketClick: (showtimeId: String) -> Unit
) {
    when (action) {
        // TRƯỜNG HỢP HIỂN THỊ DANH SÁCH PHIM
        is ChatUiAction.ShowMovieList -> {
            val movies = action.data.content ?: emptyList()

            // Vẽ một danh sách vuốt ngang (LazyRow)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(movies) { movie ->
                    // Giả sử bạn có 1 component Item Phim riêng biệt
                    Card(
                        modifier = Modifier
                            .width(120.dp)
                            .clickable {
                                // KHI CLICK: Gọi callback và truyền ID phim ra ngoài
                                movie.movieId?.let { id -> onMovieClick(id) }
                            }
                    ) {
                        Column {
                            Text(text = movie.title ?: "Tên phim", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }

        // TRƯỜNG HỢP HIỂN THỊ LỊCH CHIẾU
        is ChatUiAction.ShowShowtimes -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                action.data.forEach { showtime ->
                    Button(
                        onClick = {
                            // KHI CLICK: Chuyển sang luồng đặt vé
                            showtime.showtimeId?.let { id -> onBookTicketClick(id) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đặt vé ${showtime.cinemaName} - ${showtime.startTime}")
                    }
                }
            }
        }

        // ... Các trường hợp khác
        else -> {}
    }
}