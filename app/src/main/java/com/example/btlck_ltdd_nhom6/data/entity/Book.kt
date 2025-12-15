package com.example.btlck_ltdd_nhom6.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Định nghĩa Entity và tên bảng là 'book_table'
@Entity(tableName = "book_table")
data class Book(

    // Đặt 'id' làm Khóa chính và tự động tăng
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val author: String,
    val genre: String, // Thể loại
    val price: Double,
    val stockQuantity: Int // Số lượng tồn kho
)