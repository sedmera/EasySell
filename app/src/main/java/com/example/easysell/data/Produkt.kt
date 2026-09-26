package com.example.easysell.data

enum class ProductCategory(val displayName: String) {
    FOOD("Jídlo"),
    DRINK("Nápoj"),
    COFFEE("Káva"),
    TEA("Čaj"),
    DESERT("Zákusek"),
    SWEET("Sladkost"),
    MISC("Ostatní")
}

data class Produkt(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val category: ProductCategory
)