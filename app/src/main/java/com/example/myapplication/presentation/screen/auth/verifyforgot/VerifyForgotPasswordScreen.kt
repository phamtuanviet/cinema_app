package com.example.myapplication.presentation.screen.auth.verifyforgot


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyForgotPasswordScreen(
    email: String,
    onVerifySuccess: (resetToken: String) -> Unit,
    viewModel: VerifyForgotPasswordViewModel = hiltViewModel(),
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

    val snackbarHostState = remember { SnackbarHostState() }

    val focusManager = LocalFocusManager.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Xác thực email",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                }
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Mã xác nhận",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hãy nhập 6 số được gửi đến $email"
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.otp,
                onValueChange = { viewModel.onOtpChange(it) },
                label = { Text("Mã OTP") },
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
                    Text("Xác nhận")
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
}