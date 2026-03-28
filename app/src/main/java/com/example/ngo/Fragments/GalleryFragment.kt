package com.example.ngo.Activities.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.Adapter.UserAdapter
import com.example.ngo.R
import com.example.ngo.Utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GalleryFragment : Fragment() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var loader: ProgressBar
    private val database = FirebaseDatabase.getInstance().getReference("Users")
    private val usersList = mutableListOf<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        rvUsers = view.findViewById(R.id.rvUsers)
        loader = view.findViewById(R.id.loader)

        rvUsers.layoutManager = LinearLayoutManager(requireContext())

        fetchUsers()
        return view
    }

    private fun fetchUsers() {
        loader.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)


                    user?.let {
                        if(it.email == FirebaseAuth.getInstance().currentUser?.email){

                        } else{
                            usersList.add(it)
                        }
                         }
                }

                rvUsers.adapter = UserAdapter(usersList)
                loader.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                loader.visibility = View.GONE
            }
        })
    }
}