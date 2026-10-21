package net.awalq.controller.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VoucherDao {
    @Query("SELECT * FROM vouchers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Voucher>>

    @Query("SELECT * FROM vouchers WHERE code = :code")
    suspend fun getByCode(code: String): Voucher?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(voucher: Voucher)

    @Update
    suspend fun update(voucher: Voucher)

    @Delete
    suspend fun delete(voucher: Voucher)
}
