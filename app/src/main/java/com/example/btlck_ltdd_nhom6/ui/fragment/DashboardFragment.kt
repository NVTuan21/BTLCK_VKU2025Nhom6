package com.example.btlck_ltdd_nhom6.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.btlck_ltdd_nhom6.R
import com.example.btlck_ltdd_nhom6.data.AppDatabase
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import com.example.btlck_ltdd_nhom6.databinding.FragmentDashboardBinding
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModel
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModelFactory

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Khởi tạo ViewModel bằng Factory
    private val viewModel: BookstoreViewModel by viewModels {
        val context = requireContext().applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = BookstoreRepository(database.bookDao(), database.saleDao())

        BookstoreViewModelFactory(repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        observeViewModel()
        setupMenuListeners()
    }

    private fun observeViewModel() {
        // Quan sát tổng doanh thu và hiển thị trên Dashboard
        viewModel.totalRevenue.observe(viewLifecycleOwner) { revenueString ->
            // Giả định text_quick_revenue là TextView hiển thị tổng doanh thu
            binding.textQuickRevenue.text = "Tổng doanh thu: $revenueString"
        }
    }

    private fun setupMenuListeners() {
        // Điều hướng đến Màn hình Quản lý Sách (BookListFragment)
        binding.cardManageBooks.setOnClickListener {
            // Đảm bảo action_dashboardFragment_to_bookListFragment đã được định nghĩa trong nav_graph
            findNavController().navigate(R.id.action_dashboardFragment_to_bookListFragment)
        }

        // Điều hướng đến Màn hình Bán hàng (Tùy chọn)
        binding.cardMakeSale.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_saleFragment)
        }

        // Thêm các listener cho các CardView menu khác
        // binding.cardReports.setOnClickListener { /* ... */ }
    } // <--- Đóng khối setupMenuListeners() chính xác ở đây


    // Hàm onDestroyView() phải nằm ở cấp độ Fragment class
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}