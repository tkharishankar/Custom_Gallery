package com.custom.gallery.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.custom.gallery.ui.components.BasicButton
import com.custom.gallery.ui.theme.Purple700
import com.custom.gallery.viewmodel.GalleryViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicButton(
            text = "Open Gallery",
            textColor = Color.White,
            bgColor = Purple700,
            modifier = Modifier
        ) {
            if (permissionState.status.isGranted) {
                navController.navigate("gallery")
            } else {
                permissionState.launchPermissionRequest()
            }
        }
    }
}
