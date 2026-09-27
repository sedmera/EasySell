package com.example.easysell.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.easysell.data.OrderRepository
import com.example.easysell.data.ProductRepository
import com.example.easysell.data.UserPreferencesRepository
import com.example.easysell.data.local.OrderEntity
import com.example.easysell.data.local.OrderItemEntity
import com.example.easysell.data.local.OrderWithItems
import com.example.easysell.data.local.Product
import com.example.easysell.data.local.generateOrderId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EasySellViewModel(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val products: StateFlow<List<Product>> =
        productRepository.allProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val orders: StateFlow<List<OrderEntity>> =
        orderRepository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val orderName: StateFlow<String> =
        userPreferencesRepository.orderName.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Objednávka"
        )

    fun getOrder(id: String): Flow<OrderWithItems?> {
        return orderRepository.getOrder(id)
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun setOrderName(name: String) {
        viewModelScope.launch {
            userPreferencesRepository.setOrderName(name)
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.deleteOrder(orderId)
        }
    }


    fun saveOrder(
        name: String,
        products: List<Product>,
        quantities: Map<Int, Int>
    ) {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return
        }

        val orderId = generateOrderId(cleanName)

        val items = products.mapNotNull { product ->
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

        if (items.isEmpty()) {
            return
        }

        val total = items.sumOf {
            it.unitPrice * it.quantity
        }

        val order = OrderEntity(
            id = orderId,
            name = cleanName,
            createdAt = System.currentTimeMillis(),
            total = total
        )

        viewModelScope.launch {
            orderRepository.saveOrder(
                order = order,
                items = items
            )
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val orderRepository: OrderRepository,
        val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(EasySellViewModel::class.java)) {
                return EasySellViewModel(
                    productRepository = productRepository,
                    orderRepository = orderRepository,
                    userPreferencesRepository = userPreferencesRepository
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}