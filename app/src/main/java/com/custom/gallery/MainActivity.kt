package com.custom.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.custom.gallery.ui.screen.DetailScreen
import com.custom.gallery.ui.screen.GalleryScreen
import com.custom.gallery.ui.screen.HomeScreen
import com.custom.gallery.ui.theme.CustomGalleryTheme
import com.custom.gallery.viewmodel.GalleryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GalleryViewModel by viewModels(
        factoryProducer = {
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
    )

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
            composable("gallery") { GalleryScreen(navController, viewModel) }
            composable("detail" + "/{bucketId}" + "/{displayName}" + "/{mediaType}") { navBackStack ->
                val bucketId = navBackStack.arguments?.getString("bucketId")
                val displayName = navBackStack.arguments?.getString("displayName")
                val mediaType = navBackStack.arguments?.getInt("mediaType")
                if (bucketId != null && displayName != null && mediaType != null) {
                    DetailScreen(viewModel, bucketId, displayName, mediaType)
                }
            }
        }
    }
}



