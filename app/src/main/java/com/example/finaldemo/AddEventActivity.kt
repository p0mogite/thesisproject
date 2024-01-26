package com.example.finaldemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

@Suppress("DEPRECATION")
class AddEventActivity : AppCompatActivity() {

    private lateinit var imageViewEvent: ImageView
    private lateinit var buttonUploadImage: Button
    private lateinit var textInputLayoutLocation: TextInputLayout
    private lateinit var editTextLocation: TextInputEditText
    private lateinit var textInputLayoutDate: TextInputLayout
    private lateinit var editTextDate: TextInputEditText
    private lateinit var textInputLayoutPrice: TextInputLayout
    private lateinit var editTextPrice: TextInputEditText
    private lateinit var textInputLayoutDescription: TextInputLayout
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var buttonSubmit: Button


    private var imageUri: Uri? = null
    private val firebaseStorage = Firebase.storage.reference
    private val firebaseDatabase = Firebase.database.reference.child("events")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        // Initialize views
        imageViewEvent = findViewById(R.id.imageView_event)
        buttonUploadImage = findViewById(R.id.button_upload_image)
        textInputLayoutLocation = findViewById(R.id.textInputLayout_location)
        editTextLocation = findViewById(R.id.editText_location)
        textInputLayoutDate = findViewById(R.id.textInputLayout_date)
        editTextDate = findViewById(R.id.editText_date)
        textInputLayoutPrice = findViewById(R.id.textInputLayout_price)
        editTextPrice = findViewById(R.id.editText_price)
        textInputLayoutDescription = findViewById(R.id.textInputLayout_description)
        editTextDescription = findViewById(R.id.editText_description)
        buttonSubmit = findViewById(R.id.button_submit)

        // Set click listener on Upload Image button
        buttonUploadImage.setOnClickListener {
            chooseImageFromGallery()
        }

        // Set click listener on Submit button
        buttonSubmit.setOnClickListener {
            uploadEventData()
        }
    }


    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            // Get image Uri from gallery
            imageUri = data?.data
            imageViewEvent.setImageURI(imageUri)
        }
    }

    private fun uploadEventData() {
        val location = editTextLocation.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val price = editTextPrice.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        // Check if fields are empty
        if (location.isEmpty() || date.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload image to Firebase storage
        val imageRef = firebaseStorage.child("images/${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Create event object and add to Firebase database
                val eventId = firebaseDatabase.push().key ?: ""
                val event = Event(eventId, location, date, price, description)
                event.pictureUrl = uri.toString()
                firebaseDatabase.child(eventId).setValue(event)
                Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show()
                val intentToSwipe = Intent(this, SwipeActivity::class.java)
                startActivity(intentToSwipe)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 100
    }
}
data class Event(
    val eventId: String = "",
    val location: String = "",
    val date: String = "",
    val price: String = "",
    val description: String = "",
    var pictureUrl: String? = null
)