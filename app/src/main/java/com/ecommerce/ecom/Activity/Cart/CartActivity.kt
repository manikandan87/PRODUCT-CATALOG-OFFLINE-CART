package com.ecommerce.ecom.Activity.Cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.ecom.Activity.Dashboard.DashboardActivity
import com.ecommerce.ecom.Adapter.CartAdapter
import com.ecommerce.ecom.R
import kotlin.compareTo
import kotlin.times

class CartActivity : AppCompatActivity() {
    private lateinit var btnCheckout: Button
    private lateinit var btnClearCart: Button
    private lateinit var tvGrandTotal: TextView
    private lateinit var tvTax: TextView
    private lateinit var tvDelivery: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var bottomLayout: LinearLayout
    private lateinit var rvCart: RecyclerView
    private lateinit var btnContinueShopping: Button
    private lateinit var tvEmpty: TextView
    private lateinit var toolbar: Toolbar

    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        initialize()
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

    }

    private fun initialize() {
        btnCheckout = findViewById(R.id.btnCheckout)
        btnClearCart = findViewById(R.id.btnClearCart)
        tvGrandTotal = findViewById(R.id.tvGrandTotal)
        tvTax = findViewById(R.id.tvTax)
        tvDelivery = findViewById(R.id.tvDelivery)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvItemCount = findViewById(R.id.tvItemCount)
        bottomLayout = findViewById(R.id.bottomLayout)
        rvCart = findViewById(R.id.rvCart)
        btnContinueShopping = findViewById(R.id.btnContinueShopping)
        tvEmpty = findViewById(R.id.tvEmpty)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Shopping Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { item, quantity ->
                cartViewModel.updateQuantity(item, quantity)
                if (quantity > 0) {
                    rvCart.scheduleLayoutAnimation()
                }
            },
            onDelete = { item ->
                cartViewModel.removeItem(item)
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            }
        )

        rvCart.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
            setHasFixedSize(true)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                this@CartActivity,
                R.anim.layout_animation_fall_down
            )
        }
    }

    private fun setupListeners() {
        btnCheckout.setOnClickListener {
            if (cartViewModel.getItemCount() > 0) {
                showCheckoutDialog()
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                btnCheckout.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.shake)
                )
            }
        }

        btnClearCart.setOnClickListener {
            if (cartViewModel.getItemCount() > 0) {
                showClearCartDialog()
            }
        }

        btnContinueShopping.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun observeViewModel() {
        cartViewModel.cartItems.observe(this) { items ->
            if (items.isEmpty()) {
                rvCart.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
                bottomLayout.visibility = View.GONE
                btnClearCart.visibility = View.GONE
                btnContinueShopping.visibility = View.VISIBLE
            } else {
                tvItemCount.text = "(${items.size} Items)"
                cartAdapter.submitList(items)
                rvCart.visibility = View.VISIBLE
                tvEmpty.visibility = View.GONE
                bottomLayout.visibility = View.VISIBLE
                btnClearCart.visibility = View.VISIBLE
                btnContinueShopping.visibility = View.GONE
                rvCart.scheduleLayoutAnimation()
            }
        }

        cartViewModel.totalPrice.observe(this) { total ->
            tvSubtotal.text = "$${"%.2f".format(total)}"

            val deliveryFee = if (total >= 50.0) 0.0 else 5.99
            val tax = total * 0.10
            val grandTotal = total + deliveryFee + tax

            tvDelivery.text = if (deliveryFee == 0.0) "Free" else "$${"%.2f".format(deliveryFee)}"
            tvTax.text = "$${"%.2f".format(tax)}"
            tvGrandTotal.text = "$${"%.2f".format(grandTotal)}"
        }
    }

    private fun showCheckoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Order")
            .setMessage("Would you like to proceed with your order?")
            .setPositiveButton("Place Order") { _, _ ->
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                cartViewModel.clearCart()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearCartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Clear Cart")
            .setMessage("Are you sure you want to remove all items from your cart?")
            .setPositiveButton("Clear") { _, _ ->
                cartViewModel.clearCart()
                Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}