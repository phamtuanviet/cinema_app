package com.example.myapplication.presentation.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionScreen(
    onAllowPermission: () -> Unit,
    onSkip: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Allow Permissions",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We need some permissions to improve your experience.\n\n• Location: find nearby cinemas\n• Notification: alert new movies",
                textAlign = TextAlign.Center
            )

        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = onAllowPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                Text("Allow Permission")

            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                Text("Skip")

            }

        }

    }

}