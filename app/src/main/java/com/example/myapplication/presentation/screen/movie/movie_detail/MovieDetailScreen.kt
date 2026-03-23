package com.example.myapplication.presentation.screen.movie.movie_detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đang tải thông tin phim...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val movie = state.movie ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // 🎬 BANNER + TRAILER
        item {
            if (showTrailer && !playerError) {
                YoutubePlayer(
                    trailerUrl = movie.trailerUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
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

                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    // Ảnh nền banner
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Lớp phủ Gradient đen tối dần từ dưới lên và trên xuống để chữ/icon nổi bật
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Nút Play chuẩn Material
                    FilledIconButton(
                        onClick = { showTrailer = true },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play Trailer",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Title Header
                    Text(
                        text = "Chi tiết phim",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp)
                    )
                }
            }
        }

        // 🖼 POSTER + INFO
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Poster thu nhỏ có bo góc và đổ bóng
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(Modifier.width(16.dp))

                // Thông tin chi tiết
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Tự động tạo khoảng cách
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    InfoRow("THỂ LOẠI", movie.genres.joinToString(", "))
                    InfoRow("THỜI LƯỢNG", "${movie.durationMinutes} phút")
                    InfoRow("NGÔN NGỮ", movie.language)
                    InfoRow("ĐỘ TUỔI", movie.ageRating)
                    InfoRow("KHỞI CHIẾU", movie.releaseDate)
                }
            }
        }

        // 📝 DESCRIPTION
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "NỘI DUNG PHIM",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify,
                    lineHeight = 24.sp
                )
            }
        }
    }
}