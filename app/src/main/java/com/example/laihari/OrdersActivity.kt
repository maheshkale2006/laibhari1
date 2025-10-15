package com.example.laihari

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Orders"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
