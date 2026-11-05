package com.example.campuslost

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val query = intent.getStringExtra("query")?.trim() ?: ""
        val container = findViewById<LinearLayout>(R.id.containerSearchResults)

        val tvHeader = findViewById<TextView>(R.id.tvSearchHeader)
        tvHeader.text = if (query.isEmpty()) "Search Results" else "Search Results for \"$query\""

        var matchCount = 0

        val searchInArray = { array: org.json.JSONArray, category: String ->
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val name = item.optString("name")
                val desc = item.optString("description")
                val loc = item.optString("location")

                if (query.isEmpty() ||
                    name.contains(query, ignoreCase = true) ||
                    desc.contains(query, ignoreCase = true) ||
                    loc.contains(query, ignoreCase = true)
                ) {
                    matchCount++
                    val card = TextView(this).apply {
                        val date = item.optString("date")
                        val email = item.optString("email")

                        text = "[$category] $name\n📍 Location: $loc\n📅 Date: $date\n📝 $desc\n✉️ Contact: $email"
                        textSize = 15f
                        setPadding(32, 24, 32, 24)
                        setBackgroundColor(0xFFFFFFFF.toInt())
                    }
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 16)
                    }
                    container.addView(card, params)
                }
            }
        }

        searchInArray(ItemStorage.getLostItems(this), "Lost")
        searchInArray(ItemStorage.getFoundItems(this), "Found")

        if (matchCount == 0) {
            val emptyTv = TextView(this).apply {
                text = "No matching items found."
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            container.addView(emptyTv)
        }
    }
}
