package com.example.myapplication.presentation.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.presentation.app.AppState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    appState : AppState,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit,
) {

    LaunchedEffect(appState.isLoggedIn, appState.hasOnboarded) {

        if (!appState.hasOnboarded) {
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo"
        )

    }
}