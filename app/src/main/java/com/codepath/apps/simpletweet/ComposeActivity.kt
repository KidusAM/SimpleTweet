package com.codepath.apps.simpletweet

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.codepath.apps.simpletweet.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet : Button
    lateinit var tvTweetCharCount : TextView

    lateinit var client: TwitterClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvTweetCharCount = findViewById(R.id.tvTweetCharCount)
        client = TwitterApplication.getRestClient(this)

        // if there is a saved draft, retrieve it
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val draft = pref.getString(DRAFT_KEY, "")

        etCompose.setText(draft)

        btnTweet.setOnClickListener { postTweet() }
        updateLengthCount()
        etCompose.addTextChangedListener {
            updateLengthCount()
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (etCompose.text.length == 0) {
            // just exit out since there is nothing to save
            super.onBackPressed()
            return
        }

        val tweetComposeDialogFragment = TweetComposeDialogFragment.newInstance(
            "Do you want to save your tweet as a draft?",
            object : TweetComposeDialogFragment.OnDialogAction {
                override fun onSubmitCallback() {
                    updateTweetDraft(etCompose.text.toString())
                    super@ComposeActivity.onBackPressed()
                }

                override fun onCancelCallback() {
                    updateTweetDraft("")
                    super@ComposeActivity.onBackPressed()
                }
            }
        )
        tweetComposeDialogFragment.show(fragmentManager, "fragment_confirm")
    }

    fun updateLengthCount() {
        val charsLeft = MAX_TWEET_LENGTH - etCompose.text.length

        tvTweetCharCount.text = charsLeft.toString()
        if (charsLeft < 0) {
            tvTweetCharCount.setTextColor(Color.RED)
            btnTweet.isEnabled = false
        } else {
            tvTweetCharCount.setTextColor(Color.BLACK)
            btnTweet.isEnabled = true
        }
    }


    fun updateTweetDraft(draftText : String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this@ComposeActivity)
        val edit = pref.edit()
        edit.putString(ComposeActivity.DRAFT_KEY, draftText)
        edit.commit()
    }

    fun postTweet() {
        // grab the content of the edit text
        val tweetContent = etCompose.text.toString()

        // 1. check that tweet isn't empty
        if (tweetContent.isEmpty()) {
            Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            // Look into displaying SnackBar
        }
        // 2. make sure the tweet isn't over the char count
        else if (tweetContent.length > MAX_TWEET_LENGTH) {
            Toast.makeText(
                this,
                "Tweet is too long! Limit is 140 characters",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    Log.i(TAG, "Successfully published tweet")
                    val newTweet = Tweet.fromJson(json.jsonObject)

                    val intent = Intent()
                    intent.putExtra("newTweet", newTweet)

                    setResult(RESULT_OK, intent)
                    finish()
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.e(TAG, "Failed to publish tweet", throwable)
                }
            })
        }

        // update the draft to be nothing so that they can start fresh on the next compose
        updateTweetDraft("")

    }
    companion object {
        val TAG = "ComposeActivity"
        val DRAFT_KEY = "tweet_draft"
        val MAX_TWEET_LENGTH = 240
    }
}