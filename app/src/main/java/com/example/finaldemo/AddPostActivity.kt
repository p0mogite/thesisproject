package com.example.finaldemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Suppress("DEPRECATION")
class AddPostActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    private lateinit var postTextView: EditText
    private lateinit var addPictureButton: Button
    private lateinit var pictureImageView: ImageView
    private lateinit var locationTextView: EditText
    private lateinit var saveButton: Button

    private val database = FirebaseDatabase.getInstance().reference

    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        ratingBar = findViewById(R.id.ratingBar)
        postTextView = findViewById(R.id.editText_post)
        addPictureButton = findViewById(R.id.button_upload_image)
        pictureImageView = findViewById(R.id.pictureImageView)
        locationTextView = findViewById(R.id.editText_location)
        saveButton = findViewById(R.id.button_post)

        addPictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        saveButton.setOnClickListener {
            val post = Post(
                rating = ratingBar.rating,
                postText = postTextView.text.toString(),
                pictureUrl = "",
                location = locationTextView.text.toString()
            )
            val key = database.child("posts").push().key
            if (key != null) {
                val pictureRef = storage.child("images/${UUID.randomUUID()}")
                pictureRef.putFile(Uri.parse(pictureUrl))
                    .addOnSuccessListener {
                        pictureRef.downloadUrl.addOnSuccessListener { uri ->
                            post.pictureUrl = uri.toString()
                            database.child("posts").child(key).setValue(post)
                            setResult(Activity.RESULT_OK)
                            val intent = Intent(this, FeedActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                pictureImageView.setImageURI(selectedImageUri)
                pictureUrl = selectedImageUri.toString()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE = 100
        private var pictureUrl: String? = null
    }
}

data class Post(
    val rating: Float = 0f,
    val postText: String = "",
    var pictureUrl: String? = null,
    val location: String = ""
)
