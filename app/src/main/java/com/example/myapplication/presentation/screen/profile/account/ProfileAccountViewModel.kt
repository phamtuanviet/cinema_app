package com.example.myapplication.presentation.screen.profile.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileAccountState())
    val state: StateFlow<ProfileAccountState> = _state

    init {
        observeUser()
    }

    // ===== Observe user from SessionManager =====
    private fun observeUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            sessionManager.userFlow.collect { user ->
                _state.value = _state.value.copy(
                    user = user,
                    fullName = user?.fullName ?: "",
                    isLoading = false
                )
            }
        }
    }

    // ===== UI actions =====
    fun onFullNameChange(value: String) {
        _state.value = _state.value.copy(fullName = value)
    }

    fun setAvatar(file: File) {
        _state.value = _state.value.copy(avatarFile = file)
    }

    // ===== Update profile =====
    fun updateProfile() {
        val current = _state.value

        // validate
        if (current.fullName.isBlank() && current.avatarFile == null) {
            _state.value = current.copy(error = "Nothing to update")
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isUpdating = true, error = null)

            val result = userRepository.updateProfile(
                fullName = current.fullName,
                avatarFile = current.avatarFile
            )

            result.onSuccess { userDto ->
                // update session
                sessionManager.saveUser(userDto)

                _state.value = _state.value.copy(
                    user = userDto,
                    isUpdating = false,
                    updateSuccess = true
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isUpdating = false,
                    error = error.message
                )
            }
        }
    }

    fun clearFlags() {
        _state.value = _state.value.copy(
            updateSuccess = false,
            error = null
        )
    }
}