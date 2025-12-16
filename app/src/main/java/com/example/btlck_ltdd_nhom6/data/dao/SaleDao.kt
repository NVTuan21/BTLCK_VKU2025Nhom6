package com.example.btlck_ltdd_nhom6.data.dao

import androidx.room.*
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.data.relations.BookAndSale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    // Thêm một giao dịch bán hàng mới
    @Insert
    suspend fun insertSale(sale: Sale)

    // Lấy tất cả giao dịch theo thứ tự ngày bán mới nhất
    @Query("SELECT * FROM sale_table ORDER BY saleDate DESC")
    fun getAllSales(): Flow<List<Sale>>

    // Lấy tổng doanh thu
    @Query("SELECT SUM(totalPrice) FROM sale_table")
    fun getTotalRevenue(): Flow<Double>

    // Lấy tất cả giao dịch cùng với thông tin chi tiết về sách
    @Transaction
    @Query("SELECT * FROM sale_table ORDER BY saleDate DESC")
    fun getSalesWithBookDetails(): Flow<List<BookAndSale>>

    @Insert
    suspend fun insertAllSales(sales: List<Sale>)
}