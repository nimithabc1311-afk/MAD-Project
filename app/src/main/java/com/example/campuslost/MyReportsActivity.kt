package com.example.campuslost

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reports)

        val container = findViewById<LinearLayout>(R.id.containerItems)
        val studentEmail = ItemStorage.getStudentEmail(this)

        var count = 0

        val processArray = { array: org.json.JSONArray, typeLabel: String ->
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val email = item.optString("email")
                if (studentEmail.isNotEmpty() && email.equals(studentEmail, ignoreCase = true)) {
                    count++
                    val card = TextView(this).apply {
                        val name = item.optString("name")
                        val desc = item.optString("description")
                        val loc = item.optString("location")
                        val date = item.optString("date")

                        text = "[$typeLabel] $name\n📍 Location: $loc\n📅 Date: $date\n📝 $desc"
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

        processArray(ItemStorage.getLostItems(this), "Lost")
        processArray(ItemStorage.getFoundItems(this), "Found")

        if (count == 0) {
            val emptyTv = TextView(this).apply {
                text = if (studentEmail.isEmpty()) {
                    "Please set your student email in My Profile to view your reports."
                } else {
                    "No reports submitted under $studentEmail."
                }
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            container.addView(emptyTv)
        }
    }
}
