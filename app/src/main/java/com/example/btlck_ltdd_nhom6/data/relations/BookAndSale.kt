package com.example.btlck_ltdd_nhom6.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.btlck_ltdd_nhom6.data.entity.Book
import com.example.btlck_ltdd_nhom6.data.entity.Sale


// Định nghĩa mối quan hệ "Một sách có nhiều giao dịch bán"
data class BookAndSale(

    // Nhúng (Embedded) thực thể chính (Sale)
    @Embedded val sale: Sale,

    // Định nghĩa mối quan hệ (JOIN)
    @Relation(
        // Khóa ngoại trong bảng Sale (chứa id của sách)
        parentColumn = "bookId",
        // Khóa chính tương ứng trong bảng Book
        entityColumn = "bookId"
    )
    // List này sẽ chứa cuốn sách được bán trong giao dịch này (nên chỉ có 1 phần tử)
    val bookDetails: List<Book>
)