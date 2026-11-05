package com.example.campuslost

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object ItemStorage {

    private const val PREF_NAME = "CampusLostData"
    private const val LOST_KEY = "lost_items"
    private const val FOUND_KEY = "found_items"
    private const val USER_KEY = "student_email"
    private const val NAME_KEY = "student_name"
    private const val USN_KEY = "student_usn"

    // Save lost item
    fun addLostItem(
        context: Context,
        itemName: String,
        description: String,
        location: String,
        date: String,
        email: String,
        imagePath: String? = null
    ) {
        val preferences = context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        )

        val items = getLostItems(context)

        val item = JSONObject().apply {
            put("id", System.currentTimeMillis().toString())
            put("name", itemName)
            put("description", description)
            put("location", location)
            put("date", date)
            put("email", email)
            put("type", "Lost")
            put("imagePath", imagePath ?: "")
        }

        items.put(item)

        preferences.edit()
            .putString(LOST_KEY, items.toString())
            .apply()
    }

    // Get all lost items
    fun getLostItems(context: Context): JSONArray {
        val preferences = context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        )

        val json = preferences.getString(LOST_KEY, "[]")

        return try {
            JSONArray(json)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    // Save found item
    fun addFoundItem(
        context: Context,
        itemName: String,
        description: String,
        location: String,
        date: String,
        email: String,
        imagePath: String? = null
    ) {
        val preferences = context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        )

        val items = getFoundItems(context)

        val item = JSONObject().apply {
            put("id", System.currentTimeMillis().toString())
            put("name", itemName)
            put("description", description)
            put("location", location)
            put("date", date)
            put("email", email)
            put("type", "Found")
            put("imagePath", imagePath ?: "")
        }

        items.put(item)

        preferences.edit()
            .putString(FOUND_KEY, items.toString())
            .apply()
    }

    // Get all found items
    fun getFoundItems(context: Context): JSONArray {
        val preferences = context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        )

        val json = preferences.getString(FOUND_KEY, "[]")

        return try {
            JSONArray(json)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    // Save complete student profile
    fun saveStudentProfile(context: Context, name: String, usn: String, email: String) {
        context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        ).edit()
            .putString(NAME_KEY, name)
            .putString(USN_KEY, usn)
            .putString(USER_KEY, email)
            .apply()
    }

    // Get saved student name
    fun getStudentName(context: Context): String {
        return context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        ).getString(NAME_KEY, "") ?: ""
    }

    // Get saved student USN
    fun getStudentUsn(context: Context): String {
        return context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        ).getString(USN_KEY, "") ?: ""
    }

    // Save student email
    fun saveStudentEmail(context: Context, email: String) {
        context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        ).edit()
            .putString(USER_KEY, email)
            .apply()
    }

    // Get saved student email
    fun getStudentEmail(context: Context): String {
        return context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        ).getString(USER_KEY, "") ?: ""
    }

    // Copies image from Uri to internal app storage and returns file path
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "item_images")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val destFile = File(dir, "img_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
