package com.example.btlck_ltdd_nhom6.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.dao.BookDao
import com.example.btlck_ltdd_nhom6.data.dao.SaleDao
import com.example.btlck_ltdd_nhom6.data.entity.Sale

@Database(entities = [Book::class , Sale::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Khai báo các DAO
    abstract fun bookDao(): BookDao
    abstract fun saleDao(): SaleDao
    companion object {
        // Singleton pattern để tránh nhiều phiên bản database được mở
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Tên file database
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}