package com.android

import com.android.session.ChatSession
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlin.collections.set

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    intercept(Plugins) {
        if (call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["userName"] ?: "Guest"
            call.sessions.set(ChatSession(username, generateNonce()))
        }
    }

    /* routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }*/
}