package com.example.myapplication.presentation.screen.auth.reset


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    private var resetToken: String = ""

    fun setResetToken(token: String) {
        resetToken = token
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onConfirmPasswordChange(confirm: String) {
        _state.value = _state.value.copy(confirmPassword = confirm)
    }

    fun resetPassword() {

        val password = _state.value.password
        val confirm = _state.value.confirmPassword

        if (password != confirm) {
            _state.value = _state.value.copy(
                error = "Passwords do not match"
            )
            return
        }

        if (password.length < 6) {
            _state.value = _state.value.copy(
                error = "Password must be at least 6 characters"
            )
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {

                repository.resetPassword(
                    resetToken,
                    password
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Reset password failed"
                )

            }

        }

    }

}