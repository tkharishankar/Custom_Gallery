package com.custom.gallery.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.custom.gallery.ui.theme.Purple700

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun TopBar(title: String, onNavigationClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(Icons.Filled.ArrowBack, "backIcon", tint = Color.White)
            }
        },
        title = {
            BasicText(text = title, color = Color.White)
        },
        backgroundColor = Purple700
    )
}