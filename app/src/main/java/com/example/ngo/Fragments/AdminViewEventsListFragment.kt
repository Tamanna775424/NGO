package com.example.ngo.Fragments

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.Adapter.EventAdapter
import com.example.ngo.R
import com.example.ngo.Utils.DonationEvent
import com.google.firebase.database.*

class AdminViewEventsListFragment : Fragment() {

    private lateinit var rvEvents: RecyclerView
    private lateinit var spinnerFilter: Spinner
    private lateinit var database: DatabaseReference
    private val allEvents = mutableListOf<DonationEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_view_events_list, container, false)

        rvEvents = view.findViewById(R.id.rvEventsList)
        spinnerFilter = view.findViewById(R.id.spinnerStatusFilter)

        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        database = FirebaseDatabase.getInstance().getReference("DonationEvents")

        setupSpinner()
        fetchEvents()

        return view
    }

    private fun setupSpinner() {
        val options = arrayOf("Unverified", "Verified")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // position 0 = Unverified (false), position 1 = Verified (true)
                filterList(position == 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchEvents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allEvents.clear()
                for (child in snapshot.children) {
                    val event = child.getValue(DonationEvent::class.java)
                    event?.let { allEvents.add(it) }
                }
                // Trigger filter for current spinner selection
                filterList(spinnerFilter.selectedItemPosition == 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Inside AdminViewEventsListFragment, update filterList:
    private fun filterList(isVerified: Boolean) {
        val filteredList = allEvents.filter { it.eventStatus == isVerified }

        rvEvents.adapter = EventAdapter(filteredList.sortedByDescending { it.timestamp }) { event ->
            // Navigation Logic
            val fragment = EventViewFragment()
            val bundle = Bundle()
            bundle.putParcelable("event_data", event)
            fragment.arguments = bundle

           findNavController().navigate(R.id.eventViewFragment,bundle)
        }
    }
}