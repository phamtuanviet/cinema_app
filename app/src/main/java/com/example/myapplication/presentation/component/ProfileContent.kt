package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.UserDto

@Composable
fun ProfileContent(
    user: UserDto,
    onLogoutClick: () -> Unit,
    onNavigateAccount: () -> Unit,
    onNavigateBookings: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ===== User Info (simple) =====
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Menu =====
        ProfileMenuItem(
            title = "Account",
            onClick = onNavigateAccount
        )

        ProfileMenuItem(
            title = "My Bookings",
            onClick = onNavigateBookings
        )

        ProfileMenuItem(
            title = "Settings",
            onClick = onNavigateSettings
        )

        Spacer(modifier = Modifier.weight(1f))

        // ===== Logout =====
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}