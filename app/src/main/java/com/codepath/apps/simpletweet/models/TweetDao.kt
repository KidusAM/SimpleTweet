package com.codepath.apps.simpletweet.models

import androidx.room.*

@Dao
interface TweetDao {
    @Query("SELECT * FROM Tweet INNER JOIN User ON Tweet.userId = User.user_id ORDER BY Tweet.createdAt LIMIT 25")
    fun recentItems() : List<TweetWithUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(vararg tweets : Tweet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(vararg users : User)


    data class TweetWithUser(
        @Embedded
        val tweet : Tweet,

        @Embedded
        val user : User,
    ) { }

    companion object {
        fun getTweetList(twuList : List<TweetWithUser>) : List<Tweet> {
            val tweets = arrayListOf<Tweet>()

            for (twu in twuList) {
                val tweet = twu.tweet
                tweet.user = twu.user
                tweets.add(tweet)
            }

            return tweets
        }
    }
}
