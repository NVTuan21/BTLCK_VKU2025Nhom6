package com.example.btlck_ltdd_nhom6.repository

import com.example.btlck_ltdd_nhom6.data.dao.BookDao
import com.example.btlck_ltdd_nhom6.data.dao.SaleDao
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.data.relations.BookAndSale
import kotlinx.coroutines.flow.Flow
import androidx.room.withTransaction // Cần cho giao dịch Room
import com.example.btlck_ltdd_nhom6.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookstoreRepository(
    private val bookDao: BookDao,
    private val saleDao: SaleDao,
    private val database: AppDatabase
) {
    // -------------------------------------------------------------------
    // A. PHẦN READ (READ OPERATIONS)
    // -------------------------------------------------------------------

    // Lấy tất cả sách (dạng Flow để ViewModel quan sát)
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    fun getBookById(id: Int): Flow<Book?> {
        return bookDao.getBookById(bookId = id)
    }

    // Lấy tất cả giao dịch bán hàng (dạng Flow)
    val allSalesWithDetails: Flow<List<BookAndSale>> = saleDao.getSalesWithBookDetails()

    // Lấy tổng doanh thu
    val totalRevenue: Flow<Double> = saleDao.getTotalRevenue()

    // -------------------------------------------------------------------
    // A.1. TÍNH NĂNG TÌM KIẾM MỚI
    // -------------------------------------------------------------------

    /**
     * Tìm kiếm sách theo từ khóa (tên sách hoặc tác giả).
     * Hàm này cần được hỗ trợ bởi hàm tương ứng trong BookDao.
     */
    suspend fun searchBooks(query: String): List<Book> {
        // Cần đảm bảo BookDao có hàm: suspend fun searchBooks(query: String): List<Book>
        return withContext(Dispatchers.IO) {
            bookDao.searchBooks(query)
        }
    }


    // -------------------------------------------------------------------
    // B. PHẦN WRITE (WRITE/UPDATE OPERATIONS)
    // -------------------------------------------------------------------

    suspend fun insertBook(book: Book) {
        bookDao.insert(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.update(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.delete(book)
    }

    // -------------------------------------------------------------------
    // C. LOGIC NGHIỆP VỤ (Bán hàng)
    // -------------------------------------------------------------------

    // ** Hàm cũ đã được thay thế bằng hàm insertBulkSalesAndUpdateInventory bên dưới. **
    // suspend fun processSaleTransaction(sale: Sale) { ... }

    /**
     * Thực hiện giao dịch bán hàng hàng loạt trong một Room Transaction (Atomic operation).
     * 1. Kiểm tra tồn kho cho tất cả các mặt hàng.
     * 2. Thêm tất cả giao dịch vào bảng Sale.
     * 3. Cập nhật tồn kho cho tất cả các sách liên quan.
     * * @throws Exception nếu tồn kho không đủ hoặc sách không tồn tại.
     */
    suspend fun insertBulkSalesAndUpdateInventory(sales: List<Sale>) {
        // Room Transaction đảm bảo toàn bộ logic được thực hiện thành công
        // hoặc không có gì xảy ra (rollback) nếu có lỗi.
        database.withTransaction {

            val booksToUpdate = mutableListOf<Book>()

            // 1. Kiểm tra tồn kho và chuẩn bị cập nhật
            for (sale in sales) {
                // Lấy thông tin sách hiện tại (cần BookDao có hàm suspend getBookForTransaction)
                val bookIdInt = sale.bookId.toInt()
                val book = bookDao.getBookForTransaction(bookIdInt)

                if (book == null) {
                    throw Exception("Không tìm thấy sách với ID: ${sale.bookId}")
                }

                if (book.stockQuantity < sale.quantitySold) {
                    throw Exception("Không đủ tồn kho cho sách '${book.title}'. Tồn: ${book.stockQuantity}, Cần bán: ${sale.quantitySold}.")
                }

                // Tính tồn kho mới
                val newStock = book.stockQuantity - sale.quantitySold
                // Tạo đối tượng Book đã được cập nhật
                booksToUpdate.add(book.copy(stockQuantity = newStock))
            }

            // 2. Thêm tất cả giao dịch bán hàng
            saleDao.insertAllSales(sales) // Cần đảm bảo SaleDao có hàm insertAllSales

            // 3. Cập nhật tồn kho cho tất cả các sách
            bookDao.updateAll(booksToUpdate) // Cần đảm bảo BookDao có hàm updateAll
        }
    }

    suspend fun processSaleTransaction(sale: Sale) {
        // Có thể gọi hàm Bulk Sale nếu muốn xử lý giao dịch đơn lẻ
        insertBulkSalesAndUpdateInventory(listOf(sale))
    }
}