package com.ecommerce.ecom.Activity.Dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommerce.ecom.Model.Product
import com.ecommerce.ecom.Model.ProductRepository
import com.ecommerce.ecom.Model.ProductResponse
import com.ecommerce.ecom.Room.Entity.CartItem
import com.ecommerce.ecom.Utils.Resource
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application): AndroidViewModel(application) {

    private val productRepository =  ProductRepository(application)
    private val _products = MutableLiveData<Resource<ProductResponse>>()
    val products: LiveData<Resource<ProductResponse>> = _products

    private val _searchResults = MutableLiveData<Resource<ProductResponse>>()
    val searchResults: LiveData<Resource<ProductResponse>> = _searchResults

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> = _cartItemCount

    private var allProducts: List<Product> = emptyList()

    init {
        fetchProducts()
        loadCartItems()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            val result = productRepository.getProducts()
            _products.value = result

            if (result is Resource.Success) {

                allProducts = result.data?.products.orEmpty()
                _searchResults.value = result
            }
        }
    }

    fun searchProduct(query: String){
        val text = query.trim()

        if (text.isEmpty()) {
            val result = _products.value
            if (result is Resource.Success) {
                _searchResults.value = result!!
            }
            return
        }

        val filteredData = allProducts.filter { product ->
            product.title.contains(
                text,
                ignoreCase = true
            ) ||
                    product.brand?.contains(
                        text,
                        ignoreCase = true
                    ) == true ||

                    product.category?.contains(
                        text,
                        ignoreCase = true
                    ) == true
        }

        val originalResult = _products.value

        if (originalResult is Resource.Success) {

            val filteredResponse =
                originalResult.data?.copy(
                    products = filteredData,
                    total = filteredData.size
                )

            if (filteredResponse != null) {
                _searchResults.value =
                    Resource.Success(filteredResponse)
            }
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            productRepository.getCartItems().collect { items ->
                _cartItems.value = items
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.addToCart(product)
                loadCartItems()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }



    fun isProductInCart(productId: Int): Boolean {
        return _cartItems.value?.any { it.productId == productId } ?: false
    }
}