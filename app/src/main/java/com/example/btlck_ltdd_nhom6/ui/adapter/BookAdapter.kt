package com.example.btlck_ltdd_nhom6.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.databinding.ItemBookBinding

class BookAdapter(private val onClick: (Book) -> Unit) :
    ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    public override fun getItem(position: Int): Book {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.textBookTitle.text = book.title
            binding.textBookAuthor.text = "Tác giả: ${book.author}"
            binding.textStock.text = "Tồn kho: ${book.stockQuantity}"

            // Xử lý sự kiện nhấn vào mục
            binding.root.setOnClickListener {
                onClick(book)
            }
        }
    }
}

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        // Kiểm tra tất cả các trường dữ liệu có thay đổi không
        return oldItem == newItem
    }
}