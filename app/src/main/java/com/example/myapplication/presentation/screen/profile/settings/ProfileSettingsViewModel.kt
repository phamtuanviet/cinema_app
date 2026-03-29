package com.example.myapplication.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileSettingsState())
    val state = _state.asStateFlow()

    // lấy theme từ DataStore
    val darkThemeFlow = sessionManager.darkThemeFlow

    // =========================
    // =========================
    fun toggleTheme(current: Boolean) {
        viewModelScope.launch {
            try {
                sessionManager.updateDarkTheme(!current)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Toggle theme failed")
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}