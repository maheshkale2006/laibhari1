package com.example.laihari

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var product1: ImageView
    private lateinit var product2: ImageView
    private lateinit var product3: ImageView
    private lateinit var product4: ImageView
    private lateinit var catShirt: ImageView
    private lateinit var catPants: ImageView
    private lateinit var catJacket: ImageView
    private lateinit var catBlazer: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.etSearch)
        product1 = findViewById(R.id.product1)
        product2 = findViewById(R.id.product2)
        product3 = findViewById(R.id.product3)
        product4 = findViewById(R.id.product4)
        catPants = findViewById(R.id.catPants)
        catShirt = findViewById(R.id.catShirt)
        catJacket = findViewById(R.id.catJacket)
        catBlazer = findViewById(R.id.catBlazer)

        // 🔍 Optional: Search feedback (no crash now)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // You can later add filter logic here if you want to filter visible products
            }
        })

        // 🛒 Category clicks → ProductActivity
        catShirt.setOnClickListener { openProductPage("Shirt", "Blue Shirt") }
        catPants.setOnClickListener { openProductPage("Pants", "Formal Pants") }
        catJacket.setOnClickListener { openProductPage("Jacket", "Leather Jacket") }
        catBlazer.setOnClickListener { openProductPage("Blazer", "Blazer") }


        // 📦 Product clicks → ProductActivity
        product1.setOnClickListener { openProductPage("Shirt", "Blue Shirt") }
        product2.setOnClickListener { openProductPage("Pants", "Formal Pants") }
        product3.setOnClickListener { openProductPage("Jacket", "Leather Jacket") }
        product4.setOnClickListener { openProductPage("Blazer", "Blazer") }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cart -> startActivity(Intent(this, CartActivity::class.java))
                R.id.nav_orders -> startActivity(Intent(this, OrdersActivity::class.java))
                R.id.nav_reviews -> startActivity(Intent(this, ReviewsActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            true
        }
    }

    private fun openProductPage(category: String, productName: String) {
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra("CATEGORY", category)
        intent.putExtra("PRODUCT_NAME", productName)
        startActivity(intent)
    }
}
