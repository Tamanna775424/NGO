package com.example.ngo.Adapters

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.ngo.R
import com.example.ngo.Utils.Certificate
import com.google.firebase.database.FirebaseDatabase

class CertificateAdapter(private val list: List<Certificate>) :
    RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.ivCertImage)
        val title: TextView = v.findViewById(R.id.tvCertTitle)
        val issuer: TextView = v.findViewById(R.id.tvCertIssuer)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDeleteCert)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_certificate_carousel, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cert = list[position]

        holder.title.text = cert.title
        holder.issuer.text = "Issued by: ${cert.issuedBy}"

        // Load Image
        Glide.with(holder.itemView.context)
            .load(cert.imageUrl)
            .placeholder(R.drawable.ic_menu_gallery)
            .error(R.drawable.ic_menu_gallery)
            .into(holder.img)

        // Delete Certificate (Safe)
        holder.btnDelete.setOnClickListener {
            cert.id?.let { id ->
                FirebaseDatabase.getInstance()
                    .getReference("Certificates")
                    .child(id as String)
                    .removeValue()
            }
        }

        // Click → Open Full Screen Zoom
        holder.itemView.setOnClickListener {
            cert.imageUrl?.let {
                showZoomDialog(holder.itemView, it as String)
            }
        }
    }

    override fun getItemCount() = list.size

    // ==============================
    // 🔥 FULL SCREEN ZOOM DIALOG
    // ==============================
    private fun showZoomDialog(view: View, imageUrl: String) {

        val context = view.context

        val root = FrameLayout(context)
        root.setBackgroundColor(Color.BLACK)

        val imageView = ImageView(context)
        imageView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.MATRIX
        imageView.adjustViewBounds = true

        root.addView(imageView)

        // ❌ CLOSE BUTTON
        val close = ImageView(context)
        close.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)

        val params = FrameLayout.LayoutParams(120, 120)
        params.gravity = Gravity.TOP or Gravity.END
        params.setMargins(20, 60, 20, 20)
        close.layoutParams = params

        root.addView(close)

        // Load Full Quality Image
        Glide.with(context)
            .load(imageUrl)
            .override(Target.SIZE_ORIGINAL)
            .into(imageView)

        // 🔥 ZOOM + DRAG
        val matrix = Matrix()
        val savedMatrix = Matrix()

        var mode = 0
        val NONE = 0
        val DRAG = 1

        var startX = 0f
        var startY = 0f

        val scaleDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    matrix.postScale(
                        detector.scaleFactor,
                        detector.scaleFactor,
                        detector.focusX,
                        detector.focusY
                    )
                    imageView.imageMatrix = matrix
                    return true
                }
            })

        imageView.setOnTouchListener { _, event ->

            scaleDetector.onTouchEvent(event)

            when (event.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(matrix)
                    startX = event.x
                    startY = event.y
                    mode = DRAG
                }

                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        matrix.set(savedMatrix)
                        val dx = event.x - startX
                        val dy = event.y - startY
                        matrix.postTranslate(dx, dy)
                        imageView.imageMatrix = matrix
                    }
                }

                MotionEvent.ACTION_UP -> {
                    mode = NONE
                }
            }
            true
        }

        val dialog = AlertDialog.Builder(context).create()
        dialog.setView(root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()

        // FULL SCREEN
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        close.setOnClickListener {
            dialog.dismiss()
        }
    }
}