package com.example.campuslost

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val etName = findViewById<EditText>(R.id.etStudentName)
        val etUsn = findViewById<EditText>(R.id.etStudentUsn)
        val etEmail = findViewById<EditText>(R.id.etStudentEmail)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        etName.setText(ItemStorage.getStudentName(this))
        etUsn.setText(ItemStorage.getStudentUsn(this))
        etEmail.setText(ItemStorage.getStudentEmail(this))

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val usn = etUsn.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isEmpty() || usn.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all profile fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ItemStorage.saveStudentProfile(this, name, usn, email)
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
