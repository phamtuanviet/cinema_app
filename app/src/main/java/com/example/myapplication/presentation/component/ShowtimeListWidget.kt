package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.ShowtimeChatbotResponse

@Composable
fun ShowtimeListWidget(
    showtimes: List<ShowtimeChatbotResponse>,
    onSelect: (String, String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Suất chiếu khả dụng:", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        showtimes.forEach { showtime ->
            Button(
                onClick = { onSelect(showtime.showtimeId.toString(), showtime.movieId.toString()) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${showtime.cinemaName}")
                    Text("${showtime.startTime}")
                }
            }
        }
    }
}