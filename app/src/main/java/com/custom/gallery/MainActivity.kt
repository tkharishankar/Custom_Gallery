package com.custom.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.custom.gallery.ui.screen.DetailScreen
import com.custom.gallery.ui.screen.GalleryScreen
import com.custom.gallery.ui.screen.HomeScreen
import com.custom.gallery.ui.theme.CustomGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomGalleryTheme {
                GalleryApp()
            }
        }
    }

    @Composable
    private fun GalleryApp() {
        val navController = rememberNavController()
        NavHost(
            navController,
            startDestination = "home"
        ) {
            composable("home") { HomeScreen(navController) }
            composable("gallery") { GalleryScreen(navController) }
            composable("detail") { DetailScreen(navController) }
        }
    }
}



