package com.example.myapplication.presentation.screen.movie.seat_selection

import SeatGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.AirlineSeatIndividualSuite
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.remote.enums.SeatStatus
import com.example.myapplication.presentation.component.BottomBar
import com.example.myapplication.presentation.component.BottomBarSelection
import com.example.myapplication.presentation.component.CountdownTimer
import com.example.myapplication.presentation.component.MovieHeader
import com.example.myapplication.presentation.component.ScreenIndicator
import com.example.myapplication.presentation.component.SeatLegend
import com.example.myapplication.utils.parseExpireTime

@Composable
fun MovieSeatSelectionScreen(
    showtimeId: String,
    movieId: String,
    onContinueClick: (sessionId : String) -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: MovieSeatSelectionViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    var showExpireDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMovie(movieId)
        viewModel.loadSeatMap(showtimeId)
    }

    if (state.isLoadingSeatMap || state.isLoadingMovie) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
        ) {

            MovieHeader(state)

            SeatLegend()

            ScreenIndicator()

            SeatGrid(
                rows = state.seatMap?.rows ?: emptyList(),
                selectedSeats = state.selectedSeats,
                onSeatClick = {
                    viewModel.toggleSeat(showtimeId, it)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            if(state.expiresAt != null) {
                CountdownTimer(parseExpireTime(state.expiresAt), onExpire = {
                    showExpireDialog = true
                })
            }

            if (state.selectedSeats.isNotEmpty()) {
                BottomBarSelection(
                    totalPrice = state.totalPrice,
                    selectedSeats = state.selectedSeats.toList(),
                    onContinueClick = {
                        state.sessionId?.let { sessionId ->
                            onContinueClick(sessionId)
                        }
                    }                )
            }
        }
        if (showExpireDialog) {

            AlertDialog(
                onDismissRequest = { },

                title = {
                    Text("Session Expired")
                },

                text = {
                    Text(
                        "Your seat hold session has expired. The seats have been released. Please select a new showtime."
                    )
                },

                confirmButton = {

                    Button(
                        onClick = {
                            showExpireDialog = false
                            onSessionExpired()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}