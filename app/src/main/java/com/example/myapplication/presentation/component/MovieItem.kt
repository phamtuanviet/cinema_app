package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.Movie

@Composable
fun MovieItem(movie: Movie,
              onClick: () -> Unit) {

    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(110.dp)
            .clickable { onClick() }
    ) {

        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        ) {

            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize()
            )

        }

        Text(
            text = movie.title,
            maxLines = 1
        )

        Text(
            text = "${movie.duration} phút",
            style = MaterialTheme.typography.bodySmall
        )

    }

}