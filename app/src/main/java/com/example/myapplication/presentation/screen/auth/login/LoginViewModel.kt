package com.example.myapplication.presentation.screen.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password : String) {
        _state.value = _state.value.copy(password = password)
    }


    fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true,error = null)
            try {

                val result = repository.login(
                    email = _state.value.email,
                    password = _state.value.password
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null,
                    isSuccess = result
                )
            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }

        }
    }

}