package com.example.ngo.Utils
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class User(
    val uid: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val profileUrl: String? = null
)


@Parcelize
data class DonationEvent(
    val eventId: String? = null,
    val donorId: String? = null,
    val donationType: String? = null,
    val description: String? = null,
    val address: String? = null,
    val date: String? = null,
    val time: String? = null,
    var eventStatus: Boolean ?= false,
    val timestamp: Long = System.currentTimeMillis()
): Parcelable


data class PaymentDetails(
    val paymentId: String? = "",
    val amount: String? = "",
    val date: String? = "",
    val time: String? = "",
    val userUid: String? = ""
)

data class Certificate(
    val id: String? = "",
    val title: String? = "",
    val issuedBy: String? = "",
    val imageUrl: String? = "",
    val dateAdded: String? = ""
)