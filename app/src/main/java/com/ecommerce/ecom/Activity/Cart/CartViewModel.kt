package com.ecommerce.ecom.Activity.Cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.ecom.Model.ProductRepository
import com.ecommerce.ecom.Room.Entity.CartItem
import kotlinx.coroutines.launch

class CartViewModel(application: Application): AndroidViewModel(application){

    private val productRepository = ProductRepository(application)
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        viewModelScope.launch {
            productRepository.getCartItems().collect { items ->
                _cartItems.value = items
                calculateTotalPrice()
            }
        }
    }

    fun calculateTotalPrice() {
        viewModelScope.launch {
            val total = productRepository.getTotalPrice()
            _totalPrice.value = total
        }
    }

    fun updateQuantity(item: CartItem, quantity: Int) {
        viewModelScope.launch {
            productRepository.updateCartItemQuantity(item.productId, quantity)
            loadCartItems()
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            productRepository.removeFromCart(item.productId)
            loadCartItems()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            productRepository.clearCart()
            loadCartItems()
        }
    }

    fun getItemCount(): Int {
        return _cartItems.value?.size ?: 0
    }


    fun getTotalItems(): Int {
        return _cartItems.value?.sumOf { it.quantity } ?: 0
    }
}