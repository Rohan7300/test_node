package com.android.data

import com.android.data.model.Message
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    db: CoroutineDatabase
) : MessageDataSource {

    private val message = db.getCollection<Message>()

    override suspend fun getAllMessages(): List<Message> {
        return message.find().descendingSort(Message::timeStamp).toList()
    }

    override suspend fun insertMessage(message: Message) {
        this.message.insertOne(message)
    }
}