package com.ecommerce.ecom.Model

import android.content.Context
import com.ecommerce.ecom.Api.Api
import com.ecommerce.ecom.Api.AppModule
import com.ecommerce.ecom.Room.DAO
import com.ecommerce.ecom.Room.DatabaseModule
import com.ecommerce.ecom.Room.Entity.CartItem
import com.ecommerce.ecom.Utils.NetworkHelper
import com.ecommerce.ecom.Utils.Resource
import kotlinx.coroutines.flow.Flow

class ProductRepository (context: Context){
    private val api = AppModule.provideProductApi()
    private val cartDao = DatabaseModule.provideDao(context)
    private val networkHelper = AppModule.provideNetworkHelper(context)

    suspend fun getProducts(): Resource<ProductResponse> {
        return try {
            if (!networkHelper.isNetworkAvailable()) {
                return Resource.Error("No internet connection")
            }
            val response = api.getProducts()
            if (response.products.isNotEmpty()) {
                Resource.Success(response)
            } else {
                Resource.Error("No products available")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong")
        }
    }

    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }

    suspend fun addToCart(product: Product) {
        val existingItem = cartDao.getCartItemById(product.id)
        if (existingItem != null) {
            existingItem.quantity++
            cartDao.updateCartItem(existingItem)
        } else {
            val cartItem = CartItem(
                productId = product.id,
                title = product.title,
                price = product.price,
                thumbnail = product.thumbnail,
                quantity = 1
            )
            cartDao.insertCartItem(cartItem)
        }
    }

    suspend fun updateCartItemQuantity(productId: Int, quantity: Int) {
        val item = cartDao.getCartItemById(productId)
        item?.let {
            it.quantity = quantity
            if (quantity > 0) {
                cartDao.updateCartItem(it)
            } else {
                cartDao.deleteCartItem(it)
            }
        }
    }

    suspend fun removeFromCart(productId: Int) {
        val item = cartDao.getCartItemById(productId)
        item?.let {
            cartDao.deleteCartItem(it)
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun getTotalPrice(): Double {
        return cartDao.getTotalPrice() ?: 0.0
    }

    suspend fun getCartItem(productId: Int): CartItem? {
        return cartDao.getCartItemById(productId)
    }
}