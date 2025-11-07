package com.example.laibhariowner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderStatusAdapter(
    private val orderList: List<OrderModel>,
    private val onStatusUpdate: (OrderModel) -> Unit
) : RecyclerView.Adapter<OrderStatusAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderId: TextView = view.findViewById(R.id.txtOrderId)
        val txtUserDetails: TextView = view.findViewById(R.id.txtUserDetails)
        val txtProductDetails: TextView = view.findViewById(R.id.txtProductDetails)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val btnUpdateStatus: Button = view.findViewById(R.id.btnUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item_layout, parent, false)
        return OrderViewHolder(view)
    }


    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.txtOrderId.text = "Order ID: ${order.orderId}"
        holder.txtUserDetails.text = "User: ${order.userName} | ${order.userPhone}"
        holder.txtProductDetails.text = "Product: ${order.productName}\nQty: ${order.quantity}"
        holder.txtStatus.text = "Status: ${order.status}"

        holder.btnUpdateStatus.setOnClickListener {
            onStatusUpdate(order)
        }
    }

    override fun getItemCount(): Int = orderList.size
}
