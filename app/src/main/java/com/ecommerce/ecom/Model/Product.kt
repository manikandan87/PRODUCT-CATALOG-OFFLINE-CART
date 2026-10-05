package com.ecommerce.ecom.Model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double? = null,
    val rating: Double? = null,
    val stock: Int? = null,
    val brand: String? = null,
    val category: String? = null,
    val thumbnail: String,
    val images: List<String>? = null
)
