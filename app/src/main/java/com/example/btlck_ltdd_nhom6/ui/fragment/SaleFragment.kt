package com.example.btlck_ltdd_nhom6.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btlck_ltdd_nhom6.R
import com.example.btlck_ltdd_nhom6.data.AppDatabase
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.entity.Sale
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import com.example.btlck_ltdd_nhom6.databinding.FragmentSaleBinding
import com.example.btlck_ltdd_nhom6.ui.adapter.CartAdapter
import com.example.btlck_ltdd_nhom6.ui.adapter.SearchBookAdapter
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModel
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModelFactory

// Data Class tạm thời cho mục trong Giỏ hàng
data class CartItem(
    val book: Book,
    var quantity: Int
)

class SaleFragment : Fragment(R.layout.fragment_sale) {

    private var _binding: FragmentSaleBinding? = null
    private val binding get() = _binding!!

    // ViewModel được sử dụng cho cả quản lý sách và xử lý bán hàng
    private val viewModel: BookstoreViewModel by viewModels {
        val context = requireContext().applicationContext
        val database = AppDatabase.getDatabase(context)
        // Đảm bảo Repository và ViewModelFactory đã được tạo
        val repository = BookstoreRepository(database.bookDao(), database.saleDao(), database)

        BookstoreViewModelFactory(repository)
    }

    private lateinit var searchAdapter: SearchBookAdapter
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSaleBinding.bind(view)

        setupAdapters()
        setupSearchWatcher()
        observeData()

        // SỬA: Truy cập Button thông qua layout cha (layoutCheckout)
        binding.buttonProcessSale.setOnClickListener {
            // Lấy dữ liệu Giỏ hàng từ ViewModel để xử lý
            processSale(viewModel.cartItems.value.orEmpty())
        }
    }

    // --- CÀI ĐẶT CÁC THÀNH PHẦN MỚI ---

    /** Thiết lập Adapter và RecyclerView */
    private fun setupAdapters() {
        // 1. Adapter cho kết quả tìm kiếm (RecyclerView: recycler_view_search_results)
        searchAdapter = SearchBookAdapter(onBookClicked = { book ->
            // Khi sách được chọn, thêm vào giỏ hàng
            viewModel.addToCart(book)
            // Xóa nội dung tìm kiếm và ẩn kết quả
            binding.inputSearchBook.editText?.text?.clear()
            binding.recyclerViewSearchResults.visibility = View.GONE
        })
        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        // 2. Adapter cho Giỏ hàng (RecyclerView: recycler_view_cart)
        cartAdapter = CartAdapter(
            onQuantityChange = { cartItem, newQuantity ->
                // Đảm bảo số lượng không âm
                if (newQuantity >= 1) {
                    viewModel.updateCartItemQuantity(cartItem.book, newQuantity)
                } else {
                    // Nếu số lượng là 0, thì xóa khỏi giỏ hàng
                    viewModel.removeFromCart(cartItem.book)
                }
            },
            onRemoveItem = { cartItem ->
                viewModel.removeFromCart(cartItem.book)
            }
        )
        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    /** Thiết lập lắng nghe cho EditText tìm kiếm */
    private fun setupSearchWatcher() {
        // Sử dụng doAfterTextChanged để lắng nghe thay đổi
        binding.inputSearchBook.editText?.doAfterTextChanged { editable ->
            val query = editable.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchBooks(query)
                // Hiển thị kết quả tìm kiếm khi có query
                binding.recyclerViewSearchResults.visibility = View.VISIBLE
            } else {
                // Ẩn kết quả tìm kiếm khi trống
                searchAdapter.submitList(emptyList())
                binding.recyclerViewSearchResults.visibility = View.GONE
            }
        }
    }

    /** Theo dõi dữ liệu Cart và Search Results từ ViewModel */
    private fun observeData() {
        // Theo dõi kết quả tìm kiếm
        viewModel.searchResults.observe(viewLifecycleOwner) { books ->
            searchAdapter.submitList(books)
            // Đảm bảo hiển thị kết quả nếu có, chỉ khi EditText không trống
            if (books.isNotEmpty() && binding.inputSearchBook.editText?.text?.isNotEmpty() == true) {
                binding.recyclerViewSearchResults.visibility = View.VISIBLE
            } else if (binding.inputSearchBook.editText?.text?.isNotEmpty() == true) {
                // Nếu không tìm thấy sách, vẫn ẩn nếu muốn tối ưu không gian
                binding.recyclerViewSearchResults.visibility = View.GONE
            }
        }

        // Theo dõi dữ liệu Giỏ hàng
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)
            updateTotalPrice(cartItems)
        }
    }

    /** Cập nhật UI hiển thị tổng tiền (Sử dụng ID mới: text_total_amount) */
    private fun updateTotalPrice(cartItems: List<CartItem>) {
        // Tính tổng tiền
        val total = cartItems.sumOf { it.book.price * it.quantity }

        // Định dạng và hiển thị (Giả định giá là Float/Double)
        val formattedTotal = String.format("TỔNG CỘNG: %,.0f VNĐ", total)
        binding.textTotalAmount.text = formattedTotal
    }


    private fun processSale(cartItems: List<CartItem>) {
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Giỏ hàng trống. Vui lòng thêm sách.", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuyển CartItem thành Sale
        val salesToProcess = cartItems.map { item ->
            Sale(
                saleId = 0,
                bookId = item.book.bookId,
                quantitySold = item.quantity,
                totalPrice = item.book.price * item.quantity,
            )
        }

        // Gọi ViewModel để xử lý nghiệp vụ
        viewModel.processBulkSale(salesToProcess) { success, error ->
            if (success) {
                Toast.makeText(context, "Thanh toán thành công! Đã cập nhật tồn kho.", Toast.LENGTH_LONG).show()
                viewModel.clearCart()
            } else {
                Toast.makeText(context, "Lỗi bán hàng: ${error ?: "Lỗi không xác định hoặc không đủ tồn kho"}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}