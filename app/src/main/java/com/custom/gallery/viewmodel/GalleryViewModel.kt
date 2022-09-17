package com.custom.gallery.viewmodel

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.custom.gallery.uistate.FileUIState
import com.custom.gallery.viewmodel.model.MediaFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _TAG: String = "GalleryViewModel"

    var fileUIState by mutableStateOf(FileUIState())

    fun start() {
        Log.i(_TAG, "Started...")
    }

    fun stop() {
        Log.i(_TAG, "Stopped!")
    }

    fun getBuckets(context: Context) {
        viewModelScope.launch {
            val bucketList = fetchBuckets(context)
            fileUIState = fileUIState.copy(files = bucketList)
        }
    }

    fun getFiles(context: Context, bucketId: String?, selectedMediaType: String) {
        if (bucketId == null)
            return
        viewModelScope.launch {
            val files = fetchBucketFiles(context, bucketId, selectedMediaType)
            fileUIState = fileUIState.copy(files = files)
        }
    }

    private fun fetchBucketFiles(
        context: Context,
        bucketId: String,
        selectedMediaType: String
    ): List<MediaFile> {
        val files = mutableListOf<MediaFile>()

        getFileCursor(context, bucketId, selectedMediaType)?.use { cursor ->
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
                files.add(
                    MediaFile(
                        id, cursor.getString(displayNameColumnIndex),
                        ContentUris.withAppendedId(
                            getUri(selectedMediaType),
                            id
                        ),
                        mediaType,
                    )
                )
            }
        }
        return files
    }


    private fun fetchBuckets(context: Context): List<MediaFile> {
        val fileIdSet = mutableSetOf<String?>()
        val bucketList = mutableListOf<MediaFile>()
        val bucketHashMap: HashMap<String, Int> = HashMap()

        getBucketCursor(context)?.use { cursor ->
            while (cursor.moveToNext()) {
                val fileIdColumn = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID)
                val fileId = cursor.getString(fileIdColumn)
                if (fileId in fileIdSet) {
                    bucketHashMap[fileId] = bucketHashMap[fileId]!! + 1
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
                val fileUri = ContentUris.withAppendedId(
                    getUri(mediaType), id
                )
                bucketList.add(
                    MediaFile(
                        fileId.toLong(), fileDisplayName,
                        fileUri, mediaType
                    )
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

        //to bring these items on top "all image & all video"
        bucketList.addAll(0, tempList)
        return bucketList
    }

    private fun getUri(mediaType: Int) = when (mediaType) {
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ->
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }

    private fun getUri(mediaType: String) = when {
        mediaType.equals("all images", ignoreCase = true) ->
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        mediaType.equals("all videos", ignoreCase = true) ->
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }

    private fun getBucketCursor(context: Context): Cursor? {
        val uri = MediaStore.Files.getContentUri("external")
        val columns = arrayOf(
            BaseColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )
        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArguments = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        val orderBy = MediaStore.MediaColumns.DATE_ADDED

        return context.contentResolver.query(
            uri,
            columns,
            selection,
            selectionArguments,
            "$orderBy DESC"
        )
    }


    private fun getFileCursor(
        context: Context,
        bucketId: String,
        selectedMediaType: String,
    ): Cursor? {
        val uri: Uri
        val columns = arrayOf(
            BaseColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE
        )
        val selection: String
        val selectionArguments: Array<String>
        val orderBy = MediaStore.MediaColumns.DATE_ADDED

        when {
            selectedMediaType.equals("all images", ignoreCase = true) -> {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = ""
                selectionArguments = arrayOf()
            }
            selectedMediaType.equals("all videos", ignoreCase = true) -> {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                selection = ""
                selectionArguments = arrayOf()
            }
            else -> {
                uri = MediaStore.Files.getContentUri("external")
                selection = "${MediaStore.MediaColumns.BUCKET_ID}=?"
                selectionArguments = arrayOf(bucketId)
            }
        }

        return context.contentResolver.query(
            uri,
            columns,
            selection,
            selectionArguments,
            "$orderBy DESC"
        )
    }
}