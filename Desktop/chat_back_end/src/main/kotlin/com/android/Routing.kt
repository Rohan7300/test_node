package com.android

import com.android.room.RoomController
import com.android.routes.chatSocket
import com.android.routes.getAllMessages
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    /*routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }*/

    val roomController by inject<RoomController>()
    routing {
        println("Under Routing")
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}