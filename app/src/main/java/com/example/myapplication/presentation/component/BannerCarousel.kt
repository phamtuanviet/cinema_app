package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun BannerCarousel(
    banners: List<String>
) {

    LazyRow {

        items(banners) { banner ->

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .height(120.dp)
                    .width(300.dp)
            ) {

                AsyncImage(
                    model = banner,
                    contentDescription = null,
                    modifier = Modifier
                        .height(120.dp)
                        .width(300.dp)
                )

            }

        }

    }

}