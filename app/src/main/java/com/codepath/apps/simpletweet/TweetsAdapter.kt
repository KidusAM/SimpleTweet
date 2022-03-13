package com.codepath.apps.simpletweet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.simpletweet.models.Tweet

class TweetsAdapter(val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        // Inflate our item layout
        val view = inflater.inflate(R.layout.item_tweet, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        holder.bind(tweets[position])
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody = itemView.findViewById<TextView>(R.id.tvTweetBody)
        val tvTweetTime = itemView.findViewById<TextView>(R.id.tvTweetTime)
        val ivProfilePic = itemView.findViewById<ImageView>(R.id.ivProfilePic)

        fun bind(tweet: Tweet) {
            tvUserName.text = tweet.user?.name
            tvTweetBody.text = tweet.body
            tvTweetTime.text = TimeFormatter.getTimeDifference(tweet.createdAt)
            Glide.with(itemView)
                .load(tweet.user?.publicImageUrl)
                .into(ivProfilePic)
        }
    }
}