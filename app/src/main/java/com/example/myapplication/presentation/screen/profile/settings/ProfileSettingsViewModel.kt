package com.example.myapplication.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ProfileSettingsViewModel(): ViewModel() {

    private val _state = MutableStateFlow(ProfileSettingsState())
    val state = _state.asStateFlow()
}