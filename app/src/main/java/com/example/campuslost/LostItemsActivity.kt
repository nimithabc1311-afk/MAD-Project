package com.example.campuslost

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LostItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_items)

        val container = findViewById<LinearLayout>(R.id.containerItems)
        val items = ItemStorage.getLostItems(this)

        if (items.length() == 0) {
            val emptyTv = TextView(this).apply {
                text = "No lost items reported yet."
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            container.addView(emptyTv)
        } else {
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val name = item.optString("name")
                val desc = item.optString("description")
                val loc = item.optString("location")
                val date = item.optString("date")
                val email = item.optString("email")
                val imgPath = item.optString("imagePath")

                val card = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(32, 24, 32, 24)
                    setBackgroundColor(0xFFFFFFFF.toInt())
                }

                if (imgPath.isNotEmpty()) {
                    val bmp = ImageUtils.loadScaledBitmap(imgPath, 300)
                    if (bmp != null) {
                        val imgView = ImageView(this).apply {
                            setImageBitmap(bmp)
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                300
                            ).apply {
                                setMargins(0, 0, 0, 16)
                            }
                        }
                        card.addView(imgView)
                    }
                }

                val detailsTv = TextView(this).apply {
                    text = "📦 $name\n📍 Location: $loc\n📅 Date: $date\n📝 $desc\n✉️ Contact: $email"
                    textSize = 15f
                }
                card.addView(detailsTv)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 20)
                }
                container.addView(card, params)
            }
        }
    }
}
