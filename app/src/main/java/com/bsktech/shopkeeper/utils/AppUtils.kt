package com.bsktech.shopkeeper.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


object AppUtils {
    fun toCamalCase(name: String?): CharSequence? {
        val words = name!!.split(" ").toMutableList()
        var output = ""
        for (word in words) {
            output += word.capitalize() + " "
        }
        output = output.trim()
        return output
    }

    fun flipping(b: Bitmap): Bitmap {
        val bos = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapdata: ByteArray = bos.toByteArray()
        val bs = ByteArrayInputStream(bitmapdata)
        try {
            val exif = ExifInterface(bs)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    return rotateImage(b, 90f)
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    return rotateImage(b, 180f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    return rotateImage(b, 270f)
                }
                ExifInterface.ORIENTATION_NORMAL -> {
                }
                else -> {
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return b
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun getCorrectlyOrientedImage(context: Context, photoUri: Uri): Bitmap? {
        var image: InputStream = context.contentResolver.openInputStream(photoUri)!!
        val dbo = BitmapFactory.Options()
        dbo.inJustDecodeBounds = true
        BitmapFactory.decodeStream(image, null, dbo)
        image.close()
        val rotatedWidth: Int
        val rotatedHeight: Int
        val orientation: Int = getOrientation(context, photoUri)
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight
            rotatedHeight = dbo.outWidth
        } else {
            rotatedWidth = dbo.outWidth
            rotatedHeight = dbo.outHeight
        }
        var srcBitmap: Bitmap?
        image = context.contentResolver.openInputStream(photoUri)!!
        srcBitmap = BitmapFactory.decodeStream(image)
        image.close()

        /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */if (orientation > 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            srcBitmap = Bitmap.createBitmap(
                srcBitmap!!, 0, 0, srcBitmap.width,
                srcBitmap.height, matrix, true
            )
        }
        return srcBitmap
    }

    private fun getOrientation(context: Context, photoUri: Uri?): Int {
        /* it's on the external media. */
        val cursor: Cursor? = context.contentResolver.query(
            photoUri!!,
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
            null,
            null,
            null
        )
        if (cursor!!.count !== 1) {
            return -1
        }
        cursor?.moveToFirst()
        return cursor!!.getInt(0)
    }

}