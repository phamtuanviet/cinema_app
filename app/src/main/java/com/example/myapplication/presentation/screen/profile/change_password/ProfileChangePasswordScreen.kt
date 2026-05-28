package com.example.myapplication.presentation.screen.profile.change_password

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileChangePasswordScreen(
    viewModel: ProfileChangePasswordViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }

    // 🔥 XỬ LÝ TOAST THÔNG BÁO MƯỢT MÀ
    LaunchedEffect(state.isSuccess, state.isError) {
        if (state.isSuccess) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            onSuccess() // Quay về màn hình trước
        }
        if (state.isError && state.message.isNotBlank()) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            viewModel.clearError() // Xóa lỗi ngay để không bị hiện lại
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đổi mật khẩu",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ===== Ô nhập Mật khẩu cũ =====
            OutlinedTextField(
                value = state.oldPassword,
                onValueChange = viewModel::onOldPasswordChange,
                label = { Text("Mật khẩu hiện tại (*)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                trailingIcon = {
                    val image =
                        if (oldPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Ẩn/Hiện mật khẩu")
                    }
                },
                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next // Nhấn Next sẽ nhảy xuống ô dưới
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Ô nhập Mật khẩu mới =====
            val isNewPassTooShort = state.newPassword.isNotEmpty() && state.newPassword.length < 6
            val isSameAsOld =
                state.newPassword.isNotEmpty() && state.newPassword == state.oldPassword

            OutlinedTextField(
                value = state.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                label = { Text("Mật khẩu mới (*)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = isNewPassTooShort || isSameAsOld, // Hiện viền đỏ nếu lỗi
                supportingText = {
                    if (isNewPassTooShort) Text(
                        "Mật khẩu phải có ít nhất 6 ký tự",
                        color = MaterialTheme.colorScheme.error
                    )
                    else if (isSameAsOld) Text(
                        "Mật khẩu mới phải khác mật khẩu hiện tại",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                trailingIcon = {
                    val image =
                        if (newPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Ẩn/Hiện mật khẩu")
                    }
                },
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done // Nhấn Done sẽ cất bàn phím
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ===== Nút Đổi mật khẩu =====
            // Khóa nút nếu chưa nhập đủ hoặc nhập sai định dạng
            val isButtonEnabled = state.oldPassword.isNotBlank() &&
                    state.newPassword.length >= 6 &&
                    state.oldPassword != state.newPassword &&
                    !state.isLoading

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.changePassword()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Cập nhật mật khẩu", fontWeight = FontWeight.Bold)
                }
            }

            // Xóa khối hiển thị state.isError cũ ở đây vì đã dùng Toast!
        }
    }
}