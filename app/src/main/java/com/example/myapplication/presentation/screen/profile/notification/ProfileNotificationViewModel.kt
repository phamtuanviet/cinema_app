package com.example.myapplication.presentation.screen.profile.notification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ProfileNotificationViewModel(): ViewModel() {

    private val _state = MutableStateFlow(ProfileNotificationState())
    val state = _state.asStateFlow()
}