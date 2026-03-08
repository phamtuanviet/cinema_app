package com.example.myapplication.presentation.screen.profile.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ProfileSettingsScreen (
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}