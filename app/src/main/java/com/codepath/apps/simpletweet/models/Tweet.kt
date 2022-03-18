package com.codepath.apps.simpletweet.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
@Entity(foreignKeys = [ForeignKey(entity=User::class, parentColumns=["user_id"], childColumns=["userId"])])
data class Tweet (
    @ColumnInfo
    @PrimaryKey
    var id : Long,

    @ColumnInfo
    var body: String,

    @ColumnInfo
    var createdAt: String,

    @ColumnInfo
    var userId : Long,

    @Ignore
    var user: User?
) : Parcelable {

    // make Room happy by having a constructor that doesn't have the ignored 'user' field
    constructor(id: Long, body: String, createdAt: String, userId: Long) : this( id, body, createdAt, userId, null )

    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val user = User.fromJson(jsonObject.getJSONObject("user"))
            return Tweet(
                jsonObject.getLong("id"),
                jsonObject.getString("text"),
                jsonObject.getString("created_at"),
                user.user_id,
                user
            )
        }

        fun fromJsonArray(jsonArray: JSONArray) : List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }
}