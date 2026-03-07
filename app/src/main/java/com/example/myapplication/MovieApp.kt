package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.app.AppViewModel
import com.example.myapplication.presentation.navigation.graph.RootNavGraph
import com.example.myapplication.presentation.theme.MovieAppTheme
import androidx.compose.runtime.getValue
import com.example.myapplication.presentation.navigation.route.RootRoute

@Composable
fun MovieApp(
    appViewModel: AppViewModel = hiltViewModel()
) {

    val appState by appViewModel.appState.collectAsState()
    val navController = rememberNavController()

    MovieAppTheme (darkTheme = appState.darkTheme) {
        RootNavGraph(navController = navController,appState)
    }

}