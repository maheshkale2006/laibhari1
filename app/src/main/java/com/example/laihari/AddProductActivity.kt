package com.example.laihari

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var productImage: ImageView
    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAddProduct: Button
    private lateinit var btnSelectImage: Button

    private var imageUri: Uri? = null
    private var imageBase64: String? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        auth = FirebaseAuth.getInstance()
        initializeViews()
        setupSpinner()
        setupClickListeners()
    }

    private fun initializeViews() {
        productImage = findViewById(R.id.productImage)
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductPrice = findViewById(R.id.etProductPrice)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        btnSelectImage = findViewById(R.id.btnSelectImage)
    }

    private fun setupSpinner() {
        val categories = arrayOf("Shirts", "Pants", "Jackets", "Shoes", "Blazer", "Accessories")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupClickListeners() {
        btnSelectImage.setOnClickListener {
            openImageChooser()
        }

        btnAddProduct.setOnClickListener {
            addProductToFirebase()
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            productImage.setImageURI(imageUri)

            // Convert selected image to Base64
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageBase64 = bitmapToBase64(bitmap)
        }
    }

    // Convert Bitmap to Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // 50% quality
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun addProductToFirebase() {
        val productName = etProductName.text.toString().trim()
        val productDescription = etProductDescription.text.toString().trim()
        val productPrice = etProductPrice.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val shopOwnerId = auth.currentUser?.uid ?: ""

        if (productName.isEmpty()) {
            etProductName.error = "Product name is required"
            return
        }

        if (productDescription.isEmpty()) {
            etProductDescription.error = "Product description is required"
            return
        }

        if (productPrice.isEmpty()) {
            etProductPrice.error = "Product price is required"
            return
        }

        if (imageBase64 == null) {
            Toast.makeText(this, "Please select product image", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Adding Product...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val productId = UUID.randomUUID().toString()
        val product = HashMap<String, Any>()
        product["productId"] = productId
        product["productName"] = productName
        product["productDescription"] = productDescription
        product["productPrice"] = productPrice.toDouble()
        product["category"] = category
        product["imageBase64"] = imageBase64!!
        product["shopOwnerId"] = shopOwnerId
        product["timestamp"] = System.currentTimeMillis()

        val databaseRef = FirebaseDatabase.getInstance().getReference("Products")
        databaseRef.child(productId).setValue(product)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "✅ Product added successfully!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "❌ Failed to add product: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun clearForm() {
        etProductName.text.clear()
        etProductDescription.text.clear()
        etProductPrice.text.clear()
        productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        imageUri = null
        imageBase64 = null
        spinnerCategory.setSelection(0)
    }
}
