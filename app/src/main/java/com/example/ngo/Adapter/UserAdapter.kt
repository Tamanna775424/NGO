package com.example.ngo.Adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ngo.R
import com.example.ngo.Utils.User

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvUserName)
        val email: TextView = view.findViewById(R.id.tvUserEmail)
        val phone: TextView = view.findViewById(R.id.tvUserPhone)
        val image: ImageView = view.findViewById(R.id.ivUserProfile)
        val phoneIcon: ImageView = view.findViewById(R.id.ivCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.name.text = user.name
        holder.email.text = user.email
        holder.phone.text = user.phone

        // Load profile image
        Glide.with(holder.itemView.context)
            .load(user.profileUrl)
            .placeholder(R.drawable.ic_menu_gallery)
            .into(holder.image)

        // Set Click Listener for Calling
        holder.phoneIcon.setOnClickListener {
            val phoneNumber = user.phone
            if (!phoneNumber.isNullOrEmpty()) {
                val context = holder.itemView.context
                val intent = Intent(Intent.ACTION_DIAL) // ACTION_DIAL opens dialer without needing extra permissions
                intent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = userList.size
}