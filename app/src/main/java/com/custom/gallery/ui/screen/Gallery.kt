package com.custom.gallery.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
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
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Grid", "List")
    var selectedIndex by remember { mutableStateOf(0) }

    LaunchedEffect("") {
        viewModel.start()
        viewModel.getBuckets(context)
    }

    DisposableEffect("") {
        onDispose { viewModel.stop() }
    }

    Scaffold(
        topBar = {
            TopBar("Gallery", onNavigationClick = {
                navController.navigateUp()
            }, onActionClick = {
                expanded = true
            })
        },
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        Color.White
                    )
            ) {
                items.forEachIndexed { index, src ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                    }) {
                        BasicText(text = src, color = Color.Black)
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.Top
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (selectedIndex == 0) 2 else 1)
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
                                painter = rememberAsyncImagePainter(viewModel.fileUIState.files[index].uri),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        navController
                                            .navigate(
                                                "detail/${viewModel.fileUIState.files[index].id}" +
                                                        "/${viewModel.fileUIState.files[index].displayName}"
                                            )
                                    }
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
                        val gradientGrayWhite =
                            Brush.verticalGradient(0f to Color.Black, 1000f to Color.Transparent)
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .clip(RectangleShape)
                                .background(gradientGrayWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicText(
                                modifier = Modifier.padding(8.dp),
                                text = viewModel.fileUIState.files[index].displayName + "(" + viewModel.fileUIState.files[index].count + ")",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }


}




