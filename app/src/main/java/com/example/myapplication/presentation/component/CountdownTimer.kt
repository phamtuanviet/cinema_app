package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@Composable
fun CountdownTimer(
    expiresAt: Long?,
    onExpire: () -> Unit
) {

    var timeLeft by remember { mutableStateOf(0L) }

    LaunchedEffect(expiresAt) {

        val expireTime = expiresAt ?: return@LaunchedEffect

        while (true) {

            val now = System.currentTimeMillis()

            timeLeft = expireTime - now

            if (timeLeft <= 0) {
                onExpire()
                break
            }

            kotlinx.coroutines.delay(1000)
        }
    }

    val seconds = (timeLeft / 1000) % 60
    val minutes = (timeLeft / 1000) / 60

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            fontWeight = FontWeight.Bold
        )
    }
}