package com.example.btlck_ltdd_nhom6.repository

import com.example.btlck_ltdd_nhom6.data.dao.BookDao
import com.example.btlck_ltdd_nhom6.data.dao.SaleDao
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.data.relations.BookAndSale
import kotlinx.coroutines.flow.Flow

class BookstoreRepository(
    private val bookDao: BookDao,
    private val saleDao: SaleDao
    // private val apiService: BookApiService // Thêm vào khi tích hợp Retrofit
) {

    // -------------------------------------------------------------------
    // A. PHẦN READ (READ OPERATIONS)
    // -------------------------------------------------------------------

    // Lấy tất cả sách (dạng Flow để ViewModel quan sát)
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    fun getBookById(id: Int): Flow<Book?> {
        // Bây giờ, bookDao.getBookById không còn là suspend, nên có thể gọi trực tiếp
        return bookDao.getBookById(bookId = id)
    }

    // Lấy tất cả giao dịch bán hàng (dạng Flow)
    val allSalesWithDetails: Flow<List<BookAndSale>> = saleDao.getSalesWithBookDetails()

    // Lấy tổng doanh thu
    val totalRevenue: Flow<Double> = saleDao.getTotalRevenue()


    // -------------------------------------------------------------------
    // B. PHẦN WRITE (WRITE/UPDATE OPERATIONS)
    // -------------------------------------------------------------------

    // Thêm/Cập nhật sách (chỉ tương tác với Room)
    suspend fun insertBook(book: Book) {
        bookDao.insert(book)
        // Nếu có API, bạn sẽ gọi apiService.postBook(book) ở đây
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

    /**
     * Thực hiện giao dịch bán hàng:
     * 1. Thêm giao dịch vào bảng Sale.
     * 2. Cập nhật số lượng tồn kho trong bảng Book.
     */
    suspend fun processSaleTransaction(sale: Sale) {
        // 1. Lấy thông tin sách hiện tại
        val book = bookDao.getBookForTransaction(sale.bookId)

        if (book != null && book.stockQuantity >= sale.quantitySold) {
            // 2. Thêm giao dịch bán hàng
            saleDao.insertSale(sale)

            // 3. Cập nhật tồn kho (Logic Nghiệp vụ)
            val newStock = book.stockQuantity - sale.quantitySold
            val updatedBook = book.copy(stockQuantity = newStock)
            bookDao.update(updatedBook)

            // Nếu có API, bạn sẽ gọi apiService.updateStock(book.id, newStock) ở đây

        } else if (book != null && book.stockQuantity < sale.quantitySold) {
            // Xử lý lỗi: Không đủ tồn kho
            throw Exception("Không đủ sách tồn kho để thực hiện giao dịch.")
        } else {
            // Xử lý lỗi: Không tìm thấy sách
            throw Exception("Không tìm thấy sách với ID: ${sale.bookId}")
        }
    }
}