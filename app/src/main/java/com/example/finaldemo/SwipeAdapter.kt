package com.example.finaldemo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class SwipeAdapter(var events: List<Event>) : RecyclerView.Adapter<SwipeAdapter.ViewHolder>() {
    internal var selectedEventPosition = RecyclerView.NO_POSITION // initially no item is selected

    fun setSelectedEventPosition(position: Int) {
        selectedEventPosition = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val motionLayout: MotionLayout = itemView.findViewById(R.id.motionLayout)
        private val cardOne: MaterialCardView = itemView.findViewById(R.id.cardOne)
        private val cardTwo: MaterialCardView = itemView.findViewById(R.id.cardTwo)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val locationPicture: ImageView = itemView.findViewById(R.id.LocationPicture)
        init {
            itemView.setOnClickListener {
                // Update the selected event position and notify item changed
                val previousSelectedEventPosition = selectedEventPosition
                selectedEventPosition = adapterPosition
                notifyItemChanged(previousSelectedEventPosition)
                notifyItemChanged(selectedEventPosition)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(event: Event) {
            name.text = event.location
            date.text = event.date
            price.text = event.price
            description.text = event.description

            Glide.with(itemView.context).load(event.pictureUrl).into(locationPicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.swipe_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
        holder.itemView.setOnClickListener {
            // Update the selected event position and notify item changed
            val previousSelectedEventPosition = selectedEventPosition
            selectedEventPosition = holder.adapterPosition
            notifyItemChanged(previousSelectedEventPosition)
            notifyItemChanged(selectedEventPosition)

            // Get the current selected event
        }

        // Set the selected state of the current item
        holder.itemView.isSelected = selectedEventPosition == position
    }

    override fun getItemCount() = events.size

    fun updateEvents(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    fun getCurrentEvent(): Event? {
        return if (selectedEventPosition != RecyclerView.NO_POSITION) {
            events.getOrNull(selectedEventPosition)
        } else {
            null
        }
    }
}
