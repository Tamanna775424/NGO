package com.example.ngo
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var dbRef: DatabaseReference
    private val list = ArrayList<String>()
    private val ids = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_admin, container, false)
        listView = view.findViewById(R.id.listView)

        dbRef = FirebaseDatabase.getInstance().getReference("verifications")

        loadData()

        return view
    }

    private fun loadData(){

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()
                ids.clear()

                for(data in snapshot.children){
                    val item = data.child("itemName").value.toString()
                    val status = data.child("status").value.toString()

                    list.add("$item ( $status )")
                    ids.add(data.key!!)
                }

                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
                listView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            showDialog(ids[position])
        }
    }

    private fun showDialog(id: String){

        val options = arrayOf("Approve","Reject")

        AlertDialog.Builder(requireContext())
            .setTitle("Action")
            .setItems(options){ _, which ->

                if(which == 0){
                    dbRef.child(id).child("status").setValue("approved")
                } else {
                    dbRef.child(id).child("status").setValue("rejected")
                }
            }
            .show()
    }
}