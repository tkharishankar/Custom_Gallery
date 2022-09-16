package com.custom.gallery.uistate

import com.custom.gallery.viewmodel.model.Bucket
import com.custom.gallery.viewmodel.model.MediaFile

/**
 * Author: Hari K
 * Date: 16/09/2022.
 */
data class BucketUIState(
    val buckets: List<Bucket> = emptyList(),
)

data class FileUIState(
    val files: List<MediaFile> = emptyList(),
)