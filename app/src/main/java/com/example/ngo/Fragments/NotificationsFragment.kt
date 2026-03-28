package com.example.ngo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.Adapter.UserEventAdapter
import com.example.ngo.R
import com.example.ngo.Utils.DonationEvent
import com.example.ngo.databinding.FragmentNotificationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationsFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private val verifiedEvents = mutableListOf<DonationEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        rv = view.findViewById(R.id.rvNotifications)
        rv.layoutManager = LinearLayoutManager(requireContext())

        fetchVerifiedEvents()
        return view
    }

    private fun fetchVerifiedEvents() {
        val ref = FirebaseDatabase.getInstance().getReference("DonationEvents")

        // Use a query to filter verified events directly from the database
        ref.orderByChild("eventStatus").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    verifiedEvents.clear()
                    for (child in snapshot.children) {
                        val event = child.getValue(DonationEvent::class.java)
                        event?.let { verifiedEvents.add(it) }
                    }
                    // Sort by timestamp so newest notifications appear first
                    rv.adapter = UserEventAdapter(verifiedEvents.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}