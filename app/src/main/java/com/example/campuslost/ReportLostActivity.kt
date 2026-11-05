package com.example.campuslost

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ReportLostActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            findViewById<ImageView>(R.id.ivSelectedImage).apply {
                setImageURI(uri)
                visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_lost)

        val itemName = findViewById<EditText>(R.id.etItemName)
        val description = findViewById<EditText>(R.id.etDescription)
        val location = findViewById<EditText>(R.id.etLocation)
        val date = findViewById<EditText>(R.id.etDate)
        val submitButton = findViewById<Button>(R.id.btnSubmitLost)
        val selectImageButton = findViewById<Button>(R.id.btnSelectImage)

        selectImageButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        submitButton.setOnClickListener {
            val name = itemName.text.toString().trim()
            val desc = description.text.toString().trim()
            val loc = location.text.toString().trim()
            val itemDate = date.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || loc.isEmpty() || itemDate.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var imagePath: String? = null
            selectedImageUri?.let { uri ->
                imagePath = ItemStorage.saveImageToInternalStorage(this, uri)
            }

            ItemStorage.addLostItem(
                this,
                name,
                desc,
                loc,
                itemDate,
                ItemStorage.getStudentEmail(this).ifEmpty { "Not set" },
                imagePath
            )

            Toast.makeText(this, "Lost item reported successfully!", Toast.LENGTH_LONG).show()

            itemName.text.clear()
            description.text.clear()
            location.text.clear()
            date.text.clear()

            finish()
        }
    }
}
