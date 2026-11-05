package com.example.campuslost

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btnReportLost)
            .setOnClickListener {
                startActivity(Intent(this, ReportLostActivity::class.java))
            }

        findViewById<Button>(R.id.btnReportFound)
            .setOnClickListener {
                startActivity(Intent(this, ReportFoundActivity::class.java))
            }

        findViewById<Button>(R.id.btnLostItems)
            .setOnClickListener {
                startActivity(Intent(this, LostItemsActivity::class.java))
            }

        findViewById<Button>(R.id.btnFoundItems)
            .setOnClickListener {
                startActivity(Intent(this, FoundItemsActivity::class.java))
            }

        findViewById<Button>(R.id.btnMyReports)
            .setOnClickListener {
                startActivity(Intent(this, MyReportsActivity::class.java))
            }

        findViewById<Button>(R.id.btnMyProfile)
            .setOnClickListener {
                startActivity(Intent(this, MyProfileActivity::class.java))
            }

        findViewById<Button>(R.id.btnSearch)
            .setOnClickListener {
                val query = findViewById<EditText>(
                    R.id.etHomeSearch
                ).text.toString()

                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()
        val name = ItemStorage.getStudentName(this)
        val tvWelcome = findViewById<TextView>(R.id.tvWelcomeHeader)
        tvWelcome.text = if (name.isNotEmpty()) "Hello, $name!" else "Hello, Student!"
    }
}
