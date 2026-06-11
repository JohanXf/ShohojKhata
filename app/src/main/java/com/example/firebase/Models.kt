package com.example.firebase

data class FirebaseUser(
    val uid: String = "",
    val name: String = "",
    val phone_number: String? = null,
    val pin_hash: String? = null,
    val planType: String = "free",
    val createdAt: Long = System.currentTimeMillis()
)

data class Shop(
    val id: String = "",
    val ownerId: String = "",
    val shopName: String = "",
    val shopType: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Membership(
    val id: String = "",
    val shopId: String = "",
    val clientPhoneNumber: String = "",
    val customerUid: String? = null,
    val totalDues: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

data class LedgerEntry(
    val id: String = "",
    val shopId: String = "",
    val customerUid: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "GIVE" or "GET"
    val description: String = "",
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
