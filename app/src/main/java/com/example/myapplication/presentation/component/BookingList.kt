package com.example.myapplication.presentation.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.myapplication.data.remote.dto.BookingMyBookingDto

@Composable
fun BookingList(bookings: List<BookingMyBookingDto>) {
    LazyColumn {
        items(bookings) { booking ->
            Text(booking.movie.title)
        }
    }
}