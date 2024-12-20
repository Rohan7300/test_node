package com.android.room

import com.android.data.MessageDataSource
import com.android.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        userName: String, sessionId: String, socket: WebSocketSession
    ) {
        if (members.containsKey(userName)) {
            throw MemberAlreadyExistsException()
        }
        members[userName] = Member(
            userName = userName, sessionId = sessionId, socket = socket
        )
    }

    suspend fun sendMessage(senderUsername: String, message: String) {

        val messageEntity = Message(
            message = message, userName = senderUsername, timeStamp = System.currentTimeMillis()
        )
        messageDataSource.insertMessage(messageEntity)

        members.values.forEach { member ->
            val parsedMessageString = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessageString))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}