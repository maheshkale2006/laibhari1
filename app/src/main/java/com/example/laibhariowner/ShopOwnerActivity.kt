package com.example.laibhariowner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ShopOwnerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_owner)

        auth = FirebaseAuth.getInstance()

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnAddProduct = findViewById<Button>(R.id.btnAddProduct)
        val btnViewProducts = findViewById<Button>(R.id.btnViewProducts)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val currentUser = auth.currentUser
        tvWelcome.text = "Welcome, Shop Owner!\n${currentUser?.email}"

        btnAddProduct.setOnClickListener {
            // Navigate to Add Product Activity
            startActivity(Intent(this, AddProductActivity::class.java))//AddProductActivity
        }

        btnViewProducts.setOnClickListener {
            // Navigate to View Products Activity
            startActivity(Intent(this, ViewProductsActivity::class.java))//ViewProductsActivity
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}