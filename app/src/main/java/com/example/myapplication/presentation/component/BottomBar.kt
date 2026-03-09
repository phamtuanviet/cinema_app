package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.presentation.navigation.route.MainRoute

@Composable
fun BottomBar(
    navController: NavHostController,
    items: List<MainRoute>
) {

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        items.forEach { item ->

            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.route } == true

            val color =
                if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) {

                        navController.navigate(item.route) {

                            popUpTo(navController.graph.startDestinationId)

                            launchSingleTop = true
                            restoreState = true

                        }

                    }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item.icon?.let {

                    Icon(
                        imageVector = it,
                        contentDescription = item.title,
                        tint = color
                    )

                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.title,
                    color = color,
                    style = MaterialTheme.typography.labelSmall
                )

            }

        }

    }

}