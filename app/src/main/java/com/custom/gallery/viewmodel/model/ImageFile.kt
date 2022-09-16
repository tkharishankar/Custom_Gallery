package com.custom.gallery.viewmodel.model

import android.net.Uri

/**
 * Author: Hari K
 * Date: 16/09/2022.
 */
data class MediaFile(
    val id: Long,
    val displayName: String,
    val uri: Uri,
    val size: Long,
    val mimeType: String?,
)