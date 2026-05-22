package com.example.myapplication.presentation.screen.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.presentation.app.AppState
import com.example.myapplication.presentation.app.AppViewModel

@Composable
fun SplashScreen(
    appState: AppState,
    appViewModel: AppViewModel = hiltViewModel(), // 🔥 Nhúng AppViewModel vào đây để gọi hàm verify
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onUserBanned: () -> Unit // Callback xử lý báo lỗi / Toast khi bị đá ra
) {

    // Chỉ chạy effect khi trạng thái loading thay đổi (từ true -> false)
    LaunchedEffect(appState.isLoading) {
        if (appState.isLoading) {
            return@LaunchedEffect
        }

        Log.d("SplashScreen", "isLoading=${appState.isLoading}, isLoggedIn=${appState.isLoggedIn}, hasOnboarded=${appState.hasOnboarded}")

        if (!appState.hasOnboarded) {
            onNavigateToOnboarding()
        }
        else if (!appState.isLoggedIn) {
            onNavigateToAuth()
        }
        else {
            // 🔥 CÓ TOKEN -> KIỂM TRA SỐNG VỚI SERVER TRƯỚC KHI CHO VÀO
            Log.d("SplashScreen", "Đang xác thực trạng thái tài khoản với Server...")

            appViewModel.verifyUserStatusAndNavigate(
                onSuccess = { role ->
                    Log.d("SplashScreen", "Xác thực thành công! Role: $role")
                    if (role == "ADMIN") {
                        onNavigateToAdmin()
                    } else {
                        onNavigateToMain()
                    }
                },
                onBannedOrError = {
                    Log.w("SplashScreen", "Tài khoản bị khóa hoặc lỗi xác thực!")
                    onUserBanned()
                }
            )
        }
    }

    // Giao diện tĩnh của Splash Screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.anh_logo),
            contentDescription = "App Logo"
        )
    }
}