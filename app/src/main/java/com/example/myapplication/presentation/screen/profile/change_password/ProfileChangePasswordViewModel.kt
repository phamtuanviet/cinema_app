package com.example.myapplication.presentation.screen.profile.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.UserRepository
import com.example.myapplication.presentation.screen.profile.account.ProfileAccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileChangePasswordViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileChangePasswordState())
    val state: StateFlow<ProfileChangePasswordState> = _state

    fun onOldPasswordChange(value: String) {
        _state.value = _state.value.copy(oldPassword = value)
    }

    fun onNewPasswordChange(value: String) {
        _state.value = _state.value.copy(newPassword = value)
    }

    fun changePassword() {
        val currentState = _state.value

        if (currentState.oldPassword.isBlank() || currentState.newPassword.isBlank()) {
            _state.value = currentState.copy(
                isError = true,
                message = "Password cannot be empty"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, isError = false)

            val result = repository.changePassword(
                currentState.oldPassword,
                currentState.newPassword
            )

            result.fold(
                onSuccess = { response ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        isSuccess = true,
                        message = response.message
                    )
                },
                onFailure = {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isError = true,
                        message = it.message ?: "Error"
                    )
                }
            )
        }
    }
}