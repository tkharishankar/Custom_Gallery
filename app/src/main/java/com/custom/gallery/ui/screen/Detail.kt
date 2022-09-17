package com.custom.gallery.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.custom.gallery.ui.components.TopBar
import com.custom.gallery.viewmodel.GalleryViewModel

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun DetailScreen(
    navController: NavHostController,
    viewModel: GalleryViewModel,
    bucketId: String,
    displayName: String
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    LaunchedEffect("") {
        viewModel.start()
        viewModel.getFiles(context, bucketId, displayName)
    }

    DisposableEffect("") {
        onDispose { viewModel.stop() }
    }

    Scaffold(
        topBar = {
            TopBar(displayName) {
                navController.navigateUp()
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = rememberImagePainter(viewModel.fileUIState.files[index].uri),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                            )
                            if (viewModel.fileUIState.files[index].mediaType == 3) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
