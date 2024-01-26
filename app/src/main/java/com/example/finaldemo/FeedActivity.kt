package com.example.finaldemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FeedActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedAdapter
    private lateinit var swipe: BottomNavigationItemView
    private lateinit var profile: BottomNavigationItemView
    private lateinit var addNew: FloatingActionButton

    // Firebase Realtime Database reference
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        swipe = findViewById(R.id.navigation_swipe)
        profile = findViewById(R.id.navigation_profile)
        addNew = findViewById(R.id.addNewButton)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FeedAdapter(emptyList())
        recyclerView.adapter = adapter
        swipe.setOnClickListener{
            val intent = Intent(this, SwipeActivity::class.java)
            startActivity(intent)
            finish()
        }
        profile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        addNew.setOnClickListener{
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Load posts from Firebase Realtime Database
        database.child("posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                adapter.updatePosts(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to load posts from Firebase", error.toException())
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.likedEvents -> {
                val intent = Intent(this, LikedEventsActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.calendar -> {
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        private const val TAG = "FeedActivity"
    }
}
