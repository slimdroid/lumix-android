package com.slimdroid.lumix.core.database.source

import com.slimdroid.lumix.core.database.dao.DeviceDao
import com.slimdroid.lumix.core.database.entity.DeviceEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DeviceLocalDataSource(
    private val deviceDao: DeviceDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun insertDevice(device: DeviceEntity) = withContext(ioDispatcher) {
        deviceDao.insertDevice(device)
    }

    suspend fun insertDevices(devices: List<DeviceEntity>) = withContext(ioDispatcher) {
        deviceDao.insertDevices(devices)
    }

    fun getDevices(): Flow<List<DeviceEntity>> = deviceDao.getDevices()
        .flowOn(ioDispatcher)

    suspend fun getDeviceById(id: String): DeviceEntity? = withContext(ioDispatcher) {
        deviceDao.getDeviceById(id)
    }

    suspend fun updateDeviceIp(id: String, ip: String) = withContext(ioDispatcher) {
        deviceDao.updateDeviceIp(id, ip)
    }

    suspend fun updateDeviceName(id: String, name: String) = withContext(ioDispatcher) {
        deviceDao.updateDeviceName(id, name)
    }

    suspend fun updateAppVersion(id: String, version: String) = withContext(ioDispatcher) {
        deviceDao.updateAppVersion(id, version)
    }

    suspend fun deleteDevice(device: DeviceEntity) = withContext(ioDispatcher) {
        deviceDao.deleteDevice(device)
    }

    suspend fun deleteDeviceById(id: String) = withContext(ioDispatcher) {
        deviceDao.deleteDeviceById(id)
    }

    suspend fun deleteAll() = withContext(ioDispatcher) {
        deviceDao.deleteAll()
    }

}