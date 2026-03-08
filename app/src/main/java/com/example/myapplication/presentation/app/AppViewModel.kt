package com.example.myapplication.presentation.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.AppDataStore

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appDataStore: AppDataStore,

) : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState = _appState.asStateFlow()

    init {
        observeAppState()
    }
    private fun observeAppState() {
        viewModelScope.launch {

            combine(
                appDataStore.accessTokenFlow,
                appDataStore.hasOnboardedFlow,
                appDataStore.darkThemeFlow

            ) { token, onboarded, theme ->
                Log.d("AppViewModel", "token = $token")
                Log.d("AppViewModel", "onboarded = $onboarded")
                Log.d("AppViewModel", "darkTheme = $theme")
                AppState(
                    isLoggedIn = !token.isNullOrEmpty(),
                    hasOnboarded = onboarded,
                    darkTheme = theme
                )

            }.collect { state ->
                _appState.value = state.copy(isLoading = false)
            }

        }
    }

}
