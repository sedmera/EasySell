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
}