package com.example.myapplication

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.app.AppViewModel
import com.example.myapplication.presentation.navigation.graph.RootNavGraph
import com.example.myapplication.presentation.theme.MovieAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

import com.example.myapplication.utils.handleDeepLink

@Composable
fun MovieApp(
    appViewModel: AppViewModel = hiltViewModel()
) {

    val appState by appViewModel.appState.collectAsState()
    val navController = rememberNavController()

    val context = LocalContext.current
    val activity = context as? Activity

    // ================== 🔥 HANDLE DEEP LINK (ĐẶT Ở ĐẦU) ==================

    // Khi app mở lần đầu
    LaunchedEffect(activity?.intent) {
        activity?.intent?.let { intent ->
            handleDeepLink(intent, appViewModel)

            // tránh gọi lại nhiều lần
            intent.data = null
        }
    }

    // ================== UI ==================

    MovieAppTheme(darkTheme = appState.darkTheme) {
        RootNavGraph(
            navController = navController,
            appState = appState
        )
    }
}