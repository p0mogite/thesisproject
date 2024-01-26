@file:Suppress("DEPRECATION")

package com.example.finaldemo

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameEt: TextInputEditText
    private lateinit var usernameEt: TextInputEditText
    private lateinit var saveBtn: Button
    private lateinit var editAvatarBtn: Button
    private lateinit var avatar: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var Fdatabase: FirebaseDatabase
    private lateinit var swipe: BottomNavigationItemView
    private lateinit var feed: BottomNavigationItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        swipe = findViewById(R.id.navigation_swipe)
        feed = findViewById(R.id.navigation_feed)

        nameEt = findViewById(R.id.name_et)
        usernameEt = findViewById(R.id.username_et)
        saveBtn = findViewById(R.id.save_btn)
        editAvatarBtn = findViewById(R.id.edit_avatar_btn)
        avatar = findViewById(R.id.avatar)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        Fdatabase = FirebaseDatabase.getInstance()

        swipe.setOnClickListener{
            val intent = Intent(this, SwipeActivity::class.java)
            startActivity(intent)
            finish()
        }
        feed.setOnClickListener{
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = database.child("users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value?.toString()
                    val username = snapshot.child("username").value?.toString()
                    if (name != null) nameEt.setText(name)
                    if (username != null) usernameEt.setText(username)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }

        saveBtn.setOnClickListener {
            val name = nameEt.text.toString()
            val username = usernameEt.text.toString()
            if (userId != null) {
                val userRef = database.child("users").child(userId)
                userRef.child("name").setValue(name)
                userRef.child("username").setValue(username)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        editAvatarBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }
    }
    companion object {
        private const val IMAGE_PICK_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
            avatar.setImageURI(imageUri)

                // Get a reference to the Firebase storage location where the image will be stored
                val userId = auth.currentUser?.uid ?: return
                val storageRef = storage.reference.child("avatars/$userId.jpg")

                // Upload the image to Firebase storage
                val uploadTask = storageRef.putFile(imageUri)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Get the URL of the uploaded image
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Save the URL of the uploaded image to Firebase Realtime Database
                        val userRef = Fdatabase.reference.child("users/$userId")
                        val avatarUrlMap = HashMap<String, Any>()
                        avatarUrlMap["avatarUrl"] = uri.toString()
                        userRef.updateChildren(avatarUrlMap)
                    }
                }
            }
        }
    }
}
