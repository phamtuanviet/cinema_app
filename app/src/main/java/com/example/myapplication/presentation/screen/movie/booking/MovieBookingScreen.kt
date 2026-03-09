package com.example.myapplication.presentation.screen.movie.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.setValue
import com.example.myapplication.utils.getCurrentLocation
import com.google.accompanist.permissions.*
import android.provider.Settings


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MovieBookingScreen(
    movieId: String,
    viewModel: MovieBookingViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // hỏi permission mỗi lần mở screen
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // khi được cấp quyền -> lấy location
    LaunchedEffect(locationPermissionState.status) {

        if (locationPermissionState.status.isGranted) {

            getCurrentLocation(context) { lat, lng ->
                viewModel.setLocation(lat, lng)
            }
        }
    }

    // load movie
    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
        viewModel.loadDates(movieId)
    }

    // load cinema khi có date + location
    LaunchedEffect(state.selectedDate, state.lat, state.lng) {

        val date = state.selectedDate
        val lat = state.lat
        val lng = state.lng

        if (date != null && lat != null && lng != null) {
            viewModel.loadCinemas(movieId, date)
        }
    }

    // nếu chưa có permission
    var permissionRequested by remember { mutableStateOf(false) }

    if (!locationPermissionState.status.isGranted) {

        Column {

            Text("Ứng dụng cần quyền vị trí để hiển thị rạp gần bạn")

            when {

                // user từ chối nhưng vẫn có thể hỏi lại
                locationPermissionState.status.shouldShowRationale -> {

                    Button(
                        onClick = {
                            permissionRequested = true
                            locationPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text("Cấp quyền vị trí")
                    }
                }

                // đã request trước đó -> có thể là Don't ask again
                permissionRequested -> {

                    Button(
                        onClick = {

                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )

                            context.startActivity(intent)
                        }
                    ) {
                        Text("Mở cài đặt")
                    }
                }

                // lần đầu
                else -> {

                    Button(
                        onClick = {
                            permissionRequested = true
                            locationPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text("Yêu cầu quyền")
                    }
                }
            }
        }

        return
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        // MOVIE BANNER
        item {

            when {

                state.isLoadingMovie -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.movie != null -> {

                    val movie = state.movie

                    Column {

                        AsyncImage(
                            model = movie!!.posterUrl,
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // DATE SELECTOR
        item {

            when {

                state.isLoadingDates -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.dates.isNotEmpty() -> {

                    LazyRow(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {

                        items(state.dates) { date ->

                            val selected = date == state.selectedDate

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selected) Color.Red
                                        else Color.Gray
                                    )
                                    .clickable(
                                        enabled = !state.isLoadingCinemas
                                    ) {

                                        viewModel.selectDate(date)

                                    }
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                            ) {

                                Text(
                                    text = date,
                                    color = Color.White
                                )

                            }
                        }
                    }
                }
            }
        }

        // CINEMA LIST
        when {

            state.isLoadingCinemas -> {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            else -> {

                items(state.cinemas) { cinema ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "${cinema.cinemaName} (${cinema.distanceKm} km)",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow {

                            items(cinema.showtimes) { showtime ->

                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .border(
                                            1.dp,
                                            Color.Red,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        )
                                ) {

                                    Text(
                                        text = showtime.startTime
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ERROR
        state.error?.let { error ->

            item {

                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}