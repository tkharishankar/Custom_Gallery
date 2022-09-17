package com.custom.gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.custom.gallery.ui.theme.Purple700

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun TopBar(title: String, onNavigationClick: () -> Unit, onActionClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(Icons.Filled.ArrowBack, "backIcon", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(Icons.Filled.List, "list", tint = Color.White)
            }
        },
        title = {
            BasicText(
                text = title,
                color = Color.White,
            )
        },
        backgroundColor = Purple700
    )
}

@Composable
fun TopBar(title: String, onNavigationClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(Icons.Filled.ArrowBack, "backIcon", tint = Color.White)
            }
        },
        title = {
            BasicText(
                text = title,
                color = Color.White,
            )
        },
        backgroundColor = Purple700
    )
}

@Composable
fun Dropdown() {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Grid", "List")
    var selectedIndex by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.Red
                )
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                }) {

                }
            }
        }
    }
}