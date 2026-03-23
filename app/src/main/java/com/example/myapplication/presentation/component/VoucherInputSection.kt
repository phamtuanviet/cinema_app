package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VoucherInputSection(
    voucherInput: String,
    onInputChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Nhập mã giảm giá",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = voucherInput,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ví dụ: GIOVANG50...") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(56.dp) // Để nút Add cao bằng với ô Text Field
            ) {
                Text("Áp dụng", fontWeight = FontWeight.Bold)
            }
        }
    }
}