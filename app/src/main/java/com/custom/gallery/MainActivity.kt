package com.custom.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.custom.gallery.ui.screen.DetailScreen
import com.custom.gallery.ui.screen.GalleryScreen
import com.custom.gallery.ui.screen.HomeScreen
import com.custom.gallery.ui.theme.CustomGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            composable("gallery") {
                GalleryScreen(navController, hiltViewModel())
            }
            composable("detail" + "/{bucketId}" + "/{displayName}" + "/{mediaType}") { navBackStack ->
                val bucketId = navBackStack.arguments?.getString("bucketId")
                val displayName = navBackStack.arguments?.getString("displayName")
                if (bucketId != null && displayName != null) {
                    DetailScreen(navController, hiltViewModel(), bucketId, displayName)
                }
            }
        }
    }
}



