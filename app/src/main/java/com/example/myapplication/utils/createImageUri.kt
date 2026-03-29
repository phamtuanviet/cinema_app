package com.example.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun createImageUri(context: Context): Uri {
    val file = File(
        context.cacheDir,
        "IMG_${System.currentTimeMillis()}.jpg"
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")

    val outputStream = FileOutputStream(file)

    inputStream.use { input ->
        outputStream.use { output ->
            input?.copyTo(output)
        }
    }

    return file
}

fun compressImageFile(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

    val maxSize = 1024
    val ratio = Math.min(
        maxSize.toFloat() / bitmap.width,
        maxSize.toFloat() / bitmap.height
    )

    val width = (bitmap.width * ratio).toInt()
    val height = (bitmap.height * ratio).toInt()

    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

    val compressedFile = File(
        file.parent,
        "COMPRESSED_${file.name}"
    )

    val outputStream = FileOutputStream(compressedFile)

    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

    outputStream.flush()
    outputStream.close()

    return compressedFile
}