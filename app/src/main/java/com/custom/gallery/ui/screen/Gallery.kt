
package com.custom.gallery.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.custom.gallery.ui.components.BasicText
import com.custom.gallery.ui.components.TopBar
import com.custom.gallery.viewmodel.GalleryViewModel

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun GalleryScreen(navController: NavHostController, viewModel: GalleryViewModel) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect("") {
        viewModel.start()
        viewModel.getBucketAll()
    }

    DisposableEffect("") {
        onDispose { viewModel.stop() }
    }

    Scaffold(
        topBar = {
            TopBar("Gallery") {
            }
        },
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.Top
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(viewModel.bucketUIState.buckets.size) { index ->
                    Card(
                        backgroundColor = Color.White,
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        elevation = 8.dp,
                    ) {
                        Image(
                            painter = rememberImagePainter(viewModel.bucketUIState.buckets[index].bucketUri),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .clickable {
                                    navController
                                        .navigate(
                                            "detail/${viewModel.bucketUIState.buckets[index].bucketId}" +
                                                    "/${viewModel.bucketUIState.buckets[index].displayName}" +
                                                    "/${viewModel.bucketUIState.buckets[index].mediaType}"
                                        )
                                }
                        )
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentWidth()
                                .clip(RectangleShape)
                                .background(color = Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicText(
                                text = viewModel.bucketUIState.buckets[index].displayName,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}




