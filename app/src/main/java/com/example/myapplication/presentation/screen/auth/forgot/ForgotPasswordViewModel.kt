package com.example.myapplication.presentation.screen.auth.forgot


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(
            email = email,
            error = null
        )
    }

    fun sendOtp() {

        val email = _state.value.email

        if (email.isBlank()) {
            _state.value = _state.value.copy(
                error = "Email cannot be empty"
            )
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {

                repository.forgotPassword(email)

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send OTP"
                )

            }
        }
    }
}