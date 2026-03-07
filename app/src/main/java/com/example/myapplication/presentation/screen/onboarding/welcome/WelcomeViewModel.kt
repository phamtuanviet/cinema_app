package com.example.myapplication.presentation.screen.onboarding.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val appDataStore: AppDataStore
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            appDataStore.saveOnboarded(true)
        }
    }

}