package com.example.myapplication.presentation.screen.movie.booking


import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.presentation.component.CinemaItem
import com.example.myapplication.presentation.component.DateSelector
import com.example.myapplication.presentation.component.LoadingBox
import com.example.myapplication.presentation.component.MovieBanner
import com.example.myapplication.presentation.component.PermissionUI
import com.example.myapplication.utils.getCurrentLocation
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MovieBookingScreen(
    movieId: String,
    onMovieDetailClick: (String) -> Unit,
    onShowtimeClick: (String, String) -> Unit,
    viewModel: MovieBookingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // 🔥 load data
    LaunchedEffect(movieId) { viewModel.load(movieId) }

    // 🔥 xin quyền
    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    // 🔥 lấy location
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateLocation(lat, lng)
            }
        }
    }

    // 🔥 chưa có permission
    if (!permissionState.status.isGranted) {
        PermissionUI(permissionState, context)
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp), // Tránh bị cắt đuôi khi cuộn
        verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách đều giữa các khối
    ) {

        // 🎬 MOVIE
        item {
            state.movie?.let { movie ->
                MovieBanner(
                    movie = movie,
                    onClick = { onMovieDetailClick(movieId) }
                )
            }
        }

        // 📅 DATE
        item {
            if (state.dates.isNotEmpty()) {
                DateSelector(
                    dates = state.dates,
                    selected = state.selectedDate,
                    isLoading = state.isLoading,
                    onClick = viewModel::selectDate
                )
            }
        }

        // 🔥 LOADING
        if (state.isLoading && state.cinemas.isEmpty()) {
            item { LoadingBox() }
        }

        // 🎥 CINEMAS
        items(state.cinemas) { cinema ->
            CinemaItem(
                cinema = cinema,
                onShowtimeClick = { showtimeId ->
                    onShowtimeClick(showtimeId, movieId)
                }
            )
        }

        state.error?.let { errorMsg ->
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}