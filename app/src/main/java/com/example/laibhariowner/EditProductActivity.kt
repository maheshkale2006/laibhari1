package com.example.laibhariowner

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class EditProductActivity : AppCompatActivity() {

    private lateinit var imgProduct: ImageView
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private var productId: String = ""
    private var imageBase64: String = ""

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = uriToBitmap(it)
                bitmap?.let { bmp ->
                    imgProduct.setImageBitmap(bmp)
                    imageBase64 = bitmapToBase64(bmp)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        imgProduct = findViewById(R.id.imgProduct)
        etName = findViewById(R.id.etName)
        etDescription = findViewById(R.id.etDescription)
        etPrice = findViewById(R.id.etPrice)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)

        val categories = arrayOf("Shirts", "Pants", "Jackets", "Shoes", "Blazer", "Accessories")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter

        productId = intent.getStringExtra("productId") ?: ""
        etName.setText(intent.getStringExtra("productName"))
        etDescription.setText(intent.getStringExtra("productDescription"))
        etPrice.setText(intent.getDoubleExtra("productPrice", 0.0).toString())

        val category = intent.getStringExtra("category")
        if (category != null) {
            val pos = categories.indexOf(category)
            if (pos >= 0) spinnerCategory.setSelection(pos)
        }

        val originalImageBase64 = intent.getStringExtra("imageBase64") ?: ""
        imageBase64 = originalImageBase64
        if (originalImageBase64.isNotEmpty()) {
            val bytes = Base64.decode(originalImageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imgProduct.setImageBitmap(bitmap)
        }

        imgProduct.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnUpdate.setOnClickListener {
            updateProduct()
        }

        btnDelete.setOnClickListener {
            confirmDeleteProduct()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun updateProduct() {
        val name = etName.text.toString().trim()
        val desc = etDescription.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()

        if (name.isEmpty() || desc.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "⚠️ Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val priceValue = price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            Toast.makeText(this, "⚠️ Enter a valid price", Toast.LENGTH_SHORT).show()
            return
        }

        if (productId.isEmpty()) {
            Toast.makeText(this, "❌ Invalid product ID", Toast.LENGTH_SHORT).show()
            return
        }

        val productRef = FirebaseDatabase.getInstance().getReference("Products").child(productId)
        val updates = mapOf(
            "productName" to name,
            "productDescription" to desc,
            "productPrice" to priceValue,
            "category" to category,
            "imageBase64" to imageBase64
        )

        productRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Product updated successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmDeleteProduct() {
        if (productId.isEmpty()) {
            Toast.makeText(this, "❌ Invalid product ID", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { _, _ ->
                deleteProduct()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct() {
        val productRef = FirebaseDatabase.getInstance().getReference("Products").child(productId)
        productRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "🗑️ Product deleted successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Delete failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
