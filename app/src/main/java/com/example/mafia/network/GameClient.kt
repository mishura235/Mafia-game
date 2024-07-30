package com.example.mafia.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


object GameClient {
    private val client by lazy {
        HttpClient(OkHttp){
            engine {
                config {
                    followRedirects(true)
                }
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(15, TimeUnit.SECONDS)
                    .build()
            }
            install(WebSockets){

            }
        }
    }

    suspend fun connectToRoom(url:String): HttpResponse = withContext(Dispatchers.IO){
        client.get(urlString = url)
    }

}