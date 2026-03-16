package com.example.myapplication.presentation.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun PointsSection(
    availablePoints: Int,
    usedPoints: Int,
    onChange: (Int) -> Unit
) {

    Column {

        Text("Điểm BETA")

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { if (usedPoints > 0) onChange(usedPoints - 1000) }
            ) {
                Icon(Icons.Default.Remove, null)
            }

            Text("$usedPoints")

            IconButton(
                onClick = {
                    if (usedPoints + 1000 <= availablePoints)
                        onChange(usedPoints + 1000)
                }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    }
}