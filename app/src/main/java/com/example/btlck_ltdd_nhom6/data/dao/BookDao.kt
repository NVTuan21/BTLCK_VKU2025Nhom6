package com.example.btlck_ltdd_nhom6.data.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.example.btlck_ltdd_nhom6.data.entity.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // C - Create (Thêm sách mới)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    // U - Update (Cập nhật thông tin sách)
    @Update
    suspend fun update(book: Book)

    // D - Delete (Xóa một cuốn sách)
    @Delete
    suspend fun delete(book: Book)

    // R - Read (Lấy tất cả sách và quan sát thay đổi)
    @Query("SELECT * FROM book_table ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>> // Sử dụng Flow để xử lý luồng dữ liệu theo kiến trúc hiện đại

    // R - Read (Lấy sách theo ID)
    @Query("SELECT * FROM book_table WHERE bookId = :bookId")
    fun getBookById(bookId: Int): Flow<Book?>

    @Query("SELECT * FROM book_table WHERE bookId = :bookId")
    suspend fun getBookForTransaction(bookId: Int): Book?

    @Update
    fun updateAll(booksToUpdate: MutableList<Book>)

    @Query("SELECT * FROM book_table WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): List<Book>
}