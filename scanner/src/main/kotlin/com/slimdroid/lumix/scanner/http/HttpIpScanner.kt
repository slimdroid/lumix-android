package com.slimdroid.lumix.scanner.http

import com.slimdroid.lumix.scanner.Device
import com.slimdroid.lumix.scanner.http.HttpIpScanner.Companion.IP_PATTERN
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.net.NetworkInterface

internal class HttpIpScanner : HttpScanner() {

    companion object {
        private const val TAG = "Scanner_HTTP_IP"

        internal val IP_PATTERN =
            Regex(pattern = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\$")
    }

    private var producerScope: ProducerScope<Device>? = null
    private var isScanning = false

    override fun startScan() = channelFlow {
        if (!isScanning) {
            Napier.i("Start HTTP IP scanner", tag = TAG)

            val networkIpList = getNetworkIpList()
            if (networkIpList.isEmpty()) return@channelFlow

            isScanning = true
            producerScope = this

            coroutineScope.launch {
                while (isScanning) {
                    getIpCollectionForScan(networkIpList)
                        .map {
                            async {
                                checkDevice(it)
                            }
                        }.forEach { device ->
                            device.await().onSuccess { trySend(it) }
                        }
                }
            }
        }
        awaitClose()
    }.takeWhile {
        isScanning
    }.onEach {
        Napier.i("device:${it.macAddress}, ip:${it.ipAddress}, type:${it.type}", tag = TAG)
    }.onCompletion {
        Napier.i("result: HTTP IP search is over", tag = TAG)
    }

    override fun stopScan() {
        isScanning = false
        producerScope?.close()
        producerScope = null
    }

    private fun getIpCollectionForScan(networkIpList: List<String>) =
        networkIpList.flatMap {
            generateIpList(it)
        }

    private fun generateIpList(address: String): List<String> = mutableListOf<String>()
        .apply {
            val mask = address.substring(0, address.lastIndexOf("."))
            for (lastOctet in 0..255) {
                add("$mask.$lastOctet")
            }
            remove(address)
        }

}

private fun getNetworkIpList(): List<String> =
    NetworkInterface.getNetworkInterfaces()
        .asSequence()
        .flatMap { networkInterface ->
            networkInterface.inetAddresses.toList()
        }.filter { address ->
            !address.isLoopbackAddress && address.isSiteLocalAddress
        }.mapNotNull {
            it.hostAddress
        }.filter {
            IP_PATTERN.matches(it)
        }.toList()

