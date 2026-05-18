package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PointsSection(
    availablePoints: Int,
    usedPoints: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State cục bộ giúp quản lý chữ đang nhập mượt mà, tránh lỗi nhảy con trỏ
    var inputText by remember(usedPoints) {
        mutableStateOf(if (usedPoints == 0) "" else usedPoints.toString())
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🌟 Tiêu đề
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "ĐIỂM CỦA BẠN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 💰 Thông tin điểm hiện có của user
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Điểm của bạn:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$availablePoints điểm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            // 🎛 Ô nhập điểm muốn sử dụng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sử dụng:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Ô nhập liệu (TextField) chuẩn Material 3
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { newValue ->
                        // Xử lý khi người dùng xóa hết (để chuỗi rỗng)
                        if (newValue.isEmpty()) {
                            inputText = ""
                            onChange(0)
                            return@OutlinedTextField
                        }

                        // Lọc chỉ lấy các ký tự là chữ số (chống copy/paste chữ cái)
                        val digitsOnly = newValue.filter { it.isDigit() }
                        val parsedInt = digitsOnly.toIntOrNull()

                        if (parsedInt != null) {
                            if (parsedInt <= availablePoints) {
                                // Nếu số điểm nhập <= điểm hiện có thì cho phép
                                inputText = digitsOnly
                                onChange(parsedInt)
                            } else {
                                // Nếu nhập quá số điểm đang có -> Tự động ép về max điểm
                                inputText = availablePoints.toString()
                                onChange(availablePoints)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, // Mở bàn phím số
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.width(150.dp), // Độ rộng ô nhập
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.End, // Căn phải số liệu cho đẹp
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    suffix = {
                        Text(
                            text = "điểm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}