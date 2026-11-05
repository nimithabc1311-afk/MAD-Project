package com.example.campuslost

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class CompareItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_items)

        val lostJsonStr = intent.getStringExtra("lost_json") ?: ""
        val foundJsonStr = intent.getStringExtra("found_json") ?: ""
        val score = intent.getIntExtra("score", 0)

        val tvMatchScore = findViewById<TextView>(R.id.tvMatchScore)
        val ivLostPhoto = findViewById<ImageView>(R.id.ivLostPhoto)
        val ivFoundPhoto = findViewById<ImageView>(R.id.ivFoundPhoto)
        val tvLostDetails = findViewById<TextView>(R.id.tvLostDetails)
        val tvFoundDetails = findViewById<TextView>(R.id.tvFoundDetails)
        val btnClose = findViewById<Button>(R.id.btnCloseComparison)

        tvMatchScore.text = "AI Visual Match Score: $score%\n" + when {
            score >= 80 -> "High Confidence Match"
            score >= 60 -> "Moderate Confidence Match"
            else -> "Low Match Confidence"
        }

        if (score >= 80) {
            tvMatchScore.setBackgroundColor(0xFF168B65.toInt())
        } else if (score >= 60) {
            tvMatchScore.setBackgroundColor(0xFFD69E2E.toInt())
        } else {
            tvMatchScore.setBackgroundColor(0xFFE53E3E.toInt())
        }

        if (lostJsonStr.isNotEmpty()) {
            val lost = JSONObject(lostJsonStr)
            val name = lost.optString("name")
            val loc = lost.optString("location")
            val date = lost.optString("date")
            val email = lost.optString("email")
            val imgPath = lost.optString("imagePath")

            tvLostDetails.text = "📦 LOST ITEM\n\nName: $name\nLocation: $loc\nDate: $date\nEmail: $email"

            if (imgPath.isNotEmpty()) {
                val bmp = ImageUtils.loadScaledBitmap(imgPath, 300)
                if (bmp != null) {
                    ivLostPhoto.setImageBitmap(bmp)
                }
            }
        }

        if (foundJsonStr.isNotEmpty()) {
            val found = JSONObject(foundJsonStr)
            val name = found.optString("name")
            val loc = found.optString("location")
            val date = found.optString("date")
            val email = found.optString("email")
            val imgPath = found.optString("imagePath")

            tvFoundDetails.text = "🔍 FOUND ITEM\n\nName: $name\nLocation: $loc\nDate: $date\nEmail: $email"

            if (imgPath.isNotEmpty()) {
                val bmp = ImageUtils.loadScaledBitmap(imgPath, 300)
                if (bmp != null) {
                    ivFoundPhoto.setImageBitmap(bmp)
                }
            }
        }

        btnClose.setOnClickListener {
            finish()
        }
    }
}
