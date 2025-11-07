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
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val currentUser = auth.currentUser
        tvWelcome.text = "Welcome, Shop Owner!\n${currentUser?.email}"

        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        btnViewProducts.setOnClickListener {
            startActivity(Intent(this, ViewProductsActivity::class.java))
        }

        btnViewOrders.setOnClickListener {
            startActivity(Intent(this, ViewOrdersActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
