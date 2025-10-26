package com.example.laibhariowner

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*
import ProductModel
class ProductsAdapter(
    private val context: Context,
    private val products: List<ProductModel>, // Make sure ProductModel has imageBase64
    private val onEditClicked: (ProductModel) -> Unit // callback for edit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.imgProduct)
        val productName: TextView = itemView.findViewById(R.id.txtProductName)
        val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
        val productCategory: TextView = itemView.findViewById(R.id.txtProductCategory)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.productName.text = product.productName
        holder.productPrice.text = formatPrice(product.productPrice)
        holder.productCategory.text = product.category

        // Load Base64 image
        if (product.imageBase64.isNotEmpty()) {
            try {
                val bytes = Base64.decode(product.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.productImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Edit button click
        holder.btnEdit.setOnClickListener {
            onEditClicked(product)
        }

        // Optional: click item for toast
        holder.itemView.setOnClickListener {
            Toast.makeText(
                context,
                "${product.productName} - ${formatPrice(product.productPrice)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = products.size

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("USD")
        return format.format(price)
    }
}
