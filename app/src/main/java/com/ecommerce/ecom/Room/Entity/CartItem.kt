package com.ecommerce.ecom.Room.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    var quantity: Int = 1
)