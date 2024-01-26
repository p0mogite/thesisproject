package com.example.finaldemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LikedEventsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LikedEventsAdapter
    private lateinit var swipe: BottomNavigationItemView
    private lateinit var profile: BottomNavigationItemView
    private lateinit var feed: BottomNavigationItemView
    private val database = FirebaseDatabase.getInstance().reference.child("liked_events")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_events)
        swipe = findViewById(R.id.navigation_swipe)
        profile = findViewById(R.id.navigation_profile)
        feed = findViewById(R.id.navigation_feed)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LikedEventsAdapter(emptyList())
        recyclerView.adapter = adapter
        feed.setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
        swipe.setOnClickListener {
            val intent = Intent(this, SwipeActivity::class.java)
            startActivity(intent)
            finish()
        }
        profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val likedEvents = snapshot.children.mapNotNull { it.getValue(LikedEvent::class.java) }
                adapter.updateLikedEvents(likedEvents)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
data class LikedEvent(
    val locationName: String = "",
    val locationDate: String = ""
)

