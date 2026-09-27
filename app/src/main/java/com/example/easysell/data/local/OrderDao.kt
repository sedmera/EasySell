package com.example.easysell.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>): List<Long>

    @Transaction
    suspend fun saveOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ): String {
        insertOrder(order)
        insertItems(items)
        return order.id
    }

    @Query("""
        SELECT * FROM orders
        ORDER BY createdAt DESC
    """)
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Transaction
    @Query("""
        SELECT * FROM orders
        WHERE id = :orderId
    """)
    fun getOrderWithItems(orderId: String): Flow<OrderWithItems?>

    @Query("""
    DELETE FROM order_items
    WHERE orderId = :orderId
""")
    suspend fun deleteOrderItems(orderId: String): Int

    @Query("""
    DELETE FROM orders
    WHERE id = :orderId
""")
    suspend fun deleteOrderEntity(orderId: String): Int

    @Transaction
    suspend fun deleteOrder(orderId: String): Int {
        deleteOrderItems(orderId)
        return deleteOrderEntity(orderId)
    }
}