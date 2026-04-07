package com.example.ngo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.R
import com.example.ngo.Utils.PaymentDetails

class PaymentAdapter(private val list: MutableList<PaymentDetails>) :
    RecyclerView.Adapter<PaymentAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val id: TextView = v.findViewById(R.id.tvPaymentId)
        val amt: TextView = v.findViewById(R.id.tvAmount)
        val date: TextView = v.findViewById(R.id.tvDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.id.text = "ID: ${item.paymentId}"
        holder.amt.text = item.amount
        holder.date.text = "${item.date} | ${item.time}"
    }

    override fun getItemCount() = list.size
}