package com.example.laihari

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvGoRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Internet check
            if (!isConnected()) {
                showError("No internet connection. Please check your network.")
                return@setOnClickListener
            }

            // Validate input
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter valid email"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Enter password"
                return@setOnClickListener
            }

            // Show loading
            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."

            // Firebase Login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            getUserTypeAndRedirect(uid, btnLogin)
                        } else {
                            resetButton(btnLogin)
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        resetButton(btnLogin)
                        showError("Error: ${task.exception?.message}")
                    }
                }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun getUserTypeAndRedirect(uid: String, btnLogin: Button) {
        FirebaseDatabase.getInstance().getReference("Users")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                resetButton(btnLogin)

                val userType = snapshot.child("userType").getValue(String::class.java) ?: "Customer"
                Toast.makeText(this, "Login Successful as $userType", Toast.LENGTH_SHORT).show()

                val nextActivity = if (userType == "ShopOwner") {
                    ShopOwnerActivity::class.java
                } else {
                    MainActivity::class.java
                }
                startActivity(Intent(this, nextActivity))
                finish()
            }
            .addOnFailureListener { exception ->
                resetButton(btnLogin)
                showError("Failed to get user data: ${exception.message}")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    private fun resetButton(button: Button) {
        button.isEnabled = true
        button.text = "Login"
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e(TAG, message)
    }

    // ✅ Same internet connectivity check as RegisterActivity
    private fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val userType = snapshot.child("userType").getValue(String::class.java) ?: "Customer"

                    val nextActivity = if (userType == "ShopOwner") {
                        ShopOwnerActivity::class.java
                    } else {
                        MainActivity::class.java
                    }
                    startActivity(Intent(this, nextActivity))
                    finish()
                }
                .addOnFailureListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }
}
