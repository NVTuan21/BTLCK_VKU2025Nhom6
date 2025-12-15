package com.example.btlck_ltdd_nhom6.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sale_table")
data class Sale(

    @PrimaryKey(autoGenerate = true)
    val saleId: Int = 0,

    // Khóa ngoại trỏ đến sách đã bán
    val bookId: Int,

    val quantitySold: Int,

    val saleDate: Long = System.currentTimeMillis(), // Thời điểm giao dịch

    val totalPrice: Double // Tổng giá trị giao dịch (có thể tính từ giá sách * số lượng)
)