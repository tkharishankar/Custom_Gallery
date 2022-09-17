package com.custom.gallery.uistate

import com.custom.gallery.viewmodel.model.MediaFile

/**
 * Author: Hari K
 * Date: 16/09/2022.
 */
data class FileUIState(
    val files: List<MediaFile> = emptyList(),
)