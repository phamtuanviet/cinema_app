package com.example.myapplication.presentation.screen.onboarding.welcome


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.presentation.screen.onboarding.welcome.WelcomeViewModel
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen (
    viewModel: WelcomeViewModel = hiltViewModel(),
    onNavigatePermission: () -> Unit
) {
    val scope = rememberCoroutineScope()
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

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Welcome Image",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to MovieApp",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Discover movies, cinemas, and book tickets easily.",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

        }

        Button(
            onClick = {
                scope.launch {
                    viewModel.completeOnboarding()
                    onNavigatePermission()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {

            Text("Get Started")

        }

    }

}