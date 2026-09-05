package com.example.easysell.data

enum class GoodsCategory(val displayName: String) {
    FOOD("Jídlo"),
    DRINK("Nápoj"),
    COFFEE("Káva"),
    TEA("Čaj"),
    DESERT("Zákusek"),
    SWEET("Sladkost"),
    MISC("Ostatní")
}

data class Goods(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val category: GoodsCategory
)