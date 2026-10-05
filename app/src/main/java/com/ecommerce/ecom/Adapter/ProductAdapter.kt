package com.ecommerce.ecom.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ecommerce.ecom.Model.Product
import com.ecommerce.ecom.R
import com.ecommerce.ecom.databinding.ItemProdctBinding

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products = listOf<Product>()
    private var lastPosition = -1

    fun submitList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProdctBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int = products.size

    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            view.startAnimation(
                AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
            )
            lastPosition = position
        }
    }

    inner class ProductViewHolder(
        private val binding: ItemProdctBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvTitle.text = product.title
                tvPrice.text = "$${"%.2f".format(product.price)}"
                tvRating.text = "⭐ ${product.rating ?: 0.0}"

                // Show discount if available
                if (product.discountPercentage != null && product.discountPercentage > 0) {
                    tvDiscount.visibility = View.VISIBLE
                    tvDiscount.text = "${product.discountPercentage.toInt()}% OFF"
                } else {
                    tvDiscount.visibility = View.GONE
                }

                // Load image with Glide with placeholder and error handling
                Glide.with(ivProduct.context)
                    .load(product.thumbnail)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivProduct)

                // Click listeners
                root.setOnClickListener {
                    onProductClick(product)
                    // Add ripple effect
                    root.isPressed = true
                }

                btnAddToCart.setOnClickListener {
                    onAddToCartClick(product)
                    // Animate button
                    btnAddToCart.startAnimation(
                        AnimationUtils.loadAnimation(btnAddToCart.context, R.anim.bounce)
                    )
                }

                // Show stock status
                if (product.stock != null && product.stock > 0) {
                    tvStock.text = "In Stock"
                    tvStock.setTextColor(root.context.getColor(R.color.green))
                } else {
                    tvStock.text = "Out of Stock"
                    tvStock.setTextColor(root.context.getColor(R.color.red))
                }
            }
        }
    }
}