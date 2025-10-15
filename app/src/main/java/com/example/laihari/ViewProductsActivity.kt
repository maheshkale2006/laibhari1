package com.example.laihari

import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewProductsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var tvNoProducts: TextView
    private lateinit var progressBar: ProgressBar

    private val productsList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_products)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initializeViews()
        setupRecyclerView()
        loadProducts()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        tvNoProducts = findViewById(R.id.tvNoProducts)
        progressBar = findViewById(R.id.progressBar)

        // Set up back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(productsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productsAdapter
    }

    private fun loadProducts() {
        val shopOwnerId = auth.currentUser?.uid ?: return

        progressBar.visibility = View.VISIBLE
        tvNoProducts.visibility = View.GONE

        val productsRef = database.getReference("Products")
        val query = productsRef.orderByChild("shopOwnerId").equalTo(shopOwnerId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()

                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productsList.add(it)
                    }
                }

                progressBar.visibility = View.GONE

                if (productsList.isEmpty()) {
                    tvNoProducts.visibility = View.VISIBLE
                } else {
                    tvNoProducts.visibility = View.GONE
                    // Sort by timestamp (newest first)
                    productsList.sortByDescending { it.timestamp }
                }

                productsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                tvNoProducts.visibility = View.VISIBLE
                tvNoProducts.text = "Error loading products"
            }
        })
    }

    // Product data class
    data class Product(
        val productId: String = "",
        val productName: String = "",
        val productDescription: String = "",
        val productPrice: Double = 0.0,
        val category: String = "",
        val imageUrl: String = "",
        val shopOwnerId: String = "",
        val timestamp: Long = 0
    )

    // Products Adapter
    inner class ProductsAdapter(private val products: List<Product>) :
        RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val productImage: ImageView = itemView.findViewById(R.id.imgProduct)
            val productName: TextView = itemView.findViewById(R.id.txtProductName)
            val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
           val productCategory: TextView = itemView.findViewById(R.id.txtProductCategory)
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

            // Use placeholder image
            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery)

            // Add click listener if needed
            holder.itemView.setOnClickListener {
                Toast.makeText(this@ViewProductsActivity, "${product.productName} - ${formatPrice(product.productPrice)}", Toast.LENGTH_SHORT).show()
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
}