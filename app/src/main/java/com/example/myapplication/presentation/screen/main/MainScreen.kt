package com.example.myapplication.presentation.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.component.BottomBar
import com.example.myapplication.presentation.navigation.graph.bookingNavGraph
import com.example.myapplication.presentation.navigation.graph.cinemaNavGraph
import com.example.myapplication.presentation.navigation.graph.movieNavGraph
import com.example.myapplication.presentation.navigation.graph.profileNavGraph
import com.example.myapplication.presentation.navigation.graph.promotionNavGraph
import com.example.myapplication.presentation.navigation.graph.voucherNavGraph
import com.example.myapplication.presentation.navigation.route.CinemaRoute
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.MovieRoute
import com.example.myapplication.presentation.navigation.route.ProfileRoute
import com.example.myapplication.presentation.navigation.route.PromotionRoute
import com.example.myapplication.presentation.navigation.route.VoucherRoute
import com.example.myapplication.presentation.screen.chatbot.ChatScreen

@Composable
fun MainScreen(rootNavController: NavHostController) {

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val items = listOf(
        MainRoute.MovieGraph,
        MainRoute.CinemaGraph,
        MainRoute.VoucherGraph,
        MainRoute.PromotionGraph,
        MainRoute.ProfileGraph
    )

    val bottomBarRoutes = listOf(
        MovieRoute.MovieList.route,
        CinemaRoute.CinemaList.route,
        VoucherRoute.VoucherList.route,
        PromotionRoute.PromotionList.route,
        ProfileRoute.Profile.route
    )

    val chatbotRoute = "chatbot_screen"

    // Xác định xem có đang ở màn hình hiển thị Bottom Bar không
    val isMainTab = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (isMainTab) {
                BottomBar(navController, items)
            }
        },
        // THÊM FLOATING ACTION BUTTON VÀO ĐÂY
        floatingActionButton = {
            if (isMainTab) { // Chỉ hiện nút Chat khi đang ở các màn hình chính
                FloatingActionButton(
                    onClick = {
                        // Dùng rootNavController để điều hướng đè lên trên MainScreen
                        navController.navigate("chatbot_screen")
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat, // Nhớ import androidx.compose.material.icons.filled.Chat
                        contentDescription = "Chatbot Trợ lý"
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = MainRoute.MovieGraph.route,
            modifier = Modifier.padding(padding) // Padding này tự động chừa chỗ cho BottomBar và FAB
        ) {

            composable(chatbotRoute) {
                ChatScreen(
                    internalNavController = navController,
                    rootNavController = rootNavController
                )
            }

            movieNavGraph(navController, rootNavController)
            cinemaNavGraph(navController, rootNavController)
            voucherNavGraph(navController, rootNavController)
            promotionNavGraph(navController, rootNavController)
            profileNavGraph(navController, rootNavController)
            bookingNavGraph(navController, rootNavController)
        }
    }
}