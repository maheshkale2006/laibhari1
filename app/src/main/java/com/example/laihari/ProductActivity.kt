package com.example.laihari

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val category = intent.getStringExtra("CATEGORY")
        val productName = intent.getStringExtra("PRODUCT_NAME")

        val tvProduct = findViewById<TextView>(R.id.tvProductDetails)
        tvProduct.text = "Category: $category\nProduct: $productName"
    }
}
