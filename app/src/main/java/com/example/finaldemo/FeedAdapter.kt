package com.example.finaldemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView

class FeedAdapter(private var posts: List<Post>) : RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    fun updatePosts(posts: List<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postTextView: TextView = itemView.findViewById(R.id.locationText)
        private val pictureImageView: ImageView = itemView.findViewById(R.id.locationImage)
//        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationName)

        fun bind(post: Post) {
            postTextView.text = post.postText
//            ratingBar.rating = post.rating
            locationTextView.text = post.location
            Glide.with(itemView)
                .load(post.pictureUrl)
                .into(pictureImageView)
            }
        }
    }

