package com.example.myapplication.presentation.screen.profile.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileNotificationScreen (
    viewModel: ProfileNotificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}