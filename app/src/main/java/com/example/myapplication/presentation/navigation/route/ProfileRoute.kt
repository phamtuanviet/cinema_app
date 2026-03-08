package com.example.myapplication.presentation.navigation.route


sealed class ProfileRoute(val route: String) {

    // trang cá nhân
    object Profile : ProfileRoute("profile")

    // cài đặt
    object Settings : ProfileRoute("settings")

    // thông báo
    object Notification : ProfileRoute("notification")

    // vé đã đặt
    object MyTickets : ProfileRoute("my_tickets")

}