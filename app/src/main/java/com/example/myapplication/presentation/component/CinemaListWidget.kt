package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Card


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.CinemaNearbyResponse

@Composable
fun CinemaListWidget(cinemas: List<CinemaNearbyResponse>, onCinemaClick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(cinemas) { cinema ->
            Card(
                modifier = Modifier.width(200.dp).clickable { onCinemaClick(cinema.id.toString()) }
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(cinema.name ?: "", fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(cinema.address ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    Text("Cách đây: ${String.format("%.1f", cinema.distance)} km", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}