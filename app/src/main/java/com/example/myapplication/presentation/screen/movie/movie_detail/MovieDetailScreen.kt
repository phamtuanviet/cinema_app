package com.example.myapplication.presentation.screen.movie.movie_detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.presentation.component.InfoRow
import com.example.myapplication.presentation.component.YoutubePlayer

@Composable
fun MovieDetailScreen(
    movieId: String,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showTrailer by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    if (state.isLoading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val movie = state.movie ?: return

    LazyColumn {

        // Banner + Trailer
        item {

            if (showTrailer && !playerError) {

                YoutubePlayer(
                    trailerUrl = movie.trailerUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    onError = {
                        playerError = true
                    }
                )

            } else {
                if (playerError || showTrailer) {

                    LaunchedEffect(Unit) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(movie.trailerUrl)
                        )
                        context.startActivity(intent)
                        showTrailer = false
                        playerError = false
                    }

                }

                Box {

                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable {
                                showTrailer = true
                            }
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶ Play Trailer")
                    }

                    Text(
                        text = "Chi tiết phim",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }
        }


        // Poster + Info
        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(110.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(16.dp))

                Column {

                    Text(
                        movie.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    InfoRow(
                        "THỂ LOẠI",
                        movie.genres.joinToString(", ")
                    )

                    InfoRow(
                        "THỜI LƯỢNG",
                        "${movie.durationMinutes} phút"
                    )

                    InfoRow(
                        "NGÔN NGỮ",
                        movie.language
                    )

                    InfoRow(
                        "ĐỘ TUỔI",
                        movie.ageRating
                    )

                    InfoRow(
                        "NGÀY KHỞI CHIẾU",
                        movie.releaseDate
                    )
                }
            }
        }

        // Description
        item {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "NỘI DUNG PHIM",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Text(movie.description)
            }
        }
    }
}
