package com.ecommerce.ecom.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ecommerce.ecom.Room.Entity.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {

    // Cart Item

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemById(productId: Int): CartItem?

    @Query("SELECT SUM(price * quantity) FROM cart_items")
    suspend fun getTotalPrice(): Double?
}