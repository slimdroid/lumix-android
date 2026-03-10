package com.slimdroid.lumix.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slimdroid.lumix.core.model.LumixDevice

@Entity(tableName = "device")
data class DeviceEntity(
    @PrimaryKey
    @ColumnInfo("device_id") val deviceId: String,
    @ColumnInfo("device_ip") val deviceIp: String,
    @ColumnInfo("device_name") val name: String,
    @ColumnInfo("app_version") val appVersion: String,
    @ColumnInfo("device_type") val type: String,
)

fun DeviceEntity.toExternalModel() = LumixDevice(
    id = deviceId,
    ip = deviceIp,
    name = name,
    type = type,
    firmwareVersion = appVersion
)