package com.example.easysell.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.easysell.data.ProductRepository
import com.example.easysell.data.local.OrderDao
import com.example.easysell.data.local.OrderEntity
import com.example.easysell.data.local.OrderItemEntity
import com.example.easysell.data.local.Product
import com.example.easysell.data.local.generateOrderId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EasySellViewModel(private val repository: ProductRepository) : ViewModel() {

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    class Factory(private val repository: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EasySellViewModel::class.java)) {
                return EasySellViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    suspend fun saveOrder(
        name: String,
        products: List<Product>,
        quantities: Map<Int, Int>
    ) {
        val orderId = generateOrderId(name)

        val items = products
            .mapNotNull { product ->
                val quantity = quantities[product.id] ?: 0

                if (quantity <= 0) {
                    null
                } else {
                    OrderItemEntity(
                        orderId = orderId,
                        productId = product.id,
                        productName = product.name,
                        unitPrice = product.price,
                        quantity = quantity
                    )
                }
            }

        val total = items.sumOf {
            it.unitPrice * it.quantity
        }

        OrderDao.saveOrder(
            OrderEntity(
                id = orderId,
                name = name,
                createdAt = System.currentTimeMillis(),
                total = total
            ),
            items
        )
    }
}