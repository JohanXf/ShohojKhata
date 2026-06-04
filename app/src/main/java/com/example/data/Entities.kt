package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val shopName: String,
    val shopType: String,
    val upiId: String,
    val pin: String, // 4-digit security PIN for authentication
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "customers",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: Int,
    val name: String,
    val phone: String,
    val email: String? = null,
    val totalDues: Double = 0.0, // Calculated as: Dues We Get (+) minus Dues We Owe (-)
    val lastTransactionAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val isJoined: Boolean = false
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: String, // "GIVE" (টাকা দিলাম - we lent / credit sale) or "GET" (টাকা পেলাম - we collected / paid)
    val description: String, // remarks e.g. "আলু, ডাল" (Grocery item names)
    val paymentMethod: String, // "CASH", "UPI", "OTHER"
    val timestamp: Long = System.currentTimeMillis()
)
