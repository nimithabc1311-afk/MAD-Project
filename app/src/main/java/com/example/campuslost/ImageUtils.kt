package com.example.campuslost

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import java.io.File
import kotlin.math.sqrt

object ImageUtils {

    data class MatchResult(
        val scorePercent: Int,
        val isPotentialMatch: Boolean,
        val matchLevel: String
    )

    /**
     * Loads a Bitmap from file path scaled down to specified target dimensions.
     */
    fun loadScaledBitmap(filePath: String, targetSize: Int = 128): Bitmap? {
        val file = File(filePath)
        if (!file.exists()) return null

        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)

            val width = options.outWidth
            val height = options.outHeight
            if (width <= 0 || height <= 0) return null

            var inSampleSize = 1
            while (width / inSampleSize > targetSize || height / inSampleSize > targetSize) {
                inSampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }

            val decoded = BitmapFactory.decodeFile(filePath, decodeOptions) ?: return null
            Bitmap.createScaledBitmap(decoded, targetSize, targetSize, true)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Computes a normalized feature vector for a bitmap combining HSV color distribution
     * and spatial grid lightness features.
     */
    private fun extractFeatureVector(bitmap: Bitmap): FloatArray {
        val width = bitmap.width
        val height = bitmap.height

        // 16 Hue bins, 8 Saturation bins, 8 Value bins -> 32 color bins + 64 grid cells (8x8)
        val numColorBins = 32
        val gridSize = 8
        val vector = FloatArray(numColorBins + (gridSize * gridSize))

        val hsv = FloatArray(3)
        val cellW = width / gridSize
        val cellH = height / gridSize

        var totalPixels = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                Color.colorToHSV(pixel, hsv)

                val hue = hsv[0]        // 0..360
                val sat = hsv[1]        // 0..1
                val valVal = hsv[2]     // 0..1

                // Map to color bins
                val hueBin = ((hue / 360f) * 16).toInt().coerceIn(0, 15)
                val satBin = (sat * 8).toInt().coerceIn(0, 7)
                val valBin = (valVal * 8).toInt().coerceIn(0, 7)

                vector[hueBin] += 1.0f
                vector[16 + (satBin / 2)] += 1.0f
                vector[24 + (valBin / 2)] += 1.0f

                totalPixels++
            }
        }

        // Normalize color distribution
        if (totalPixels > 0) {
            for (i in 0 until numColorBins) {
                vector[i] /= totalPixels.toFloat()
            }
        }

        // Spatial grid lightness features
        var gridIndex = numColorBins
        for (gy in 0 until gridSize) {
            for (gx in 0 until gridSize) {
                var cellValSum = 0f
                var count = 0

                val startX = gx * cellW
                val startY = gy * cellH
                val endX = (startX + cellW).coerceAtMost(width)
                val endY = (startY + cellH).coerceAtMost(height)

                for (cy in startY until endY) {
                    for (cx in startX until endX) {
                        val pixel = bitmap.getPixel(cx, cy)
                        Color.colorToHSV(pixel, hsv)
                        cellValSum += hsv[2]
                        count++
                    }
                }

                vector[gridIndex++] = if (count > 0) cellValSum / count else 0f
            }
        }

        return vector
    }

    /**
     * Calculates Cosine Similarity between two feature vectors.
     */
    private fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f

        for (i in v1.indices) {
            dotProduct += v1[i] * v2[i]
            normA += v1[i] * v1[i]
            normB += v2[i] * v2[i]
        }

        if (normA == 0f || normB == 0f) return 0f
        return dotProduct / (sqrt(normA) * sqrt(normB))
    }

    /**
     * Calculates AI visual similarity score between two item images on device.
     */
    fun calculateImageSimilarity(imagePath1: String, imagePath2: String): MatchResult {
        if (imagePath1.isEmpty() || imagePath2.isEmpty()) {
            return MatchResult(0, false, "No image provided")
        }

        val bmp1 = loadScaledBitmap(imagePath1)
        val bmp2 = loadScaledBitmap(imagePath2)

        if (bmp1 == null || bmp2 == null) {
            return MatchResult(0, false, "Unable to load image")
        }

        val vec1 = extractFeatureVector(bmp1)
        val vec2 = extractFeatureVector(bmp2)

        val sim = cosineSimilarity(vec1, vec2)
        val scorePercent = (sim * 100).toInt().coerceIn(0, 100)

        val isPotentialMatch = scorePercent >= 60
        val matchLevel = when {
            scorePercent >= 80 -> "High Visual Match"
            scorePercent >= 60 -> "Moderate Visual Match"
            scorePercent >= 40 -> "Low Match"
            else -> "Unlikely Match"
        }

        return MatchResult(scorePercent, isPotentialMatch, matchLevel)
    }
}
