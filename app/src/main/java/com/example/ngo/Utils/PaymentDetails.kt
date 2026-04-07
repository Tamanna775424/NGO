package com.example.ngo.Utils

data class PaymentDetails(
    val paymentId: String? = "",
    val amount: String? = "",   // ✅ IMPORTANT
    val date: String? = "",
    val time: String? = "",
    val userUid: String? = ""
)