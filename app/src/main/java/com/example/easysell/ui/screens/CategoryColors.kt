package com.example.easysell.ui.screens

import androidx.compose.ui.graphics.Color
import com.example.easysell.data.local.ProductCategory

data class CategoryColors(
    val container: Color,
    val content: Color
)

fun ProductCategory.colors(): CategoryColors {
    return when (this) {
        ProductCategory.FOOD -> CategoryColors(
            container = Color(0xFFE8F5E9),
            content = Color(0xFF1B5E20)
        )

        ProductCategory.DRINK -> CategoryColors(
            container = Color(0xFFE3F2FD),
            content = Color(0xFF0D47A1)
        )

        ProductCategory.COFFEE -> CategoryColors(
            container = Color(0xFFEFEBE9),
            content = Color(0xFF3E2723)
        )

        ProductCategory.TEA -> CategoryColors(
            container = Color(0xFFE0F2F1),
            content = Color(0xFF004D40)
        )

        ProductCategory.DESERT -> CategoryColors(
            container = Color(0xFFF3E5F5),
            content = Color(0xFF4A148C)
        )

        ProductCategory.SWEET -> CategoryColors(
            container = Color(0xFFFCE4EC),
            content = Color(0xFF880E4F)
        )

        ProductCategory.MISC -> CategoryColors(
            container = Color(0xFFF5F5F5),
            content = Color(0xFF212121)
        )
    }
}