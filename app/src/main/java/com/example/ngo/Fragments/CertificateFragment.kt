package com.example.ngo.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.example.ngo.Adapters.CertificateAdapter
import com.example.ngo.AppwriteManager
import com.example.ngo.R
import com.example.ngo.Utils.Certificate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CertificateFragment : Fragment() {

    private lateinit var rvCarousel: RecyclerView
    private lateinit var loader: ProgressBar
    private lateinit var fabAdd: FloatingActionButton

    private val certList = mutableListOf<Certificate>()
    private val database = FirebaseDatabase.getInstance().getReference("Certificates")
    private val appwriteManager by lazy { AppwriteManager.getInstance(requireContext()) }

    // Image Picker for the BottomSheet
    private var selectedImageUri: Uri? = null
    private var ivPreview: ImageView? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivPreview?.setImageURI(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_certificate, container, false)

        rvCarousel = view.findViewById(R.id.rvCertificateCarousel)
        loader = view.findViewById(R.id.certLoader)
        fabAdd = view.findViewById(R.id.fabAddCertificate) // Ensure this ID is in your XML

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCarousel.layoutManager = layoutManager

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvCarousel)

        fabAdd.setOnClickListener { showAddCertificateDialog() }

        fetchCertificates()
        return view
    }

    private fun showAddCertificateDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_certificate, null)
        dialog.setContentView(dialogView)

        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etIssuedBy = dialogView.findViewById<EditText>(R.id.etIssuedBy)
        val btnUpload = dialogView.findViewById<Button>(R.id.btnSubmit)
        val progress = dialogView.findViewById<ProgressBar>(R.id.uploadProgress)
        ivPreview = dialogView.findViewById(R.id.ivSelectImage)

        ivPreview?.setOnClickListener { pickImage.launch("image/*") }

        btnUpload.setOnClickListener {
            val title = etTitle.text.toString()
            val issuer = etIssuedBy.text.toString()
            val uri = selectedImageUri

            if (title.isEmpty() || issuer.isEmpty() || uri == null) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadToAppwriteAndFirebase(title, issuer, uri, progress, dialog)
        }

        dialog.show()
    }

    private fun uploadToAppwriteAndFirebase(title: String, issuer: String, uri: Uri, pb: ProgressBar, dialog: BottomSheetDialog) {
        pb.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Upload to Appwrite
                val imageUrl = appwriteManager.uploadImageFromUri(uri)

                // 2. Prepare Data
                val id = database.push().key ?: ""
                val currentDate =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

                val newCert = Certificate(
                    id = id,
                    title = title,
                    issuedBy = issuer,
                    imageUrl = imageUrl,
                    dateAdded = currentDate,
                )


                // 3. Save to Firebase
                database.child(id).setValue(newCert).addOnCompleteListener {
                    pb.visibility = View.GONE
                    if (it.isSuccessful) {
                        dialog.dismiss()
                        selectedImageUri = null // Reset for next time
                        Toast.makeText(context, "Certificate Added Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                pb.visibility = View.GONE
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCertificates() {
        loader.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                certList.clear()
                for (data in snapshot.children) {
                    val cert = data.getValue(Certificate::class.java)
                    cert?.let { certList.add(it) }
                }
                if (isAdded) {
                    rvCarousel.adapter = CertificateAdapter(certList)
                    loader.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                loader.visibility = View.GONE
            }
        })
    }
}
