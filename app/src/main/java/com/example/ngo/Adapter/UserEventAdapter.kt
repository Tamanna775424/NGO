package com.example.ngo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.R
import com.example.ngo.Utils.DonationEvent

class UserEventAdapter(private val list: List<DonationEvent>) :
    RecyclerView.Adapter<UserEventAdapter.UserVH>() {

    class UserVH(v: View) : RecyclerView.ViewHolder(v) {
        val type: TextView = v.findViewById(R.id.tvUserType)
        val desc: TextView = v.findViewById(R.id.tvUserDesc)
        val addr: TextView = v.findViewById(R.id.tvUserAddress)
        val date: TextView = v.findViewById(R.id.tvUserDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_event_user, parent, false)
        return UserVH(v)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val event = list[position]
        holder.type.text = event.donationType
        holder.desc.text = event.description
        holder.addr.text = event.address
        holder.date.text = event.date
    }

    override fun getItemCount() = list.size
}