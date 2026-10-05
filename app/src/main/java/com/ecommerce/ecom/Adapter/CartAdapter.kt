package com.ecommerce.ecom.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ecommerce.ecom.R
import com.ecommerce.ecom.Room.Entity.CartItem
import com.ecommerce.ecom.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onDelete: (CartItem) -> Unit
)  : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems = listOf<CartItem>()

    fun submitList(items: List<CartItem>) {
        cartItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onViewDetachedFromWindow(holder: CartViewHolder) {
        super.onViewDetachedFromWindow(holder)
        // Clear any pending animations
        holder.itemView.clearAnimation()
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.apply {
                tvTitle.text = item.title
                tvPrice.text = "$${"%.2f".format(item.price)}"
                tvQuantity.text = item.quantity.toString()

                // Calculate and display total for this item
                val itemTotal = item.price * item.quantity
                tvItemTotal.text = "$${"%.2f".format(itemTotal)}"

                // Load image with Glide
                Glide.with(ivProduct.context)
                    .load(item.thumbnail)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivProduct)

                // Quantity buttons with animations
                btnDecrease.setOnClickListener {
                    if (item.quantity > 1) {
                        onQuantityChange(item, item.quantity - 1)
                        btnDecrease.startAnimation(
                            AnimationUtils.loadAnimation(btnDecrease.context, R.anim.pop)
                        )
                    } else {
                        // Show delete confirmation when quantity is 1
                        showDeleteConfirmation(item)
                    }
                }

                btnIncrease.setOnClickListener {
                    onQuantityChange(item, item.quantity + 1)
                    btnIncrease.startAnimation(
                        AnimationUtils.loadAnimation(btnIncrease.context, R.anim.pop)
                    )
                    // Update total animation
                    tvItemTotal.startAnimation(
                        AnimationUtils.loadAnimation(tvItemTotal.context, R.anim.fade_in)
                    )
                }

                btnDelete.setOnClickListener {
                    showDeleteConfirmation(item)
                }

                // Long press to delete
                root.setOnLongClickListener {
                    showDeleteConfirmation(item)
                    true
                }
            }
        }

        private fun showDeleteConfirmation(item: CartItem) {
            androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                .setTitle("Remove Item")
                .setMessage("Remove ${item.title} from cart?")
                .setPositiveButton("Remove") { _, _ ->
                    onDelete(item)
                    // Animate removal
                    binding.root.startAnimation(
                        AnimationUtils.loadAnimation(binding.root.context, R.anim.slide_in_top)
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}