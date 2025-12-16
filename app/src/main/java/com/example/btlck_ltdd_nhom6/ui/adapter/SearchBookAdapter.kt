package com.example.btlck_ltdd_nhom6.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.databinding.ItemBookSearchResultBinding // Tên layout cho mỗi mục tìm kiếm

/**
 * Adapter hiển thị kết quả tìm kiếm sách.
 *
 * @param onBookClicked Lambda function được gọi khi một cuốn sách được click,
 * truyền vào đối tượng Book được chọn.
 */
class SearchBookAdapter(
    private val onBookClicked: (Book) -> Unit
) : ListAdapter<Book, SearchBookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

    inner class BookViewHolder(
        private val binding: ItemBookSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Thiết lập listener cho toàn bộ item
            val onClickListener =
                binding.imageButtonAddToCart.setOnClickListener { // ID mới trong layout
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onBookClicked(getItem(position))
                    }
                }
        }

        fun bind(book: Book) {
            // Thay thế bằng ID của các TextView trong layout item_search_book.xml của bạn
            binding.textViewBookTitle.text = book.title
            binding.textViewBookAuthor.text = book.author
            binding.textViewPrice.text = String.format("%,.0f VNĐ", book.price)
        }
    }

    // Callback so sánh giữa các Book để cập nhật hiệu quả RecyclerView
    private class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.bookId == newItem.bookId
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}

