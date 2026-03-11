package com.slimdroid.lumix.core.scanner.udp

import com.slimdroid.lumix.core.scanner.Device
import com.slimdroid.lumix.core.scanner.Scanner
import com.slimdroid.lumix.core.scanner.dto.ScanDeviceDto
import com.slimdroid.lumix.core.scanner.dto.asExternalModel
import io.github.aakira.napier.Napier
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeText
import io.ktor.utils.io.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

internal class UdpBroadcastScanner(private val broadcastIp: String = BROADCAST_IP) : Scanner {

    companion object {
        private const val TAG = "Scanner_UDP"

        private const val BROADCAST_IP = "255.255.255.255"
        private const val BROADCAST_PORT = 4210
        private const val HANDSHAKE = "LUMIX_DISCOVERY"
        private val SCANNER_INTERVAL = 1000.milliseconds
    }

    private val selectorManager = SelectorManager(Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }
    private var inSocket: BoundDatagramSocket? = null

    private var producerScope: ProducerScope<Device>? = null
    private var isScanning = false

    override fun startScan() = channelFlow {
        if (!isScanning) {
            Napier.i("Start Broadcast scanner", tag = TAG)

            isScanning = true
            producerScope = this

            inSocket = aSocket(selectorManager)
                .udp()
                .bind { broadcast = true }

            launch {
                receiveAnswerFlow(this@channelFlow)
            }

            while (isScanning) {
                sendHandshake()
                delay(SCANNER_INTERVAL)
            }
        }

        awaitClose {
            inSocket?.close()
            Napier.i("socket has been closed", tag = TAG)
        }
    }.takeWhile {
        isScanning
    }.onEach {
        Napier.i("device:${it.macAddress}, ip:${it.ipAddress}, type:${it.type}", tag = TAG)
    }.onCompletion {
        Napier.i("result: UPD Broadcast search is over", tag = TAG)
    }

    override fun stopScan() {
        isScanning = false
        producerScope?.close()
    }

    private suspend fun sendHandshake() {
        val datagram = Datagram(
            packet = buildPacket { writeText(HANDSHAKE) },
            address = InetSocketAddress(broadcastIp, BROADCAST_PORT)
        )
        inSocket?.outgoing?.send(datagram)
        Napier.i("Broadcast packet sent to: ${datagram.address}", tag = TAG)
    }

    private suspend fun receiveAnswerFlow(flow: ProducerScope<Device>) {
        inSocket?.incoming?.receiveAsFlow()
            ?.cancellable()
            ?.catch {
                Napier.e("Broadcast exception: ${it.message}", tag = TAG)
            }
            ?.collect { datagram ->
                try {
                    val receiveString = datagram.packet.readText()

                    Napier.i("received data: $receiveString IP:${datagram.address}", tag = TAG)
                    val jsonObject = json.decodeFromString<ScanDeviceDto>(receiveString)

                    flow.trySend(jsonObject.asExternalModel())
                } catch (e: Exception) {
                    Napier.e("parse exception: $e", tag = TAG)
                }
                datagram.packet.close()
            }
    }

}
