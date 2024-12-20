package com.android.routes

import com.android.room.MemberAlreadyExistsException
import com.android.room.RoomController
import com.android.session.ChatSession
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(
    roomController: RoomController
) {
    println("Under Routing1")
    webSocket("/chat-session") {
        println("WebSocket connection initiated")
        val session = call.sessions.get<ChatSession>()

        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No Session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                userName = session.userName, sessionId = session.session, socket = this
            )
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    roomController.sendMessage(
                        senderUsername = session.userName, message = frame.readText()
                    )
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(session.userName)
        }
    }
}

fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK, roomController.getAllMessages()
        )
    }
}