package com.custom.gallery.viewmodel.model

import android.net.Uri

/**
 * Author: Hari K
 * Date: 16/09/2022.
 */
data class Bucket(
    val bucketId: Long,
    val displayName: String,
    val bucketUri: Uri,
    val mimeType: String?,
    val mediaType: Int,
    var itemCount: Int = 0,
    val id: Long = -1
)