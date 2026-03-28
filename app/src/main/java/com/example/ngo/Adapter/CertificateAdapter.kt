package com.example.ngo.Adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ngo.R
import com.example.ngo.Utils.Certificate
import com.google.firebase.database.FirebaseDatabase

class CertificateAdapter(private val list: List<Certificate>) :
    RecyclerView.Adapter<CertificateAdapter.CertVH>() {

    class CertVH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.ivCertImage)
        val title: TextView = v.findViewById(R.id.tvCertTitle)
        val issuer: TextView = v.findViewById(R.id.tvCertIssuer)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDeleteCert) // New
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_certificate_carousel, parent, false)
        return CertVH(v)
    }

    override fun onBindViewHolder(holder: CertVH, position: Int) {
        val cert = list[position]
        holder.title.text = cert.title
        holder.issuer.text = "Issued by: ${cert.issuedBy}"

        // Handle Delete Button Click
        holder.btnDelete.setOnClickListener {
            // Add your deletion logic here
             FirebaseDatabase.getInstance().getReference("Certificates").child(cert.id!!).removeValue()
        }

        Glide.with(holder.itemView.context)
            .load(cert.imageUrl)
            .placeholder(R.drawable.ic_menu_gallery)
            .into(holder.img)
    }

    override fun getItemCount() = list.size
}