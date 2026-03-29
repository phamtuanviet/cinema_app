package com.example.myapplication.presentation.screen.profile.my_tickets

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.remote.enums.BookingTab
import com.example.myapplication.presentation.component.BookingList
import com.example.myapplication.presentation.component.CompletedList

@Composable
fun ProfileMyTicketsScreen(
    viewModel: ProfileMyTicketsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column {
        TabRow(selectedTabIndex = state.selectedTab.ordinal) {
            BookingTab.values().forEach { tab ->
                Tab(
                    selected = state.selectedTab == tab,
                    onClick = { viewModel.selectTab(tab) },
                    text = { Text(tab.name) }
                )
            }
        }

        when (state.selectedTab) {
            BookingTab.UPCOMING -> BookingList(state.upcoming)
            BookingTab.ONGOING -> BookingList(state.ongoing)
            BookingTab.COMPLETED -> CompletedList(
                bookings = state.completed,
                onRate = viewModel::rateMovie,
                loadingIds = state.ratingLoadingIds
            )
        }
    }
}