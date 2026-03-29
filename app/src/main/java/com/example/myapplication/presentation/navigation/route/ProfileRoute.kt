package com.example.myapplication.presentation.navigation.route


sealed class ProfileRoute(val route: String) {

    // trang cá nhân
    object Profile : ProfileRoute("profile")

    // cài đặt
    object Settings : ProfileRoute("settings")

    object Account : ProfileRoute("account")

    object ChangePassword : ProfileRoute("change_password")

    object MyTickets : ProfileRoute("my_tickets")

}