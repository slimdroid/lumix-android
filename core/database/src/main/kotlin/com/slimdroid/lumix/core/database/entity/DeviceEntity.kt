package com.slimdroid.lumix.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slimdroid.lumix.core.model.LumixDevice

@Entity(tableName = "device")
data class DeviceEntity(
    @PrimaryKey
    @ColumnInfo("mac_address")  val macAddress: String,
    @ColumnInfo("ip_address")   val ipAddress: String,
    @ColumnInfo("name")         val name: String,
    @ColumnInfo("firmware")     val firmware: String,
    @ColumnInfo("type")         val type: String,
)

fun DeviceEntity.toExternalModel() = LumixDevice(
    macAddress = macAddress,
    ipAddress = ipAddress,
    name = name,
    type = type,
    firmware = firmware
)

fun LumixDevice.toEntity() = DeviceEntity(
    macAddress = macAddress,
    ipAddress = ipAddress,
    name = name,
    type = type,
    firmware = firmware
)