package com.example.myapplication.presentation.screen.auth.reset

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ResetPasswordScreen(
    resetToken: String,
    onResetSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setResetToken(resetToken)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onResetSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.resetPassword() },
            modifier = Modifier.fillMaxWidth()
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Reset Password")
            }

        }

        state.error?.let {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )

        }

    }

}