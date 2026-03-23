package com.example.myapplication.presentation.screen.profile.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Navigate khi logout
    if (state.isLoggedOut) {
        LaunchedEffect(Unit) {
            Log.d("ProfileScreen", "Logout success")
            onLogoutSuccess()
        }
    }

    if (state.isLoading) {
        Text("Loading...")
        return
    }

    Column {
        Text("Email: ${state.user?.email}")
        Text("Name: ${state.user?.fullName}")

        Button(onClick = { viewModel.onLogoutClick() }) {
            Text("Logout")
        }
    }
}