package com.example.laibhariowner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.laibhariowner.OrderModel
import com.google.firebase.database.FirebaseDatabase

class OrderAdapter(private val context: Context, private val orderList: List<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOrderId: TextView = itemView.findViewById(R.id.txtOrderId)
        val txtUserDetails: TextView = itemView.findViewById(R.id.txtUserDetails)
        val txtProductDetails: TextView = itemView.findViewById(R.id.txtProductDetails)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)

        val btnConfirm: Button = itemView.findViewById(R.id.btnConfirm)
        val btnOutForDelivery: Button = itemView.findViewById(R.id.btnOutForDelivery)
        val btnDelivered: Button = itemView.findViewById(R.id.btnDelivered)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item_layout, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.txtOrderId.text = "Order ID: ${order.orderId}"
        holder.txtUserDetails.text =
            "${order.customerName} (${order.mobile})\nAddress: ${order.address}"

        holder.txtProductDetails.text =
            "Product: ${order.productName}\nQty: ${order.quantity}\nTotal: ₹${order.totalPrice}"

        holder.txtStatus.text = "Status: ${order.status}"

        //–––––––––––––––––––––––––––
        //     UPDATE STATUS BUTTONS
        //–––––––––––––––––––––––––––

        holder.btnConfirm.setOnClickListener {
            updateStatus(order.orderId!!, "Confirmed")
        }

        holder.btnOutForDelivery.setOnClickListener {
            updateStatus(order.orderId!!, "Out for Delivery")
        }

        holder.btnDelivered.setOnClickListener {
            updateStatus(order.orderId!!, "Delivered")
        }

        holder.btnCancel.setOnClickListener {
            updateStatus(order.orderId!!, "Cancelled")
        }
    }

    private fun updateStatus(orderId: String, newStatus: String) {
        val db = FirebaseDatabase.getInstance().getReference("Orders")

        db.child(orderId).child("status")
            .setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }
}
