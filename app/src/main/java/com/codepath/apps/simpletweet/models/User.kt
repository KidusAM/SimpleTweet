package com.codepath.apps.simpletweet.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
@Entity
data class User (
    @ColumnInfo
    @PrimaryKey
    var user_id : Long,

    @ColumnInfo
    var name: String,

    @ColumnInfo
    var screenName: String,

    @ColumnInfo
    var publicImageUrl: String
) : Parcelable {


    companion object {
        fun fromJson(jsonObject: JSONObject) : User {
            val user = User(
                jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("screen_name"),
                jsonObject.getString("profile_image_url_https")
            )

            return user
        }

        fun fromTweetsArray(tweets: List<Tweet>): List<User> {
            val users = arrayListOf<User>()
            for (tweet in tweets) {
                tweet.user?.let { users.add(it) }
            }
            return users
        }

    }
}