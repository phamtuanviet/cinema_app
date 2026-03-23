package com.example.myapplication.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
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
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        // Thêm một chút đổ bóng để thanh BottomBar tách biệt hẳn khỏi nội dung màn hình
        modifier = Modifier.shadow(
            elevation = 16.dp,
            spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        items.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.route } == true

            // Đổi icon dựa trên trạng thái (chọn: Filled, không chọn: Outlined)
            val iconToDisplay = if (selected) item.selectedIcon else item.unselectedIcon

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    iconToDisplay?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        // In đậm chữ khi tab được chọn
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                // ĐÂY LÀ ĐIỂM ĂN TIỀN: Ẩn chữ của các tab không được chọn
                alwaysShowLabel = false,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}