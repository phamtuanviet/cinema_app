package com.example.myapplication.presentation.screen.promotion.promotion_list

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.ErrorView
import com.example.myapplication.presentation.component.LoadingView
import com.example.myapplication.presentation.component.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionListScreen(
    onClickDetail: (String) -> Unit,
    viewModel: PromotionListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val tabs = listOf(
        "VOUCHER" to "Khuyến mãi",
        "NORMAL" to "Tin tức"
    )
    val selectedTabIndex = tabs.indexOfFirst { it.first == state.selectedType }.coerceAtLeast(0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ưu đãi & Sự kiện",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
                }
            ) {
                tabs.forEachIndexed { index, (type, title) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.changeType(type) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContentColor = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = state.selectedType,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "TabContentAnimation"
                ) { targetType ->

                    key(targetType) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                        ) {
                            itemsIndexed(
                                items = state.posts,
                                key = { _, post -> post.id }
                            ) { index, post ->
                                PostItem(
                                    post = post,
                                    onClickDetail = { onClickDetail(post.id) },
                                    modifier = Modifier.animateItem()
                                )

                                if (index == state.posts.lastIndex && !state.isLoading && !state.isLoadingMore) {
                                    LaunchedEffect(key1 = index) {
                                        viewModel.loadMore()
                                    }
                                }
                            }

                            if (state.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                LoadingView(
                    isVisible = state.isLoading,
                    modifier = Modifier.align(Alignment.Center)
                )

                ErrorView(
                    errorMsg = state.error,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}