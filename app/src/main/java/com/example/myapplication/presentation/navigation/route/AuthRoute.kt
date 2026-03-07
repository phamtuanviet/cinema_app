package com.example.myapplication.presentation.navigation.route

sealed class AuthRoute(val route: String) {
    object Login : AuthRoute("login")
    object Register : AuthRoute("register")
    object ForgotPassword : AuthRoute("forgot_password")

    object Verify : AuthRoute("verify/{email}") {
        const val emailArg = "email"
        fun createRoute(email: String): String {
            return "verify/$email"
        }
    }

    object VerifyForgotPassword : AuthRoute("verify_forgot_password/{email}"){
        const val emailArg = "email"
        fun createRoute(email: String): String {
            return "verify_forgot_password/$email"
        }
    }

    object ResetPassword : AuthRoute("reset_password/{token}"){
        const val tokenArg = "token"
        fun createRoute(token: String): String {
            return "reset_password/$token"
        }
    }
}