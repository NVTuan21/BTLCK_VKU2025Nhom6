package com.example.btlck_ltdd_nhom6.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import kotlinx.coroutines.launch

class BookstoreViewModel(private val repository: BookstoreRepository) : ViewModel() {

    // -------------------------------------------------------------------
    // I. DATA FOR VIEW (SÁCH)
    // -------------------------------------------------------------------

    /**
     * Lấy tất cả sách từ Repository.
     * Repository cung cấp Flow, chúng ta chuyển nó thành LiveData
     * để View (Fragment/Activity) có thể quan sát.
     */
    val allBooks: LiveData<List<Book>> = repository.allBooks.asLiveData()

    // -------------------------------------------------------------------
    // II. DATA FOR VIEW (GIAO DỊCH VÀ THỐNG KÊ)
    // -------------------------------------------------------------------

    // Lấy danh sách giao dịch bán hàng kèm chi tiết sách
    val salesWithBookDetails = repository.allSalesWithDetails.asLiveData()

    // Lấy tổng doanh thu (sử dụng LiveData Transformations để định dạng nếu cần)
    val totalRevenue: LiveData<String> = repository.totalRevenue.asLiveData().map { total ->
        // Logic chuyển đổi: định dạng số tiền thành chuỗi tiền tệ
        if (total != null) {
            "Doanh thu: ${String.format("%,.0f", total)} VND"
        } else {
            "Doanh thu: 0 VND"
        }
    }

    // -------------------------------------------------------------------
    // III. HÀM XỬ LÝ NGHIỆP VỤ (GỌI REPOSITORY)
    // -------------------------------------------------------------------

    fun getBookById(id: Int): LiveData<Book?> {
        // Giả định Repository có hàm getBookById trả về LiveData/Flow từ Room.
        // Nếu Repository trả về Flow, ta dùng .asLiveData()
        return repository.getBookById(id).asLiveData()
    }

    // Thêm/Cập nhật sách
    fun insertBook(book: Book) = viewModelScope.launch {
        repository.insertBook(book)
    }

    // Xóa sách
    fun deleteBook(book: Book) = viewModelScope.launch {
        repository.deleteBook(book)
    }

    /**
     * Thực hiện giao dịch bán hàng
     * @param sale: Giao dịch bán hàng (chứa bookId và quantitySold)
     * @param onComplete: Callback để thông báo thành công hay thất bại về View
     */
    fun processSale(sale: Sale, onComplete: (Boolean, String?) -> Unit) = viewModelScope.launch {
        try {
            repository.processSaleTransaction(sale)
            onComplete(true, null) // Bán hàng thành công
        } catch (e: Exception) {
            // Xử lý lỗi từ Repository (ví dụ: không đủ tồn kho)
            onComplete(false, e.message)
        }
    }
}