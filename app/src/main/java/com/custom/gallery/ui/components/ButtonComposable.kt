package com.custom.gallery.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@Composable
fun BasicButton(
    text: String,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier,
    action: () -> Unit
) {
    Button(
        contentPadding = PaddingValues(8.dp),
        onClick = action,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = bgColor
        )
    ) {
        BasicText(text = text, color = textColor)
    }
}
