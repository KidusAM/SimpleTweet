package com.codepath.apps.simpletweet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.simpletweet.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var client : TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var swipeContainer : SwipeRefreshLayout
    lateinit var adapter: TweetsAdapter
    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)
        rvTweets = findViewById(R.id.rvTweets)
        swipeContainer = findViewById(R.id.swipeContainer)
        adapter = TweetsAdapter(tweets)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            refreshHomeTimeline()
        }
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        val layoutManager = LinearLayoutManager(this)
        rvTweets.layoutManager = layoutManager
        rvTweets.adapter = adapter
        rvTweets.addOnScrollListener( object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore")
                populateHomeTimeline()
            }
        })

        populateHomeTimeline()
    }

    fun refreshHomeTimeline() {
        adapter.clear()
        populateHomeTimeline()
    }

    fun populateHomeTimeline() {
        val maxId = if (tweets.size > 0) tweets.last().id - 1 else null
        client.getHomeTimeline(maxId, object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess!")

                try {
                    // clear out our current fetched tweets
                    val oldSize = tweets.size
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray))
                    adapter.notifyDataSetChanged()

                    swipeContainer.isRefreshing = false
                } catch (e : JSONException) {
                    Log.e(TAG, "Json error $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure $statusCode\n $response!")
            }

        })
    }

    companion object {
        val TAG = "TimelineActivity"
    }
}