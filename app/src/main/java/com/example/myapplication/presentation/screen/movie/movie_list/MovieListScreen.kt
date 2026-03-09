package com.example.myapplication.presentation.screen.movie.movie_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.BannerCarousel
import com.example.myapplication.presentation.component.MovieItem
import com.example.myapplication.presentation.component.MovieTabs

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onNavigateBooking: (String) -> Unit
) {

    val state by viewModel.state.collectAsState()

    Column {

        BannerCarousel(state.banners)

        MovieTabs(
            selectedTab = state.selectedTab,
            onTabSelected = viewModel::changeTab
        )

        when {

            state.isLoading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            }

            state.error != null -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error!!)
                }

            }

            else -> {

                val movies = when (state.selectedTab) {
                    MovieTab.NOW_SHOWING -> state.nowShowing
                    MovieTab.COMING_SOON -> state.comingSoon
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp)
                ) {

                    items(movies) { movie ->

                        MovieItem(
                            movie = movie,
                            onClick = {
                                onNavigateBooking(movie.id)
                            }
                        )

                    }

                }

            }

        }

    }
}