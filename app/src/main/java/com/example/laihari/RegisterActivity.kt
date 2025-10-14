package com.example.laihari

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase
        auth = Firebase.auth

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvGoLogin)
        val radioShopOwner = findViewById<RadioButton>(R.id.radioShopOwner)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()
            val userType = if (radioShopOwner.isChecked) "ShopOwner" else "Customer"

            // Check internet before registering
            if (!isConnected()) {
                showError("No internet connection. Please check your network.")
                return@setOnClickListener
            }

            if (validateInputs(name, email, password, confirm)) {
                registerUser(name, email, password, userType)
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirm: String): Boolean {
        if (name.isEmpty()) {
            showError("Please enter your name")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email")
            return false
        }
        if (password.length < 6) {
            showError("Password must be at least 6 characters")
            return false
        }
        if (password != confirm) {
            showError("Passwords do not match")
            return false
        }
        return true
    }

    private fun registerUser(name: String, email: String, password: String, userType: String) {
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.isEnabled = false
        btnRegister.text = "Creating Account..."

        Log.d(TAG, "Attempting to register: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Registration successful")
                    saveUserData(name, email, userType)
                } else {
                    val error = task.exception?.message ?: "Unknown error"
                    Log.e(TAG, "Registration failed: $error")
                    showError("Registration failed: $error")
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"
                }
            }
    }

    private fun saveUserData(name: String, email: String, userType: String) {
        val user = auth.currentUser
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        if (user != null) {
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "userType" to userType
            )

            Firebase.database.reference.child("Users").child(user.uid).setValue(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
                        redirectUser(userType)
                    } else {
                        showError("Failed to save user data: ${task.exception?.message}")
                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"
                    }
                }
        } else {
            showError("User authentication failed.")
            btnRegister.isEnabled = true
            btnRegister.text = "Register"
        }
    }

    private fun redirectUser(userType: String) {
        val intent = if (userType == "ShopOwner") {
            Intent(this, ShopOwnerActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e(TAG, message)
    }

    // ✅ New and safer internet connectivity check
    private fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
