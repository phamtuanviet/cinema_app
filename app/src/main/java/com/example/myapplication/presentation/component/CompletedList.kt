package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.data.remote.dto.BookingMyBookingDto

@Composable
fun CompletedList(
    bookings: List<BookingMyBookingDto>,
    onRate: (String, String, Int) -> Unit,
    loadingIds: Set<String>
) {
    LazyColumn {
        items(bookings) { booking ->
            Column {
                Text(booking.movie.title)

                booking.averageRating?.let {
                    Text("⭐ ${it}")
                }

                if (booking.userRating != null) {
                    Text("You rated: ${booking.userRating}")
                } else {
                    Row {
                        (1..5).forEach { star ->
                            Text(
                                text = "⭐",
                                modifier = Modifier
                                    .clickable {
                                        onRate(booking.id, booking.movie.id, star)
                                    }
                            )
                        }
                    }
                }

                if (loadingIds.contains(booking.id)) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}