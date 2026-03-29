package com.example.myapplication.presentation.screen.profile.change_password

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ProfileChangePasswordScreen(
    viewModel: ProfileChangePasswordViewModel = hiltViewModel(),
    onSuccess : () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = state.oldPassword,
            onValueChange = { viewModel.onOldPasswordChange(it) },
            label = { Text("Old Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.newPassword,
            onValueChange = { viewModel.onNewPasswordChange(it) },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.changePassword() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(if (state.isLoading) "Loading..." else "Change Password")
        }

        if (state.isError) {
            Text(
                text = state.message,
                color = Color.Red
            )
        }
    }
}