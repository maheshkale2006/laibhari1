package com.example.laihari

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import ProductModel

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var productList: MutableList<ProductModel>
    private lateinit var productAdapter: ProductAdapter

    private lateinit var catShirtLayout: LinearLayout
    private lateinit var catPantsLayout: LinearLayout
    private lateinit var catJacketLayout: LinearLayout
    private lateinit var catBlazerLayout: LinearLayout

    private lateinit var bottomNavigationView: BottomNavigationView

    private var allProducts: MutableList<ProductModel> = mutableListOf() // all products

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerProducts = findViewById(R.id.recyclerProducts)
        recyclerProducts.layoutManager = GridLayoutManager(this, 2)

        productList = mutableListOf()
        productAdapter = ProductAdapter(productList)
        recyclerProducts.adapter = productAdapter

        // Category layouts
        catShirtLayout = findViewById(R.id.catShirtLayout)
        catPantsLayout = findViewById(R.id.catPantsLayout)
        catJacketLayout = findViewById(R.id.catJacketLayout)
        catBlazerLayout = findViewById(R.id.catBlazerLayout)

        // Bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        loadProductsFromFirebase()
        setupCategoryClicks()
        setupBottomNavigation()
    }

    private fun loadProductsFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Products")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allProducts.clear()
                for (productSnap in snapshot.children) {
                    val product = productSnap.getValue(ProductModel::class.java)
                    product?.let { allProducts.add(it) }
                }
                // sort newest first
                allProducts.sortByDescending { it.timestamp }
                productList.clear()
                productList.addAll(allProducts)
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCategoryClicks() {
        catShirtLayout.setOnClickListener { filterProductsByCategory("Shirts") }
        catPantsLayout.setOnClickListener { filterProductsByCategory("Pants") }
        catJacketLayout.setOnClickListener { filterProductsByCategory("Jackets") }
        catBlazerLayout.setOnClickListener { filterProductsByCategory("Blazers") }
    }

    private fun filterProductsByCategory(category: String) {
        val filtered = allProducts.filter { it.category.equals(category, true) }
        productList.clear()
        productList.addAll(filtered)
        productAdapter.notifyDataSetChanged()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
                    true
                }
                R.id.nav_reviews -> {
                    startActivity(Intent(this, ReviewsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

}

