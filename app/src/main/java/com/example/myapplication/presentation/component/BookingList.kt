package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.BookingMyBookingDto

@Composable
fun BookingList(
    bookings: List<BookingMyBookingDto>,
    onNavigateToDetail: (String) -> Unit
) {
    if (bookings.isEmpty()) {
        Text(
            text = "Bạn chưa có vé nào ở mục này.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bookings, key = { it.id }) { booking ->
            BookingItemCard(
                booking = booking,
                onClick = onNavigateToDetail
            )
        }
    }
}