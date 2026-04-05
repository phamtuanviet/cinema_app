package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.screen.chatbot.ChatScreen
import com.example.myapplication.presentation.screen.main.MainScreen

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {

    composable(MainRoute.BottomGraph.route) {
        MainScreen(navController)
    }


}