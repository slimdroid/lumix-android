package com.slimdroid.lumix.core.scanner.http

import com.slimdroid.lumix.core.scanner.Device
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal class HttpServiceModeScanner : HttpScanner() {

    companion object {
        private const val TAG = "Scanner_HTTP_ServiceMode"

        private val SEARCH_DELAY = 2000.milliseconds
        private const val SEARCH_ATTEMPTS = 3
    }

    private var producerScope: ProducerScope<Device>? = null
    private var isScanning = false

    override fun startScan() = channelFlow {
        if (!isScanning) {
            Napier.i("Start HTTP Service Mode scanner", tag = TAG)

            isScanning = true
            producerScope = this

            coroutineScope.launch {
                repeat(SEARCH_ATTEMPTS) {
                    if (isScanning) {
                        launch {
                            checkDevice("192.168.0.1")
                                .onSuccess { trySend(it) }
                        }.join()
                        delay(SEARCH_DELAY)
                    }
                }
                stopScan()
            }
        }
        awaitClose()
    }.takeWhile {
        isScanning
    }.onEach {
        Napier.i("device:${it.macAddress}, ip:${it.ipAddress}, type:${it.type}", tag = TAG)
    }.onCompletion {
        Napier.i("result: HTTP Service Mode search is over", tag = TAG)
    }

    override fun stopScan() {
        isScanning = false
        producerScope?.close()
        producerScope = null
    }

}