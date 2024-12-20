package com.android.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    val message: String, val userName: String, val timeStamp: Long, @BsonId val id: String = ObjectId().toString()
)
