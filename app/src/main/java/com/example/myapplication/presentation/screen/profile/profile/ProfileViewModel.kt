package com.example.myapplication.presentation.screen.profile.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            sessionManager.userFlow.collect { user ->
                _state.value = _state.value.copy(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {

            val refresh = sessionManager.getRefreshToken()
            val access = sessionManager.getAccessToken()

            Log.d("ProfileViewModel", "refreshToken = $refresh")
            Log.d("ProfileViewModel", "accessToken = $access")

            _state.value = _state.value.copy(
                isLoading = true
            )
            val result = authRepository.logout(refresh ?: "")
            Log.d("ProfileViewModel", "result = $result")

            if(result) {
                _state.value = _state.value.copy(
                    isLoggedOut = true
                )
            }

            _state.value = _state.value.copy(
                isLoading = false
            )
        }
    }
}