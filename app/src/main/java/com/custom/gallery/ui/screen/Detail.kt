package com.custom.gallery.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.custom.gallery.ui.components.BasicText

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun DetailScreen(navController: NavHostController) {
    Log.i("navController", "DetailScreen")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text = "Detail View", color = Color.Black)
    }
}
