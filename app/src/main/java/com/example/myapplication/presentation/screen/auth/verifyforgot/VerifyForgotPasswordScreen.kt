package com.example.myapplication.presentation.screen.auth.verifyforgot


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VerifyForgotPasswordScreen(
    email: String,
    onVerifySuccess: (resetToken: String)  -> Unit,
    viewModel: VerifyForgotPasswordViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEmail(email)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onVerifySuccess(state.resetToken)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Verify Code",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter the 6-digit code sent to $email"
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.otp,
            onValueChange = { viewModel.onOtpChange(it) },
            label = { Text("OTP Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.verifyOtp() },
            modifier = Modifier.fillMaxWidth()
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Verify")
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