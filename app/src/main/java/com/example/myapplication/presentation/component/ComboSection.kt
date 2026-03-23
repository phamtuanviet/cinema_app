package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.ComboDto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment


@Composable
fun ComboSection(
    combos: List<ComboDto>,
    selectedCombos: Map<String, Int>,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Ẩn section nếu không có combo nào
    if (combos.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 🍿 Tiêu đề của Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "COMBO ƯU ĐÃI LỚN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Danh sách Combo
            combos.forEachIndexed { index, combo ->
                val quantity = selectedCombos[combo.id] ?: 0

                ComboItem(
                    combo = combo,
                    quantity = quantity,
                    onIncrease = { onIncrease(combo.id) },
                    onDecrease = { onDecrease(combo.id) }
                )

                // Thêm vạch ngăn cách giữa các item (trừ item cuối cùng)
                if (index < combos.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}