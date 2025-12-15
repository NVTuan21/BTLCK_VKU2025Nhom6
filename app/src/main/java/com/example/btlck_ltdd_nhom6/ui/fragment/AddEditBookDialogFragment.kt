package com.example.btlck_ltdd_nhom6.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment // Quan trọng: Đổi từ Fragment sang DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.btlck_ltdd_nhom6.data.AppDatabase
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository
import com.example.btlck_ltdd_nhom6.databinding.FragmentAddEditBookBinding
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModel
import com.example.btlck_ltdd_nhom6.ui.viewmodel.BookstoreViewModelFactory

// Lưu ý: Đảm bảo tên file đã được đổi thành AddEditBookDialogFragment.kt
class AddEditBookDialogFragment : DialogFragment() {

    private var _binding: FragmentAddEditBookBinding? = null
    private val binding get() = _binding!!
    // Sử dụng AddEditBookFragmentArgs được sinh ra từ Navigation Component
    private val args: AddEditBookDialogFragmentArgs by navArgs()

    private var currentBookId: Int? = null

    // Khởi tạo ViewModel (Giữ nguyên logic khởi tạo)
    private val viewModel: BookstoreViewModel by viewModels {
        val context = requireContext().applicationContext
        val database = AppDatabase.getDatabase(context)
        val repository = BookstoreRepository(database.bookDao(), database.saleDao())
        BookstoreViewModelFactory(repository)
    }

    // Đảm bảo Dialog chiếm toàn bộ chiều rộng (trải nghiệm tốt hơn)
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng View Binding để inflate layout
        _binding = FragmentAddEditBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setTitle("Thêm / Sửa Sách") // Thiết lập tiêu đề cho Dialog

        setupMode()

        binding.buttonSave.setOnClickListener {
            saveBook()
        }

        // Nút Hủy để đóng Popup Hội thoại
        binding.buttonCancel.setOnClickListener {
            dismiss() // Đóng dialog
        }
    }

    private fun setupMode() {
        val bookIdString = args.bookId

        if (bookIdString != "new") {
            // Chế độ CHỈNH SỬA
            currentBookId = bookIdString.toIntOrNull()

            if (currentBookId != null) {
                binding.buttonSave.text = "CẬP NHẬT"
                loadBookData(currentBookId!!)
            } else {
                Toast.makeText(context, "Lỗi: ID sách không hợp lệ", Toast.LENGTH_SHORT).show()
                dismiss() // Đóng dialog nếu lỗi
            }
        } else {
            // Chế độ THÊM MỚI
            binding.buttonSave.text = "THÊM SÁCH"
        }
    }

    private fun loadBookData(id: Int) {
        // Logic load data giữ nguyên
        viewModel.getBookById(id).observe(viewLifecycleOwner) { book ->
            book?.let {
                binding.editTextTitle.setText(it.title)
                binding.editTextAuthor.setText(it.author)
                binding.editTextPrice.setText(it.price.toString())
                binding.editTextQuantity.setText(it.stockQuantity.toString())
            } ?: run {
                Toast.makeText(context, "Sách không tồn tại.", Toast.LENGTH_SHORT).show()
                dismiss() // Đóng dialog nếu không tìm thấy sách
            }
        }
    }

    private fun saveBook() {
        val title = binding.editTextTitle.text.toString().trim()
        val author = binding.editTextAuthor.text.toString().trim()
        val price = binding.editTextPrice.text.toString().toDoubleOrNull()
        val quantity = binding.editTextQuantity.text.toString().toIntOrNull()

        // Validation giữ nguyên
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(context, "Tên sách và Tác giả không được để trống.", Toast.LENGTH_SHORT).show()
            return
        }
        if (price == null || price <= 0.0) {
            Toast.makeText(context, "Giá bán phải là số dương hợp lệ.", Toast.LENGTH_SHORT).show()
            return
        }
        if (quantity == null || quantity < 0) {
            Toast.makeText(context, "Số lượng tồn kho phải là số nguyên không âm.", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo đối tượng Book để lưu
        val bookToSave = Book(
            id = currentBookId ?: 0,
            title = title,
            author = author,
            genre = "General",
            price = price,
            stockQuantity = quantity
        )

        viewModel.insertBook(bookToSave)

        Toast.makeText(context, "${bookToSave.title} đã được lưu.", Toast.LENGTH_SHORT).show()
        dismiss() // <--- QUAN TRỌNG: Đóng Dialog sau khi lưu thay vì findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Ngắt binding để tránh memory leak
        _binding = null
    }
}