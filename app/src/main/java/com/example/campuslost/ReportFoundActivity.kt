package com.example.campuslost

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ReportFoundActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_report_found)

        val itemName = findViewById<EditText>(R.id.etItemName)
        val description = findViewById<EditText>(R.id.etDescription)
        val location = findViewById<EditText>(R.id.etLocation)
        val date = findViewById<EditText>(R.id.etDate)
        val submitButton = findViewById<Button>(R.id.btnSubmitFound)
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

            ItemStorage.addFoundItem(
                this,
                name,
                desc,
                loc,
                itemDate,
                ItemStorage.getStudentEmail(this).ifEmpty { "Not set" },
                imagePath
            )

            // Check AI visual similarity against lost items
            var bestScore = 0
            var bestMatchLostItem: JSONObject? = null
            var bestMatchResult: ImageUtils.MatchResult? = null

            if (!imagePath.isNullOrEmpty()) {
                val lostItems = ItemStorage.getLostItems(this)
                for (i in 0 until lostItems.length()) {
                    val lost = lostItems.getJSONObject(i)
                    val lostImgPath = lost.optString("imagePath")
                    if (lostImgPath.isNotEmpty()) {
                        val result = ImageUtils.calculateImageSimilarity(imagePath, lostImgPath)
                        if (result.scorePercent > bestScore) {
                            bestScore = result.scorePercent
                            bestMatchLostItem = lost
                            bestMatchResult = result
                        }
                    }
                }
            }

            if (bestMatchLostItem != null && bestMatchResult != null && bestScore >= 50) {
                val lostName = bestMatchLostItem.optString("name")
                val lostLoc = bestMatchLostItem.optString("location")
                val lostEmail = bestMatchLostItem.optString("email")

                AlertDialog.Builder(this)
                    .setTitle("🤖 AI Match Detected! ($bestScore% Match)")
                    .setMessage(
                        "The uploaded photo visually matches a reported Lost Item:\n\n" +
                                "📦 Item: $lostName\n" +
                                "📍 Lost at: $lostLoc\n" +
                                "✉️ Contact: $lostEmail\n\n" +
                                "Match Confidence: ${bestMatchResult.matchLevel}"
                    )
                    .setPositiveButton("View Side-by-Side Match") { _, _ ->
                        val foundItemJson = JSONObject().apply {
                            put("name", name)
                            put("description", desc)
                            put("location", loc)
                            put("date", itemDate)
                            put("email", ItemStorage.getStudentEmail(this@ReportFoundActivity))
                            put("imagePath", imagePath ?: "")
                        }
                        val intent = Intent(this, CompareItemsActivity::class.java).apply {
                            putExtra("lost_json", bestMatchLostItem.toString())
                            putExtra("found_json", foundItemJson.toString())
                            putExtra("score", bestScore)
                        }
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("Done") { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            } else {
                Toast.makeText(this, "Found item reported successfully!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
