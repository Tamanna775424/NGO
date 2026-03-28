package com.example.ngo.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.R
import com.example.ngo.Utils.DonationEvent

class EventAdapter(
    private val list: List<DonationEvent>,
    private val onItemClick: (DonationEvent) -> Unit // Added listener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type: TextView = view.findViewById(R.id.tvType)
        val desc: TextView = view.findViewById(R.id.tvDesc)
        val address: TextView = view.findViewById(R.id.tvAddress)
        val dateTime: TextView = view.findViewById(R.id.tvDateTime)
        val status: TextView = view.findViewById(R.id.tvStatusLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donation_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = list[position]

        holder.itemView.setOnClickListener { onItemClick(event) } // Set click listener

        holder.type.text = event.donationType
        holder.desc.text = event.description
        holder.address.text = "📍 ${event.address}"
        holder.dateTime.text = "🗓 ${event.date} | ${event.time}"

        if (event.eventStatus == true) {
            holder.status.text = "VERIFIED"
            holder.status.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.status.text = "UNVERIFIED"
            holder.status.setTextColor(Color.parseColor("#F44336"))
        }
    }

    override fun getItemCount() = list.size
}