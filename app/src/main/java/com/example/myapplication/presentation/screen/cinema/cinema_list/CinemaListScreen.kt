package com.example.myapplication.presentation.screen.cinema.cinema_list

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.CinemaItem
import com.example.myapplication.presentation.component.CinemaItemInList
import com.example.myapplication.presentation.component.PermissionUI
import com.example.myapplication.presentation.component.RegionItem
import com.example.myapplication.utils.getCurrentLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CinemaListScreen(
    viewModel: CinemaListViewModel = hiltViewModel(),
    onCinemaClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(state.lat, state.lng) {
        val lat = state.lat
        val lng = state.lng

        if (lat != null && lng != null) {
            viewModel.loadData(lat, lng)
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateLocation(lat, lng)
            }
        }
    }

    if (!permissionState.status.isGranted) {
        PermissionUI(permissionState, context)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 🔥 Rạp gần bạn
        if (state.nearbyCinemas.isNotEmpty()) {
            Text(
                text = "RẠP GẦN BẠN",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Tự tạo khoảng cách giữa các item
            ) {
                items(state.nearbyCinemas) { cinema ->
                    CinemaItemInList(
                        cinema = cinema,
                        modifier = Modifier.width(260.dp),
                        onClick = { onCinemaClick(cinema.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔥 Chọn rạp theo khu vực
        Text(
            text = "CHỌN RẠP THEO KHU VỰC",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f), // Chiếm hết không gian còn lại
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Khoảng cách giữa các khu vực
        ) {
            items(state.regions) { region ->
                RegionItem(
                    region = region,
                    isExpanded = state.expandedRegion == region.region,
                    cinemas = state.cinemasByRegion[region.region] ?: emptyList(),
                    onClick = {
                        val lat = state.lat
                        val lng = state.lng
                        if (lat != null && lng != null) {
                            viewModel.onRegionClick(region.region, lat, lng)
                        }
                    },
                    onCinemaClick = onCinemaClick
                )
            }
        }
    }
}