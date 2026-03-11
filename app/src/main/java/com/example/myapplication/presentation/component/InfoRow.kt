package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InfoRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {

        Text(
            text = title,
            modifier = Modifier.width(120.dp),
            fontWeight = FontWeight.Bold
        )

        Text(text = value)
    }
}