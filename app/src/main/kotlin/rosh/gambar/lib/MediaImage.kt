package rosh.gambar.lib

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class MediaImage(private val context: Context) {

    fun getFolder(): List<Folder> {
        val hasil = mutableListOf<Folder>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val indexNama = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val indexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val sudahAda = mutableSetOf<String>()

            while (cursor.moveToNext()) {
                val namaFolder = cursor.getString(indexNama) ?: "Tanpa Folder"
                val idFolder = cursor.getString(indexId) ?: continue

                if (sudahAda.add(idFolder)) {
                    hasil.add(Folder(namaFolder, idFolder))
                }
            }
        }
        return hasil
    }

    fun getFile(folder: Folder): List<Uri> {
        val hasil = mutableListOf<Uri>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(folder.id)

        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val indexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(indexId)
                hasil.add(Uri.withAppendedPath(uri, imageId.toString()))
            }
        }
        return hasil
    }
}
