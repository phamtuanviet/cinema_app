package com.example.myapplication.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    fun onFullNameChange(value: String) {
        _state.value = _state.value.copy(fullName = value)
    }

    fun onPhoneChange(value: String) {
        _state.value = _state.value.copy(phone = value)
    }

    fun register() {
        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            try {

                repository.register(
                    email = _state.value.email,
                    password = _state.value.password,
                    fullName = _state.value.fullName,
                    phone = _state.value.phone
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }

        }
    }
}