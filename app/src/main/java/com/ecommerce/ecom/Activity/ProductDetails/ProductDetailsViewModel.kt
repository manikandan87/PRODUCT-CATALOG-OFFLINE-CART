package com.ecommerce.ecom.Activity.ProductDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.ecom.Model.Product
import com.ecommerce.ecom.Model.ProductRepository
import com.ecommerce.ecom.Room.Entity.CartItem
import kotlinx.coroutines.launch

class ProductDetailsViewModel(application: Application) : AndroidViewModel(application){

    private val productRepository = ProductRepository(application)
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    private val _cartItem = MutableLiveData<CartItem?>()
    val cartItem: LiveData<CartItem?> = _cartItem

    private val _cartCount = MutableLiveData<Int>()
    val cartCount: LiveData<Int> = _cartCount
    private var currentProduct: Product? = null


    fun setProduct(product: Product) {
        currentProduct = product
        _product.value = product
        getCartItem(product.id)
    }

    fun getCartItem(productId: Int) {
        viewModelScope.launch {
            val item = productRepository.getCartItem(productId)
            _cartItem.value = item
            _cartCount.value = item?.quantity ?: 0
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            _product.value?.let { product ->
                productRepository.addToCart(product)
                getCartItem(product.id)
            }
        }
    }

    fun updateQuantity(quantity: Int) {
        viewModelScope.launch {
            _product.value?.let { product ->
                if (quantity <= 0) {
                    productRepository.removeFromCart(product.id)
                } else {
                    productRepository.updateCartItemQuantity(product.id, quantity)
                }
                getCartItem(product.id)
            }
        }
    }

    fun isInCart(): Boolean {
        return _cartItem.value != null && (_cartItem.value?.quantity ?: 0) > 0
    }

    fun isInCartQuantity(): Int{
        return _cartItem.value?.quantity ?: 0
    }
}