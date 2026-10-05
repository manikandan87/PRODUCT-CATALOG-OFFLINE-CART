package com.ecommerce.ecom.Activity.Dashboard

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.ecommerce.ecom.Activity.Cart.CartActivity
import com.ecommerce.ecom.Activity.Cart.CartViewModel
import com.ecommerce.ecom.Activity.ProductDetails.ProductDetailsActivity
import com.ecommerce.ecom.Adapter.ProductAdapter
import com.ecommerce.ecom.Model.Product
import com.ecommerce.ecom.R
import com.ecommerce.ecom.Utils.Resource
import com.ecommerce.ecom.databinding.ActivityDashboardBinding
import com.google.gson.Gson

class DashboardActivity : AppCompatActivity() {
    private lateinit var rvProducts: RecyclerView
    private lateinit var btnRetry: Button
    private lateinit var tvErrorMessage: TextView
    private lateinit var layoutError: LinearLayout
    private lateinit var tvEmpty: ImageView
    private lateinit var noConnection: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var toolbar: Toolbar
    private lateinit var searchEditText: EditText
    private lateinit var editBack: ImageView
    private lateinit var searchLayout: LinearLayout

    private lateinit var productAdapter: ProductAdapter
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        initialize()
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        setupSwipeRefresh()
    }

    private fun initialize() {
        rvProducts = findViewById(R.id.rvProducts)
        btnRetry = findViewById(R.id.btnRetry)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)
        layoutError = findViewById(R.id.layoutError)
        tvEmpty = findViewById(R.id.tvEmpty)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        toolbar = findViewById(R.id.toolbar)
        noConnection = findViewById(R.id.no_connection)
        searchEditText = findViewById(R.id.ET_search)
        editBack = findViewById(R.id.editBack)
        searchLayout = findViewById(R.id.searchLayout)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Products"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                true
            }
            R.id.action_search -> {
                searchLayout.visibility = View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                navigateToProductDetail(product)
            },
            onAddToCartClick = { product ->
                dashboardViewModel.addToCart(product)
            }
        )

        rvProducts.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = productAdapter
            setHasFixedSize(true)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                this@DashboardActivity,
                R.anim.layout_animation_fall_down
            )
        }
    }

    private fun setupListeners() {
        btnRetry.setOnClickListener {
            dashboardViewModel.fetchProducts()
        }

        editBack.setOnClickListener {
            searchLayout.visibility = View.GONE
            searchEditText.text.clear()
            dashboardViewModel.fetchProducts()
            hideKeyboard()
        }

        searchEditText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(editable: Editable?) {

            }

            override fun beforeTextChanged(
                char: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {

            }

            override fun onTextChanged(
                char: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {
                if (char!!.isNotEmpty()){
                    dashboardViewModel.searchProduct(char.toString())
                }
            }

        })
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            dashboardViewModel.fetchProducts()
        }
    }

    private fun navigateToProductDetail(product: Product) {
        val products = Gson().toJson(product)
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra("product", products)
        startActivity(intent)
    }

    private fun observeViewModel() {
        dashboardViewModel.products.observe(this) { resource ->
            swipeRefresh.isRefreshing = false
            when (resource) {
                is Resource.Loading -> {
                    if (productAdapter.itemCount == 0) {
                        progressBar.visibility = View.VISIBLE
                        rvProducts.visibility = View.GONE
                        tvEmpty.visibility = View.GONE
                        layoutError.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    val products = resource.data?.products ?: emptyList()
                    if (products.isNotEmpty()) {
                        productAdapter.submitList(products)
                        rvProducts.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                        layoutError.visibility = View.GONE
                        rvProducts.scheduleLayoutAnimation()
                    } else {
                        rvProducts.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                        layoutError.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    if (productAdapter.itemCount == 0) {
                        rvProducts.visibility = View.GONE
                        tvEmpty.visibility = View.GONE
                        layoutError.visibility = View.VISIBLE
                        noConnection.visibility = if (resource.message == "No internet connection") View.VISIBLE else View.GONE
                        tvErrorMessage.text = resource.message ?: "Something went wrong"
                    } else {
                        android.widget.Toast.makeText(
                            this,
                            resource.message ?: "Error loading products",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        dashboardViewModel.searchResults.observe(this) { resource ->

            when (resource) {

                is Resource.Loading -> {
                }

                is Resource.Success -> {

                    val products = resource.data?.products.orEmpty()

                    if (products.isEmpty()) {

                        rvProducts.visibility = View.GONE
                        layoutError.visibility = View.VISIBLE
                        Glide.with(this).load(R.drawable.no_search_found).into(noConnection)
                        btnRetry.visibility = View.GONE
                        tvErrorMessage.visibility = View.GONE

                    } else {

                        rvProducts.visibility = View.VISIBLE
                        layoutError.visibility = View.GONE

                        productAdapter.submitList(products)
                    }
                }

                is Resource.Error -> {
                    // show error
                }
            }
        }

        dashboardViewModel.cartItemCount.observe(this) { count ->
            invalidateOptionsMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.loadCartItems()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}