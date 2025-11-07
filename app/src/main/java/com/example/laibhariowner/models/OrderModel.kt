package com.example.laibhariowner

data class OrderModel(
    var orderId: String? = null,
    var customerName: String? = null,
    var userName: String? = null,    // ✅ Added
    var userPhone: String? = null,
    var mobile: String? = null,
    var address: String? = null,

    var productName: String? = null,
    var quantity: Int? = null,
    var totalPrice: Int? = null,

    var status: String? = "Pending"
)
