package com.example.myapplication.presentation.screen.cinema.cinema_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.auth.forgot.ForgotPasswordViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutViewModel

@Composable
fun CinemaDetailScreen (
    viewModel: CinemaDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}