package com.example.btlck_ltdd_nhom6.ui.viewmodel

import androidx.lifecycle.* // Import tất cả LiveData/ViewModel
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import com.example.btlck_ltdd_nhom6.ui.fragment.CartItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel chịu trách nhiệm xử lý logic nghiệp vụ cho màn hình bán hàng (SaleFragment).
 */
class BookstoreViewModel(private val repository: BookstoreRepository) : ViewModel() {

    // -------------------------------------------------------------------
    // I. DATA FOR VIEW (SÁCH)
    // -------------------------------------------------------------------

    val allBooks: LiveData<List<Book>> = repository.allBooks.asLiveData()

    // -------------------------------------------------------------------
    // II. DATA FOR VIEW (GIAO DỊCH VÀ THỐNG KÊ)
    // -------------------------------------------------------------------

    val salesWithBookDetails = repository.allSalesWithDetails.asLiveData()

    val totalRevenue: LiveData<String> = repository.totalRevenue.asLiveData().map { total ->
        if (total != null) {
            "Doanh thu: ${String.format("%,.0f", total)} VND"
        } else {
            "Doanh thu: 0 VND"
        }
    }

    // -------------------------------------------------------------------
    // III. HÀM XỬ LÝ NGHIỆP VỤ (GỌI REPOSITORY)
    // -------------------------------------------------------------------

    /**
     * Sửa lỗi: Cập nhật hàm này để giải quyết lỗi 'Unresolved reference getBookById'
     * Hàm này được thiết kế để cung cấp Flow/LiveData từ Repository cho Fragment quan sát.
     * @param bookId: ID của sách (Sử dụng Long để phù hợp với ID Room)
     */
    fun getBookDetails(bookId: Int): LiveData<Book?> {
        // Repository.getBookById() thường nhận kiểu Int/Long.
        // Ta sử dụng bookId.toInt() để đảm bảo khớp với DAO nếu nó đang dùng Int.
        return repository.getBookById(bookId.toInt()).asLiveData()
    }


    // Thêm/Cập nhật sách
    fun insertBook(book: Book) = viewModelScope.launch {
        repository.insertBook(book)
    }

    // Xóa sách
    fun deleteBook(book: Book) = viewModelScope.launch {
        repository.deleteBook(book)
    }

    // XÓA HÀM NÀY, NÓ KHÔNG CẦN THIẾT VÀ GÂY LỖI CÚ PHÁP
    // private fun BookstoreRepository.processSaleTransaction(sale: Sale) {}

    // ** LƯU Ý: Nếu bạn cần dùng processSaleTransaction, hãy gọi nó từ Repository như sau:
    fun processSale(sale: Sale, onComplete: (Boolean, String?) -> Unit) = viewModelScope.launch {
        try {
            // Hàm này cần được thêm lại vào Repository để tránh lỗi Unresolved Reference
            repository.processSaleTransaction(sale)
            onComplete(true, null)
        } catch (e: Exception) {
            onComplete(false, e.message)
        }
    }

    // --- 1. Quản lý Kết quả Tìm kiếm ---

    private val _searchResults = MutableLiveData<List<Book>>()
    val searchResults: LiveData<List<Book>> = _searchResults

    private var searchJob: Job? = null

    /**
     * Thực hiện tìm kiếm sách trong database dựa trên truy vấn.
     */
    fun searchBooks(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) {
                val results = repository.searchBooks(query)
                _searchResults.value = results
            } else {
                _searchResults.value = emptyList()
            }
        }
    }

    // --- 2. Quản lý Giỏ hàng (Cart) ---

    private val _cartMap = MutableLiveData<MutableMap<Int, CartItem>>(mutableMapOf())

    val cartItems: LiveData<List<CartItem>> = _cartMap.map { it.values.toList() }

    // Hàm mở rộng map (Sử dụng extension function cho LiveData)
    private inline fun <X, Y> LiveData<X>.map(crossinline transform: (X) -> Y): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(this) {
            result.value = transform(it)
        }
        return result
    }

    fun addToCart(book: Book) {
        val currentMap = _cartMap.value ?: mutableMapOf()
        val bookId: Int = book.bookId

        if (currentMap.containsKey(bookId)) {
            currentMap[bookId]?.quantity = currentMap[bookId]!!.quantity + 1
        } else {
            currentMap[bookId] = CartItem(book, 1)
        }
        _cartMap.value = currentMap.toMutableMap()
    }

    fun updateCartItemQuantity(book: Book, newQuantity: Int) {
        val currentMap = _cartMap.value ?: return
        val bookId: Int = book.bookId

        if (newQuantity >= 1) {
            currentMap[bookId]?.quantity = newQuantity
        } else {
            currentMap.remove(bookId)
        }
        _cartMap.value = currentMap.toMutableMap()
    }

    fun removeFromCart(book: Book) {
        val currentMap = _cartMap.value ?: return
        currentMap.remove(book.bookId)
        _cartMap.value = currentMap.toMutableMap()
    }

    fun clearCart() {
        _cartMap.value = mutableMapOf()
    }

    // --- 3. Xử lý Bán hàng (Sale Logic) ---

    fun processBulkSale(sales: List<Sale>, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertBulkSalesAndUpdateInventory(sales)
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message ?: "Lỗi không xác định")
            }
        }
    }
}
