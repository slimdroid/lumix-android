package com.slimdroid.lumix.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.slimdroid.lumix.core.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query(value = "SELECT * FROM device")
    fun getDevices(): Flow<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Query("SELECT * FROM device WHERE mac_address = :id")
    suspend fun getDeviceById(id: String): DeviceEntity?

    @Query("UPDATE device SET ip_address = :ip WHERE mac_address = :id")
    suspend fun updateDeviceIp(id: String, ip: String)

    @Query("UPDATE device SET name = :name WHERE mac_address = :id")
    suspend fun updateDeviceName(id: String, name: String)

    @Query("UPDATE device SET firmware = :version WHERE mac_address = :id")
    suspend fun updateAppVersion(id: String, version: String)

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    @Query("DELETE FROM device WHERE mac_address = :id")
    suspend fun deleteDeviceById(id: String)

    @Transaction
    @Query("DELETE FROM device")
    suspend fun deleteAll()
}