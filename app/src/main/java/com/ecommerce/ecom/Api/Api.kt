package com.ecommerce.ecom.Api

import com.ecommerce.ecom.Model.ProductResponse
import retrofit2.http.GET

interface Api {

    @GET("products")
    suspend fun getProducts(): ProductResponse
}