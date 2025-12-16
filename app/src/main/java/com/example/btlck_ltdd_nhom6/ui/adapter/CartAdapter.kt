package com.example.btlck_ltdd_nhom6.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.btlck_ltdd_nhom6.databinding.ItemCartItemBinding // Tên layout cho mỗi mục giỏ hàng
import com.example.btlck_ltdd_nhom6.ui.fragment.CartItem // Import data class CartItem đã định nghĩa trong SaleFragment

/**
 * Adapter hiển thị các mục trong Giỏ hàng (CartItem).
 *
 * @param onQuantityChange Callback khi số lượng được thay đổi (tăng/giảm).
 * @param onRemoveItem     Callback khi người dùng nhấn nút xóa mục khỏi giỏ hàng.
 */
class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    inner class CartViewHolder(
        private val binding: ItemCartItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            val book = cartItem.book

            // 1. Hiển thị thông tin sách
            binding.textViewCartTitle.text = book.title
            binding.textViewCartPrice.text = String.format("Giá: %,.0f VNĐ", book.price)
            binding.textViewCartQuantity.text = cartItem.quantity.toString() // Hiển thị số lượng

            // 2. Xử lý sự kiện tăng số lượng
            binding.buttonIncreaseQuantity.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                onQuantityChange(cartItem, newQuantity)
            }

            // 3. Xử lý sự kiện giảm số lượng
            binding.buttonDecreaseQuantity.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                if (newQuantity >= 1) {
                    onQuantityChange(cartItem, newQuantity)
                } else {
                    // Nếu giảm về 0, hỏi người dùng có muốn xóa không hoặc chỉ giảm đến 1
                    onRemoveItem(cartItem) // Tôi chọn xóa khi nhấn giảm từ 1
                }
            }

            // 4. Xử lý sự kiện xóa mục
            binding.buttonRemoveItem.setOnClickListener {
                onRemoveItem(cartItem)
            }
        }
    }

    // Callback so sánh giữa các CartItem
    private class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Hai mục là cùng một Item nếu chúng có cùng Book ID
            return oldItem.book.bookId == newItem.book.bookId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Nội dung giống nhau nếu toàn bộ đối tượng CartItem giống nhau
            return oldItem == newItem
        }
    }
}