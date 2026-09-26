package com.example.easysell.data

import com.example.easysell.data.local.OrderDao
import com.example.easysell.data.local.OrderEntity
import com.example.easysell.data.local.OrderItemEntity
import com.example.easysell.data.local.OrderWithItems
import com.example.easysell.data.local.Product
import com.example.easysell.data.local.generateOrderId
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val orderDao: OrderDao
) {
    val allOrders: Flow<List<OrderEntity>> =
        orderDao.getAllOrders()

    fun getOrder(id: String): Flow<OrderWithItems?> =
        orderDao.getOrderWithItems(id)

    suspend fun saveOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ) {
        orderDao.saveOrder(order, items)
    }
}