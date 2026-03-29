package com.example.myapplication.presentation.screen.profile.profile

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.ProfileContent

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit,
    onNavigateAccount: () -> Unit,
    onNavigateBookings: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoggedOut) {
        LaunchedEffect(Unit) {
            onLogoutSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.error != null -> {
                Text(
                    text = state.error ?: "Error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.user != null -> {
                ProfileContent(
                    user = state.user!!,
                    onLogoutClick = {
                        viewModel.onLogoutClick()
                    },
                    onNavigateAccount = onNavigateAccount,
                    onNavigateBookings = onNavigateBookings,
                    onNavigateSettings = onNavigateSettings
                )
            }
        }
    }
}