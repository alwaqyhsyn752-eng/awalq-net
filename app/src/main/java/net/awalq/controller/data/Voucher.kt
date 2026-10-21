package net.awalq.controller.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vouchers")
data class Voucher(
    @PrimaryKey val code: String,
    val durationMinutes: Int,
    val dataLimitMb: Int,
    val usedMinutes: Int = 0,
    val usedDataMb: Int = 0,
    val isActive: Boolean = true,
    val macAddress: String? = null,
    val ipAddress: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
