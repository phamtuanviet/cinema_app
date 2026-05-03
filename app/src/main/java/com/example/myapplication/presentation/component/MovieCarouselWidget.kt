package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.layout.ContentScale


import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieChatbotResponse

@Composable
fun MovieCarouselWidget(movies: List<MovieChatbotResponse>, onClick: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(movies) { movie ->
            Card(
                modifier = Modifier.width(140.dp).clickable { onClick(movie.movieId.toString()) },
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = null,
                        modifier = Modifier.height(180.dp).fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = movie.title ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}