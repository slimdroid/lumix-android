package com.slimdroid.lumix.data

import io.github.aakira.napier.Napier
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeText
import kotlinx.coroutines.Dispatchers

object MessageSender : AutoCloseable {

    private const val TAG = "MessageSender_UDP"
    private const val BROADCAST_IP = "255.255.255.255"
    private const val BROADCAST_PORT = 4211

    private val selectorManager = SelectorManager(Dispatchers.IO)
    private var inSocket: BoundDatagramSocket? = null
    private var isActive = false

    suspend fun start() {
        if (!isActive) {
            Napier.i("Opening UDP socket", tag = TAG)
            try {
                inSocket = aSocket(selectorManager)
                    .udp()
                    .bind { broadcast = true }
                isActive = true
            } catch (e: Exception) {
                Napier.e("Failed to bind socket", e, tag = TAG)
                return
            }
        }
    }

    suspend fun sendMessage(message: String, ip: String = BROADCAST_IP) {
        val socket = inSocket
        if (socket == null || socket.isClosed) {
            Napier.e("Cannot send message: Socket is not initialized or closed", tag = TAG)
            return
        }

        try {
            val datagram = Datagram(
                packet = buildPacket { writeText(message) },
                address = InetSocketAddress(ip, BROADCAST_PORT)
            )
            socket.outgoing.send(datagram)
            Napier.i("Message sent to $ip: $message", tag = TAG)
        } catch (e: Exception) {
            Napier.e("Error sending message", e, tag = TAG)
        }
    }

    override fun close() {
        inSocket?.close()
        selectorManager.close()
        isActive = false
        inSocket = null
    }
}