package com.example.easysell.data

import com.example.easysell.data.local.Product
import com.example.easysell.data.local.ProductCategory

object SeedData {

    val products = listOf(
        // =====================================
        // === Káva ============================
        // =====================================
        Product(
            name = "Espresso",
            price = 40.0,
            category = ProductCategory.COFFEE
        ),
        Product(
            name = "Lungo",
            price = 50.0,
            category = ProductCategory.COFFEE
        ),
        Product(
            name = "Cappucchino",
            price = 60.0,
            category = ProductCategory.COFFEE
        ),
        Product(
            name = "Flat White",
            price = 70.0,
            category = ProductCategory.COFFEE
        ),
        Product(
            name = "Latte Macchiato",
            price = 65.0,
            category = ProductCategory.COFFEE
        ),
        Product(
            name = "Čaj",
            price = 20.0,
            category = ProductCategory.TEA
        ),
        Product(
            name = "Příchuť",
            price = 10.0,
            category = ProductCategory.MISC
        ),
        // =====================================
        // === ZÁKUSKY =========================
        // =====================================
        Product(
            name = "Choux Čok.",
            price = 40.0,
            category = ProductCategory.DESERT
        ),
        Product(
            name = "Choux Van.",
            price = 40.0,
            category = ProductCategory.DESERT
        ),
        Product(
            name = "Makový Cheese",
            price = 50.0,
            category = ProductCategory.DESERT
        ),
        Product(
            name = "Tartaletka",
            price = 50.0,
            category = ProductCategory.DESERT
        ),
        Product(
            name = "Čoko Dort",
            price = 60.0,
            category = ProductCategory.DESERT
        )
    )
}