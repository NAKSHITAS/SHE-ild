package com.example.she_ild

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Step 1: Decode a bitmap from file by first checking its dimensions.
 * This avoids loading a huge bitmap directly into memory.
 *
 * @param filePath The absolute path to the image file.
 * @param reqWidth The required width of the scaled-down bitmap.
 * @param reqHeight The required height of the scaled-down bitmap.
 * @return A scaled-down Bitmap if decoding is successful, null otherwise.
 */
fun decodeBitmapFromFile(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
    // Step 2: Get dimensions of the original image without loading it
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(filePath, options)

    // Step 3: Calculate how much to scale down the image
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    // Step 4: Decode the image file into a smaller bitmap
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(filePath, options)
}

/**
 * Step 3 helper: Calculate a power-of-2 scale factor based on requested dimensions.
 * Keeps memory usage low while still fitting the image in the desired size.
 *
 * @param options BitmapFactory.Options with original dimensions.
 * @param reqWidth Target width.
 * @param reqHeight Target height.
 * @return The sample size (1 = original, 2 = half, 4 = quarter, etc.)
 */
fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Increase the sample size until the decoded dimensions are <= requested size
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}
