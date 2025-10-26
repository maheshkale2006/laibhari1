package com.example.laibhariowner

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.LayoutInflater
class ViewProductsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var tvNoProducts: TextView
    private lateinit var progressBar: ProgressBar

    private val productsList = ArrayList<Product>()

    // ActivityResultLauncher for editing products
    private val editProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Reload products after editing
            loadProducts()
        }
    }

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
        productsAdapter = ProductsAdapter(productsList) { product ->
            // Launch EditProductActivity via callback
            val intent = Intent(this, EditProductActivity::class.java).apply {
                putExtra("productId", product.productId)
                putExtra("productName", product.productName)
                putExtra("productDescription", product.productDescription)
                putExtra("productPrice", product.productPrice)
                putExtra("category", product.category)
                putExtra("imageBase64", product.imageBase64)
            }
            editProductLauncher.launch(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productsAdapter
    }

    private fun loadProducts() {
        val shopOwnerId = auth.currentUser?.uid ?: return

        progressBar.visibility = android.view.View.VISIBLE
        tvNoProducts.visibility = android.view.View.GONE

        val productsRef = database.getReference("Products")
        val query = productsRef.orderByChild("shopOwnerId").equalTo(shopOwnerId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productsList.add(it) }
                }

                progressBar.visibility = android.view.View.GONE
                tvNoProducts.visibility = if (productsList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                productsList.sortByDescending { it.timestamp }
                productsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = android.view.View.GONE
                tvNoProducts.visibility = android.view.View.VISIBLE
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
        val imageBase64: String = "",
        val shopOwnerId: String = "",
        val timestamp: Long = 0
    )

    // Adapter
    class ProductsAdapter(
        private val products: List<Product>,
        private val onEditClicked: (Product) -> Unit
    ) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            val productImage: ImageView = itemView.findViewById(R.id.imgProduct)
            val productName: TextView = itemView.findViewById(R.id.txtProductName)
            val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
            val productCategory: TextView = itemView.findViewById(R.id.txtProductCategory)
            val btnEdit: android.widget.Button = itemView.findViewById(R.id.btnEditProduct)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProductViewHolder {
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
                    holder.itemView.context,
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
}
