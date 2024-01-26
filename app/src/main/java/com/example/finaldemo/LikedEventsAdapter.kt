package com.example.finaldemo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class LikedEventsAdapter(private var likedEvents: List<LikedEvent>) : RecyclerView.Adapter<LikedEventsAdapter.LikedEventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.liked_item, parent, false)
        return LikedEventViewHolder(view)
    }

    override fun getItemCount() = likedEvents.size

    override fun onBindViewHolder(holder: LikedEventViewHolder, position: Int) {
        val likedEvent = likedEvents[position]
        holder.bind(likedEvent)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateLikedEvents(likedEvents: List<LikedEvent>) {
        this.likedEvents = likedEvents
        notifyDataSetChanged()
    }

    inner class LikedEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationNameTextView: TextView = itemView.findViewById(R.id.locationName)
        private val locationDateTextView: TextView= itemView.findViewById(R.id.locationDate)
        private val moveToCalendarButton: Button = itemView.findViewById(R.id.button)

        fun bind(likedEvent: LikedEvent) {
            locationNameTextView.text = likedEvent.locationName
            locationDateTextView.text = likedEvent.locationDate

        }
    }
}