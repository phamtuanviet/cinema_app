package com.example.myapplication.presentation.component

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.myapplication.presentation.screen.movie.movie_list.MovieTab

@Composable
fun MovieTabs(

    selectedTab: MovieTab,
    onTabSelected: (MovieTab) -> Unit

) {

    TabRow(
        selectedTabIndex = if (selectedTab == MovieTab.NOW_SHOWING) 0 else 1
    ) {

        Tab(
            selected = selectedTab == MovieTab.NOW_SHOWING,
            onClick = { onTabSelected(MovieTab.NOW_SHOWING) },
            text = { Text("ĐANG CHIẾU") }
        )

        Tab(
            selected = selectedTab == MovieTab.COMING_SOON,
            onClick = { onTabSelected(MovieTab.COMING_SOON) },
            text = { Text("SẮP CHIẾU") }
        )

    }

}