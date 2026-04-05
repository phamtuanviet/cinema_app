package com.example.myapplication.presentation.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.presentation.app.AppState
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
@Composable
fun SplashScreen(
    appState : AppState,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit,
) {

    LaunchedEffect(appState.isLoggedIn, appState.hasOnboarded,appState.isLoading) {
        Log.d(
            "APP_STATE",
            "isLoggedIn=${appState.isLoggedIn}, hasOnboarded=${appState.hasOnboarded}"
        )

        if (appState.isLoading) {
            return@LaunchedEffect
        }

        if (!appState.hasOnboarded) {
            Log.d("APP_STATE", "hasOnboarded=${appState.hasOnboarded}")
            onNavigateToOnboarding()
        }
        else if (!appState.isLoggedIn) {
            onNavigateToAuth()
        }
        else {
            onNavigateToMain()
        }

    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo"
        )

    }
}