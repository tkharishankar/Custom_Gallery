package com.custom.gallery.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */

@Composable
fun BasicText(text: String, color: Color) {
    Text(text = text, color = color)
}