package com.ecommerce.ecom.Activity.ProductDetails

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ecommerce.ecom.Activity.Cart.CartActivity
import com.ecommerce.ecom.Model.Product
import com.ecommerce.ecom.R
import com.google.gson.Gson
import kotlin.compareTo
import kotlin.text.category
import kotlin.toString

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var btnGoToCart : Button
    private lateinit var btnAddToCart : Button
    private lateinit var ibIncrease : ImageButton
    private lateinit var tvQuantity : TextView
    private lateinit var ibDecrease : ImageButton
    private lateinit var llQuantity : LinearLayout
    private lateinit var tvDescription : TextView
    private lateinit var tvPrice : TextView
    private lateinit var tvRating : TextView
    private lateinit var tvCategory : TextView
    private lateinit var tvBrand : TextView
    private lateinit var tvTitle : TextView
    private lateinit var ivProduct : ImageView
    private lateinit var ivBack : ImageView
    private lateinit var productDetailViewModel: ProductDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        val json = intent.getStringExtra("product")
        val product = Gson().fromJson(json, Product::class.java)

        productDetailViewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]

        product?.let {
            productDetailViewModel.setProduct(it)
        } ?: run {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initialize()
        setupListeners()
        observeViewModel()
        updateCartButton()
        updateCartQuantity()
    }

    private fun updateCartQuantity() {
        tvQuantity.text = productDetailViewModel.isInCartQuantity().toString()
    }

    private fun initialize() {
        btnGoToCart = findViewById(R.id.btnGoToCart)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        ibIncrease = findViewById(R.id.ibIncrease)
        tvQuantity = findViewById(R.id.tvQuantity)
        ibDecrease = findViewById(R.id.ibDecrease)
        llQuantity = findViewById(R.id.llQuantity)
        tvDescription = findViewById(R.id.tvDescription)
        tvPrice = findViewById(R.id.tvPrice)
        tvRating = findViewById(R.id.tvRating)
        tvCategory = findViewById(R.id.tvCategory)
        tvBrand = findViewById(R.id.tvBrand)
        tvTitle = findViewById(R.id.tvTitle)
        ivProduct = findViewById(R.id.ivProduct)
        ivBack = findViewById(R.id.ivBack)
    }

    private fun setupListeners() {
        btnAddToCart.setOnClickListener {
            productDetailViewModel.addToCart()
            btnAddToCart.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.bounce)
            )
            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
        }

        btnGoToCart.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
        }

        ibIncrease.setOnClickListener {
            val currentQuantity = tvQuantity.text.toString().toIntOrNull() ?: 0
            val newQuantity = currentQuantity + 1
            productDetailViewModel.updateQuantity(newQuantity)
            tvQuantity.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.pop)
            )
        }

        ibDecrease.setOnClickListener {
            val currentQuantity = tvQuantity.text.toString().toIntOrNull() ?: 0
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                productDetailViewModel.updateQuantity(newQuantity)
                tvQuantity.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.pop)
                )
            } else if (currentQuantity == 1) {
                productDetailViewModel.updateQuantity(0)
            }
        }

        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        productDetailViewModel.product.observe(this) { product ->
            product?.let {
                displayProductDetails(it)
            }
        }

        productDetailViewModel.cartItem.observe(this) { cartItem ->
            if (cartItem != null && cartItem.quantity > 0) {
                llQuantity.visibility = android.view.View.VISIBLE
                btnAddToCart.visibility = android.view.View.GONE
                btnGoToCart.visibility = android.view.View.VISIBLE
                tvQuantity.text = cartItem.quantity.toString()
                updateCartButton()
            } else {
                llQuantity.visibility = android.view.View.GONE
                btnAddToCart.visibility = android.view.View.VISIBLE
                btnGoToCart.visibility = android.view.View.GONE
                updateCartButton()
            }
        }

        productDetailViewModel.cartCount.observe(this) { count ->
            tvQuantity.text = count.toString()
        }
    }

    private fun displayProductDetails(product: Product) {
        tvTitle.text = product.title
        tvPrice.text = "$${"%.2f".format(product.price)}"
        tvDescription.text = product.description
        tvRating.text = "⭐ ${product.rating ?: 0.0} / 5.0"

        if (!product.category.isNullOrEmpty()) {
            tvCategory.text = product.category
            tvCategory.visibility = android.view.View.VISIBLE
        } else {
            tvCategory.visibility = android.view.View.GONE
        }

        Glide.with(ivProduct.context)
            .load(product.thumbnail)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(ivProduct)

        if (!product.brand.isNullOrEmpty()) {
            tvBrand.text = "Brand: ${product.brand}"
            tvBrand.visibility = android.view.View.VISIBLE
        } else {
            tvBrand.visibility = android.view.View.GONE
        }

    }

    private fun updateCartButton() {
        btnAddToCart.text = if (productDetailViewModel.isInCart()) {
            "Update Cart"
        } else {
            "Add to Cart"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }
}