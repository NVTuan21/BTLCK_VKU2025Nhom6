package com.example.btlck_ltdd_nhom6.ui.fragment

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.btlck_ltdd_nhom6.R
import com.example.btlck_ltdd_nhom6.data.AppDatabase
import com.example.btlck_ltdd_nhom6.databinding.FragmentBookListBinding
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import com.example.btlck_ltdd_nhom6.ui.adapter.BookAdapter
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModel
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModelFactory

class BookListFragment : Fragment(R.layout.fragment_book_list) {

    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter

    // Khởi tạo ViewModel bằng Factory (lặp lại cấu trúc Factory)
    private val viewModel: BookstoreViewModel by viewModels {
        val context = requireContext().applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = BookstoreRepository(bookDao = database.bookDao(), saleDao = database.saleDao(), database = database)

        BookstoreViewModelFactory(repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()

        // Nút Thêm sách mới
        binding.fabAddBook.setOnClickListener {
            // Truyền "new" để báo hiệu chế độ Thêm mới
            val action = BookListFragmentDirections.actionBookListFragmentToAddEditBookFragment(
                bookId = "new",
                title = "Thêm Sách Mới"
            )
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        // 1. Adapter với logic click (điều hướng đến chỉnh sửa)
        bookAdapter = BookAdapter { book ->
            val action = BookListFragmentDirections.actionBookListFragmentToAddEditBookFragment(
                bookId = book.bookId.toString(),
                title = "Chỉnh Sửa Sách"
            )
            findNavController().navigate(action)
        }
        binding.recyclerViewBooks.adapter = bookAdapter

        // 2. Swipe-to-Delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rV: RecyclerView, vH: RecyclerView.ViewHolder, tV: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 1. Lấy vị trí của mục bị vuốt
                val position = viewHolder.adapterPosition

                // 2. Lấy đối tượng Book một cách an toàn
                // Nếu BookAdapter kế thừa ListAdapter, sử dụng getItem là cách an toàn nhất
                val bookToDelete = (binding.recyclerViewBooks.adapter as? BookAdapter)?.getItem(position)

                if (bookToDelete != null) {
                    // Xóa sách
                    viewModel.deleteBook(bookToDelete)

                    // Snackbar Hoàn tác
                    Snackbar.make(binding.root, "Đã xóa: ${bookToDelete.title}", Snackbar.LENGTH_LONG)
                        .setAction("HOÀN TÁC") {
                            viewModel.insertBook(bookToDelete) // Chèn lại
                        }.show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewBooks)
    }

    private fun observeViewModel() {
        // 1. Quan sát danh sách sách
        viewModel.allBooks.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
        }

        // 2. Quan sát tổng doanh thu (Có thể hiển thị ở đây nếu cần, nhưng thường đặt ở Dashboard)
        viewModel.totalRevenue.observe(viewLifecycleOwner) { revenueString ->
            binding.textTotalRevenue.text = revenueString // Giả định layout có view này
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}