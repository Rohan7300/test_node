package com.android

import com.android.di.mainModule
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    // Add the intercept for logging
    intercept(ApplicationCallPipeline.Plugins) {
        println("Incoming request: ${call.request.uri}, Headers: ${call.request.headers.toMap()}")
    }

    install(Koin) {
        modules(mainModule)
    }
    configureSockets()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
