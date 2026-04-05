package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.presentation.navigation.route.AuthRoute
import com.example.myapplication.presentation.navigation.route.RootRoute
import com.example.myapplication.presentation.screen.auth.forgot.ForgotPasswordScreen
import com.example.myapplication.presentation.screen.auth.login.LoginScreen
import com.example.myapplication.presentation.screen.auth.register.RegisterScreen
import com.example.myapplication.presentation.screen.auth.verify.VerifyEmailScreen
import com.example.myapplication.presentation.screen.auth.verifyforgot.VerifyForgotPasswordScreen
import com.example.myapplication.presentation.screen.auth.reset.ResetPasswordScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController
) {


    composable(AuthRoute.Login.route) {

        LoginScreen(

            onLoginSuccess = {
                navController.navigate(RootRoute.MainGraph.route) {
                    popUpTo(RootRoute.AuthGraph.route) { inclusive = true }
                }
            },

            onNavigateRegister = {
                navController.navigate(AuthRoute.Register.route)
            },

            onNavigateForgot = {
                navController.navigate(AuthRoute.ForgotPassword.route)
            }

        )
    }


    composable(AuthRoute.Register.route) {

        RegisterScreen(

            onNavigateVerify = { email ->
                navController.navigate(
                    AuthRoute.Verify.createRoute(email)
                )
            },

            onNavigateLogin = {
                navController.popBackStack()
            }

        )

    }


    composable(
        route = AuthRoute.Verify.route,

        arguments = listOf(
            navArgument("email") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val email =
            backStackEntry.arguments?.getString(AuthRoute.Verify.emailArg) ?: ""

        VerifyEmailScreen(

            email = email,

            onVerifySuccess = {

                navController.navigate(AuthRoute.Login.route) {

                    popUpTo(AuthRoute.Login.route) {
                        inclusive = true
                    }

                }

            }

        )

    }


    composable(AuthRoute.ForgotPassword.route) {

        ForgotPasswordScreen(

            onNavigateVerify = { email ->

                navController.navigate(
                    AuthRoute.VerifyForgotPassword.createRoute(email)
                )

            }

        )

    }


    composable(
        route = AuthRoute.VerifyForgotPassword.route,
        arguments = listOf(
            navArgument("email") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val email =
            backStackEntry.arguments?.getString(
                AuthRoute.VerifyForgotPassword.emailArg
            ) ?: ""

        VerifyForgotPasswordScreen(

            email = email,

            onVerifySuccess = { resetToken ->

                navController.navigate(
                    AuthRoute.ResetPassword.createRoute(resetToken)
                )

            }

        )

    }


    composable(
        route = AuthRoute.ResetPassword.route,
        arguments = listOf(
            navArgument("token") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val token =
            backStackEntry.arguments?.getString(
                AuthRoute.ResetPassword.tokenArg
            ) ?: ""

        ResetPasswordScreen(

            resetToken = token,

            onResetSuccess = {

                navController.navigate(AuthRoute.Login.route) {

                    popUpTo(AuthRoute.Login.route) {
                        inclusive = true
                    }

                }

            }

        )

    }

}