package com.example.myapplication.presentation.screen.cinema.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.auth.forgot.ForgotPasswordViewModel

@Composable
fun CinemaCheckoutScreen (
    viewModel: CinemaCheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}