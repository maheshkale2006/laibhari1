package com.example.laibhariowner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ViewOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: OrderStatusAdapter
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_orders)

        recyclerView = findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        loadOrders()
    }

    private fun loadOrders() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                orderList.clear()

                for (orderSnap in snapshot.children) {
                    val order = orderSnap.getValue(OrderModel::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }

                adapter = OrderStatusAdapter(orderList) { updatedOrder ->
                    updateOrderStatus(updatedOrder)
                }

                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewOrdersActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateOrderStatus(order: OrderModel) {
        val nextStatus = when (order.status) {
            "Pending" -> "Accepted"
            "Accepted" -> "Packed"
            "Packed" -> "Delivered"
            else -> "Delivered"
        }

        val updateMap = mapOf<String, Any>(
            "status" to nextStatus
        )

        dbRef.child(order.orderId!!).updateChildren(updateMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Status Updated to $nextStatus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
            }
    }
}
