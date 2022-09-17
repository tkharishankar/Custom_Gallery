package com.custom.gallery.viewmodel

import android.app.Application
import android.content.ContentUris
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.custom.gallery.uistate.BucketUIState
import com.custom.gallery.uistate.FileUIState
import com.custom.gallery.viewmodel.model.Bucket
import com.custom.gallery.viewmodel.model.MediaFile

/**
 * Author: Hari K
 * Date: 15/09/2022.
 */
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _TAG: String = "GalleryViewModel"

    var bucketUIState by mutableStateOf(BucketUIState())
        private set

    var fileUIState by mutableStateOf(FileUIState())
        private set

    fun start() {
        Log.i(_TAG, "Started...")
    }

    fun stop() {
        Log.i(_TAG, "Stopped!")
    }

    fun getFiles(bucketId: String?, mediaType: Int) {
        val files = mutableListOf<MediaFile>()
        if (bucketId == null)
            return
        val uri = when (mediaType) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> {
                MediaStore.Files.getContentUri("external")
            }
        }
        getApplication<Application>().contentResolver.query(
            uri,
            arrayOf(
                BaseColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE
            ),
            "${MediaStore.MediaColumns.BUCKET_ID}=?", arrayOf("$bucketId"),
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idColumnIndex = cursor.getColumnIndex(BaseColumns._ID)
                val displayNameColumnIndex =
                    cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val sizeColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                val mimeTypeColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)

                val id = cursor.getLong(idColumnIndex)
                val size = cursor.getLong(sizeColumnIndex)
                val mimeType = cursor.getString(mimeTypeColumnIndex)

                files += MediaFile(
                    id, cursor.getString(displayNameColumnIndex),
                    ContentUris.withAppendedId(
                        uri,
                        id
                    ),
                    size,
                    mimeType,
                )
            }
        }
        fileUIState = fileUIState.copy(files = files)
    }

    fun getBuckets() {
        val fileIdSet = mutableSetOf<String?>()
        val bucketList = mutableListOf<Bucket>()
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
                    _TAG, "item..." + Bucket(
                        fileId.toLong(), fileDisplayName,
                        fileUri , mediaType
                    )
                )
                bucketList += Bucket(
                    fileId.toLong(), fileDisplayName,
                    fileUri, mediaType
                )
            }
        }

        bucketList.map { bucket ->
            val count = bucketHashMap[bucket.bucketId.toString()]
            bucket.itemCount = count ?: 0
        }

        bucketUIState = bucketUIState.copy(buckets = bucketList)
    }
}