package com.example.easysell.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProductCategory(val displayName: String) {
    FOOD("Jídlo"),
    DRINK("Nápoj"),
    COFFEE("Káva"),
    TEA("Čaj"),
    DESERT("Zákusek"),
    SWEET("Sladkost"),
    MISC("Ostatní")
}

@Entity(tableName = "products_table")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var price: Double,
    var category: ProductCategory
)