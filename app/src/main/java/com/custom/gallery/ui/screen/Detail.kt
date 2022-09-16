package com.custom.gallery.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.custom.gallery.ui.components.TopBar
import com.custom.gallery.viewmodel.GalleryViewModel

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun DetailScreen(
    viewModel: GalleryViewModel,
    bucketId: String,
    displayName: String,
    mediaType: Int
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect("") {
        viewModel.start()
        viewModel.getFiles(bucketId, mediaType)
    }

    DisposableEffect("") {
        onDispose { viewModel.stop() }
    }

    Scaffold(
        topBar = {
            TopBar(displayName) {}
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
                columns = GridCells.Fixed(4)
            ) {
                items(viewModel.fileUIState.files.size) { index ->
                    Card(
                        backgroundColor = Color.White,
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        elevation = 8.dp,
                    ) {
                        Image(
                            painter = rememberImagePainter(viewModel.fileUIState.files[index].uri),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }
                }
            }
        }
    }
}
