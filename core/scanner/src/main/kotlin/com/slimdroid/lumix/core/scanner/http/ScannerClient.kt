package com.slimdroid.lumix.core.scanner.http

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ScannerClient {

    private const val DEVICE_PORT = 8888
    private const val CONNECTION_TIMEOUT = 1_000L // in milliseconds

    internal val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#explicit-nulls
        explicitNulls = false
    }

    fun getHttpClient(ip: String): HttpClient = HttpClient(Android.create()) {
        expectSuccess = true
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = "$ip:$DEVICE_PORT"
            }
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
//            logger = Logger.DEFAULT
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.i("HTTP_Client", null, message)
                    println(message)
                }
            }
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            socketTimeoutMillis = CONNECTION_TIMEOUT
        }
    }

}
