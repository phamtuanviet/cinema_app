package com.example.myapplication.presentation.screen.profile.my_tickets

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ProfileMyTicketsViewModel(): ViewModel() {

    private val _state = MutableStateFlow(ProfileMyTicketsState())
    val state = _state.asStateFlow()
}