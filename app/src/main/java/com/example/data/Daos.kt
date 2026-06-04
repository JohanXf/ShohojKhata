package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getFirstUserFlow(): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirstUser(): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE ownerId = :ownerId ORDER BY name ASC")
    fun getCustomersByOwner(ownerId: Int): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: Int): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("UPDATE customers SET totalDues = totalDues + :delta, lastTransactionAt = :timestamp WHERE id = :customerId")
    suspend fun updateCustomerDues(customerId: Int, delta: Double, timestamp: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsByCustomer(customerId: Int): Flow<List<Transaction>>

    @Query("""
        SELECT t.* FROM transactions t 
        INNER JOIN customers c ON t.customerId = c.id 
        WHERE c.ownerId = :ownerId 
        ORDER BY t.timestamp DESC
    """)
    fun getTransactionsByOwner(ownerId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
