package com.example.btlck_ltdd_nhom6.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.btlck_ltdd_nhom6.R
import com.example.btlck_ltdd_nhom6.data.AppDatabase
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import com.example.btlck_ltdd_nhom6.databinding.FragmentSaleBinding
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModel
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModelFactory
import java.util.Date
import java.util.UUID

class SaleFragment : Fragment(R.layout.fragment_sale) {

    private var _binding: FragmentSaleBinding? = null
    private val binding get() = _binding!!

    // ViewModel được sử dụng cho cả quản lý sách và xử lý bán hàng
    private val viewModel: BookstoreViewModel by viewModels {
        val context = requireContext().applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = BookstoreRepository(database.bookDao(), database.saleDao())

        BookstoreViewModelFactory(repository)
    }

    // Các Adapter cần thiết
    // private lateinit var searchAdapter: SearchBookAdapter // Adapter cho kết quả tìm kiếm
    // private lateinit var cartAdapter: CartAdapter         // Adapter cho giỏ hàng

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSaleBinding.bind(view)

        // setupAdapters()
        // setupSearchWatcher()
        // observeCartData()

        binding.buttonProcessSale.setOnClickListener {
            // Đây là nơi gọi hàm xử lý nghiệp vụ chính
            processSale()
        }
    }

    // Hàm giả lập việc thực hiện giao dịch
    private fun processSale() {
        // GIẢ ĐỊNH: Lấy thông tin bán hàng từ Giỏ hàng (chưa được cài đặt)
        // Đây là ví dụ về một giao dịch đơn giản

        val bookIdToSell = 1 // Giả sử ID sách là 1
        val quantitySold = 2
        val totalPrice = 100000.0 // Giả định giá

        // Tạo đối tượng Sale
        val sale = Sale(
            saleId = 0,
            bookId = bookIdToSell,
            quantitySold = quantitySold,
            totalPrice = totalPrice,
        )

        // Gọi ViewModel để xử lý nghiệp vụ chính
        viewModel.processSale(sale) { success, error ->
            if (success) {
                Toast.makeText(context, "Thanh toán thành công! Đã cập nhật tồn kho.", Toast.LENGTH_LONG).show()
                // Xóa giỏ hàng và reset giao diện
                // clearCart()
            } else {
                Toast.makeText(context, "Lỗi bán hàng: ${error ?: "Không đủ tồn kho"}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}