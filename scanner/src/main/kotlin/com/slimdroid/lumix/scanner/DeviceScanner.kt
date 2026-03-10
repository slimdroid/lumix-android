package com.slimdroid.lumix.scanner

import com.slimdroid.lumix.scanner.http.HttpScanner
import com.slimdroid.lumix.scanner.http.HttpServiceModeScanner
import com.slimdroid.lumix.scanner.udp.UdpBroadcastScanner
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.NetworkInterface

private const val DEFAULT_SEARCHING_TIME = 5_000L  // in milliseconds

interface DeviceScanner {

    /**
     * Use case:
     * ```
     * viewModelScope.launch {
     *    scanner.startScanFlow()
     *       .onCompletion {
     *          // called when the scanner is stopped
     *       }.collect { deviceList ->
     *          // show devices
     *       }
     *    }
     * }
     * ```
     * @param timeout time in milliseconds after which the search ends. A value less than or equal to 0 means no timeout
     * @param searchServiceMode activates the search for devices that are in service mode at the
     */
    fun startScanFlow(
        timeout: Long = DEFAULT_SEARCHING_TIME,
        searchServiceMode: Boolean = false,
    ): Flow<List<Device>>

    fun stopScan()

    suspend fun scanDeviceByIp(deviceIp: String): Result<Device>
}

class DeviceScannerImpl(private vararg val deviceTypes: DeviceType) : DeviceScanner {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private val stopper: suspend (timeout: Long) -> Unit = { timeout ->
        delay(timeout)
        stopScan()
    }
    private val mutex = Mutex()
    private val deviceSet: MutableSet<Device> = mutableSetOf()
    private val scannerCollection: MutableList<Scanner> = mutableListOf()

    override fun startScanFlow(
        timeout: Long,
        searchServiceMode: Boolean,
    ): Flow<List<Device>> = getScanners(searchServiceMode)
        .map { it.startScan() }
        .also {
            if (timeout > 0) coroutineScope.launch { stopper.invoke(timeout) }
        }
        .asIterable()
        .merge()
        .filter { deviceTypes.contains(it.type) }
        .map { device ->
            mutex.withLock {
                deviceSet.add(device)
                deviceSet.toList()
            }
        }.onCompletion {
            mutex.withLock {
                deviceSet.clear()
            }
            Napier.i("result: search completed", tag = TAG)
        }.flowOn(Dispatchers.IO)

    override fun stopScan() {
        job.cancelChildren()

        scannerCollection.forEach { it.stopScan() }
        scannerCollection.clear()
    }

    override suspend fun scanDeviceByIp(deviceIp: String): Result<Device> {
        val result = HttpScanner.checkDevice(deviceIp)
        return if (result.isSuccess) {
            val device: Device = result.getOrThrow()

            if (deviceTypes.contains(device.type)) {
                Result.success(device)
            } else {
                Result.failure(Exception("Prohibited or filtered device"))
            }
        } else {
            result
        }
    }

    private fun getScanners(isServiceMode: Boolean): List<Scanner> = scannerCollection.apply {
        if (isNotEmpty()) return@apply

        if (isServiceMode) {
            add(HttpServiceModeScanner())
            return@apply
        }

        val addresses = listAllBroadcastAddresses()
        if (addresses.isNotEmpty()) {
            addresses.forEach { add(UdpBroadcastScanner(broadcastIp = it)) }
        } else {
            add(UdpBroadcastScanner())
        }
    }

    companion object {
        private const val TAG = "Scanner"
    }
}

private fun listAllBroadcastAddresses(): List<String> =
    NetworkInterface.getNetworkInterfaces()
        .asSequence()
        .filter { networkInterface -> !networkInterface.isLoopback || networkInterface.isUp }
        .flatMap { networkInterface ->
            networkInterface.interfaceAddresses
        }.mapNotNull { interfaceAddress ->
            interfaceAddress.broadcast
        }.filter { address ->
            address.isSiteLocalAddress
        }.mapNotNull { address ->
            address.hostAddress
        }.toList()