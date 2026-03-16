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

@Composable
fun ComboSection(
    combos: List<ComboDto>,
    selectedCombos: Map<String, Int>,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit
) {

    Column {

        Text(
            text = "COMBO ƯU ĐÃI LỚN",
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        combos.forEach { combo ->

            val quantity = selectedCombos[combo.id] ?: 0

            ComboItem(
                combo = combo,
                quantity = quantity,
                onIncrease = { onIncrease(combo.id) },
                onDecrease = { onDecrease(combo.id) }
            )
        }
    }
}