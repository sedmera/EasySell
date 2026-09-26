package com.example.easysell.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdAt: Long,
    val total: Double
)