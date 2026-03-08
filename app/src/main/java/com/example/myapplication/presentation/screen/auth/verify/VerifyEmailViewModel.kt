package com.example.myapplication.presentation.screen.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VerifyEmailState())
    val state: StateFlow<VerifyEmailState> = _state

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onOtpChange(value: String) {
        _state.value = _state.value.copy(otp = value)
    }

    fun verify() {

        val current = _state.value

        if (current.otp.length != 6) {
            _state.value = current.copy(error = "OTP must be 6 digits")
            return
        }

        viewModelScope.launch {

            _state.value = current.copy(
                isLoading = true,
                error = null
            )

            try {

                val result = repository.verifyEmail(
                    email = current.email,
                    otp = current.otp
                )

                _state.value = current.copy(
                    isLoading = false,
                    isSuccess = result
                )

            } catch (e: Exception) {

                _state.value = current.copy(
                    isLoading = false,
                    error = e.message
                )

            }

        }

    }
}