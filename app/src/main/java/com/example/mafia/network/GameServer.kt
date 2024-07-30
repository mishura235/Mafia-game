package com.example.mafia.network



import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.ShutDownUrl.EnginePlugin.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import java.time.Duration


object GameServer {
    private val server by lazy {
        embeddedServer(Netty, port = 9292, host = "0.0.0.0") {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                webSocket("/chat") {

                }
            }
        }
    }
    fun startRoomServer(){
        server.start()
    }
}



