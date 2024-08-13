package com.difawitsqard.korupstop

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class ResizeTransformation(private val targetWidth: Int, private val targetHeight: Int) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("ResizeTransformation".toByteArray())
        messageDigest.update(targetWidth.toString().toByteArray())
        messageDigest.update(targetHeight.toString().toByteArray())
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        val scaleFactor = Math.min(targetWidth.toFloat() / width, targetHeight.toFloat() / height)
        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(toTransform, newWidth, newHeight, true)
    }
}
