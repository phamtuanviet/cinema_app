package com.example.myapplication.presentation.navigation.graph

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myapplication.presentation.app.AppState
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.navigation.route.AuthRoute
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.OnboardingRoute
import com.example.myapplication.presentation.navigation.route.RootRoute
import com.example.myapplication.presentation.screen.admin.main.AdminMainScreen
import com.example.myapplication.presentation.screen.splash.SplashScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    appState: AppState
) {

    NavHost(
        navController = navController,
        startDestination = RootRoute.Splash.route
    ) {

        // Splash
        composable(RootRoute.Splash.route) {
            val context = LocalContext.current

            SplashScreen(
                appState = appState,
                onNavigateToOnboarding = { navController.navigate(RootRoute.OnboardingGraph.route) },
                onNavigateToAuth = { navController.navigate(RootRoute.AuthGraph.route) },
                onNavigateToMain = { navController.navigate(RootRoute.MainGraph.route) },
                onNavigateToAdmin = { navController.navigate(RootRoute.AdminGraph.route) },
                onUserBanned = {
                    // Thông báo cho người dùng
                    Toast.makeText(context, "Phiên đăng nhập hết hạn hoặc tài khoản bị khóa!", Toast.LENGTH_LONG).show()

                    // Đá về màn Auth, xóa sạch backstack để không ấn nút Back quay lại Splash được
                    navController.navigate(RootRoute.AuthGraph.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(RootRoute.AdminGraph.route) {
            AdminMainScreen(rootNavController = navController)
        }

        // Onboarding
        navigation(
            route = RootRoute.OnboardingGraph.route,
            startDestination = OnboardingRoute.Welcome.route
        ) {
            onboardingNavGraph(navController)
        }

        // Auth
        navigation(
            route = RootRoute.AuthGraph.route,
            startDestination = AuthRoute.Login.route
        ) {
            authNavGraph(navController)
        }

        // Main
        navigation(
            route = RootRoute.MainGraph.route,
            startDestination = MainRoute.BottomGraph.route
        ) {
            mainNavGraph(navController)
        }


    }

}