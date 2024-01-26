package com.example.finaldemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.finaldemo.RegistrationActivity


data class SwipeModel(
    val cardTop: Event,
    val cardBottom: Event
)

class SwipeViewModel: ViewModel() {
    private val stream = MutableLiveData<SwipeModel>()
    val modelStream: MutableLiveData<SwipeModel>
        get() = stream
    private var data = mutableListOf<Event>()
    private var currentIndex = 0

    init {
        loadData()
    }
    private fun loadData() {
        val database = FirebaseDatabase.getInstance().reference
        database.child("events").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    event?.let {
                        data.add(event)
                    }
                }
                updateCards()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun swipe() {
        currentIndex += 1
        updateCards()
    }
    private fun updateCards() {
        stream.value = SwipeModel(
            cardTop = data.getOrNull(currentIndex % data.size) ?: Event(),
            cardBottom = data.getOrNull((currentIndex + 1) % data.size) ?: Event()
        )

    }
}
class SwipeActivity : AppCompatActivity() {
    private lateinit var adapter: SwipeAdapter
    private lateinit var feed: BottomNavigationItemView
    private lateinit var profile: BottomNavigationItemView
    private lateinit var addEvent: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth

    private fun bindCard(model: SwipeModel) {
        val currentEvent = adapter.getCurrentEvent() // Get the current event before updating the cards
        adapter.updateEvents(listOf(model.cardTop, model.cardBottom))

        // Update the selected event position if the current event is not null
        currentEvent?.let {
            val currentPosition = adapter.events.indexOf(it)
            if (currentPosition != -1) {
                adapter.setSelectedEventPosition(currentPosition)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        auth = FirebaseAuth.getInstance()


        feed = findViewById(R.id.navigation_feed)
        profile = findViewById(R.id.navigation_profile)
        addEvent = findViewById(R.id.addEventButton)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SwipeAdapter(emptyList())
        recyclerView.adapter = adapter

        feed.setOnClickListener {
            startActivity(FeedActivity::class.java)
        }

        profile.setOnClickListener {
            startActivity(ProfileActivity::class.java)
        }

        addEvent.setOnClickListener {
            startActivity(AddEventActivity::class.java)
        }

        val motionLayout: MotionLayout = findViewById(R.id.motionLayout)
        val likeFloating: FloatingActionButton = findViewById(R.id.likeFloating)
        val unlikeFloating: FloatingActionButton = findViewById(R.id.unlikeFloating)

        val viewModel = ViewModelProvider(this)[SwipeViewModel::class.java]
        viewModel.
        modelStream.
        observe(this) {
            bindCard(it)
        }
        motionLayout.setTransitionListener(object: TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.id.offScreenUnlike,
                    R.id.offScreenLike -> {
                        motionLayout.progress = 0f
                        motionLayout.setTransition(R.id.start, R.id.detail)
                        viewModel.swipe()
                    }
                }
            }
        })

        likeFloating.setOnClickListener {
            motionLayout.transitionToState(R.id.like)
            val currentEvent = adapter.getCurrentEvent()
            if (currentEvent != null) {
                val model = viewModel.modelStream.value
                if (model != null) {
                    val selectedCard = if (currentEvent == model.cardTop) model.cardTop else model.cardBottom
                    saveLikedEvent(selectedCard)
                } else {
                }
            } else {
            }
        }

        unlikeFloating.setOnClickListener {
            motionLayout.transitionToState(R.id.unlike)
        }
    }
    private fun saveLikedEvent(event: Event) {

        val user = auth.currentUser
        if (user == null) {
            // User is not authenticated, handle the error or redirect to the authentication screen
            // For example:
            startActivity(RegistrationActivity::class.java)
            return
        }

        val userId = user.uid
        if (event.eventId.isNullOrEmpty()) {
            // Event ID is not available or empty, handle the error or log a message
            return
        }

        val eventRef = FirebaseDatabase.getInstance().reference
            .child("liked_events")
            .child(userId)
            .child(event.eventId)

        eventRef.setValue(event)
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) {
                    "Event saved successfully"
                } else {
                    "Failed to save the event"
                }
                showToast(message)
            }
    }



    // Add this function to your SwipeActivity class
    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
        finish()
    }

}
