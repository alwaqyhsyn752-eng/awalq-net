package net.awalq.controller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Voucher::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun voucherDao(): VoucherDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "awalq_net.db"
                ).build().also { INSTANCE = it }
            }
    }
}
