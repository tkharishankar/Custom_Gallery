package com.custom.gallery.viewmodel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.custom.gallery.uistate.FileUIState
import com.custom.gallery.viewmodel.model.MediaFile
import kotlinx.coroutines.launch

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _TAG: String = "GalleryViewModel"

    var fileUIState by mutableStateOf(FileUIState())

    fun start() {
        Log.i(_TAG, "Started...")
    }

    fun stop() {
        Log.i(_TAG, "Stopped!")
    }

    fun getFiles(bucketId: String?, selectedMediaType: String) {
        Log.i(
            _TAG, "getFiles.: $bucketId..." +
                    "selectedMediaType.: $selectedMediaType"
        )
        if (bucketId == null)
            return
        val files = mutableListOf<MediaFile>()
        val selection: String
        val selectionArguments: Array<String>
        val uri: Uri
        if (selectedMediaType.equals("all images", ignoreCase = true)) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = ""
            selectionArguments = arrayOf()
        } else if (selectedMediaType.equals("all videos", ignoreCase = true)) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            selection = ""
            selectionArguments = arrayOf()
        } else {
            uri = MediaStore.Files.getContentUri("external")
            selection = "${MediaStore.MediaColumns.BUCKET_ID}=?"
            selectionArguments = arrayOf("$bucketId")
        }
        getApplication<Application>().contentResolver.query(
            uri,
            arrayOf(
                BaseColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE
            ),
            selection, selectionArguments,
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idColumnIndex = cursor.getColumnIndex(BaseColumns._ID)
                val displayNameColumnIndex =
                    cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val mimeTypeColumnIndex =
                    cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)

                val id = cursor.getLong(idColumnIndex)
                val mimeType = cursor.getString(mimeTypeColumnIndex)
                val mediaType: Int = when {
                    mimeType.contains("image") -> 1
                    mimeType.contains("video") -> 3
                    else -> 0
                }
                files += MediaFile(
                    id, cursor.getString(displayNameColumnIndex),
                    ContentUris.withAppendedId(
                        uri,
                        id
                    ),
                    mediaType,
                )
            }
        }
        fileUIState = fileUIState.copy(files = files)
    }

    fun getBuckets() {
        viewModelScope.launch {
            val fileIdSet = mutableSetOf<String?>()
            val bucketList = mutableListOf<MediaFile>()
            val bucketHashMap: HashMap<String, Int> = HashMap()
            getApplication<Application>().contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                arrayOf(
                    BaseColumns._ID,
                    MediaStore.MediaColumns.BUCKET_ID,
                    MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                ),
                "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?",
                arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
                ),
                "${MediaStore.MediaColumns.DATE_ADDED} DESC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val fileIdColumn = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID)
                    val fileId = cursor.getString(fileIdColumn)
                    if (fileId in fileIdSet) {
                        bucketHashMap[fileId] = bucketHashMap[fileId]!! + 1;
                        continue
                    }
                    bucketHashMap[fileId] = 1
                    fileIdSet += fileId
                    val idColumn = cursor.getColumnIndex(BaseColumns._ID)
                    val fileDisplayNameColumn =
                        cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                    val mediaTypeColumn =
                        cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    val id = cursor.getLong(idColumn)
                    val fileDisplayName = cursor.getString(fileDisplayNameColumn)
                    val mediaType = cursor.getInt(mediaTypeColumn)
                    val fileUri = when (mediaType) {
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ->
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                            )
                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->
                            ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                            )
                        else -> ContentUris.withAppendedId(
                            MediaStore.Files.getContentUri("external"),
                            id
                        )
                    }
                    Log.i(
                        _TAG, "item..." + MediaFile(
                            fileId.toLong(), fileDisplayName,
                            fileUri, mediaType
                        )
                    )
                    bucketList += MediaFile(
                        fileId.toLong(), fileDisplayName,
                        fileUri, mediaType
                    )
                }
            }
            //update the count value from hashmap to bucketlist
            bucketList.map { bucket ->
                val count = bucketHashMap[bucket.id.toString()]
                bucket.count = count ?: 0
            }

            //add list items "all image & all video"
            val tempList = mutableListOf<MediaFile>()
            bucketList.groupBy { it.mediaType }.forEach { entry ->
                tempList += MediaFile(
                    entry.value[0].id, when (entry.key) {
                        1 -> "All Images"
                        3 -> "All Videos"
                        else -> "Documents"
                    },
                    entry.value[0].uri, entry.key, entry.value.sumOf { it.count }
                )
            }

            //to bring these item on top "all image & all video"
            bucketList.addAll(0, tempList)
            fileUIState = fileUIState.copy(files = bucketList)
        }
    }
}