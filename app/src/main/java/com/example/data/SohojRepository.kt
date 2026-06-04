package com.example.data

import kotlinx.coroutines.flow.Flow
import androidx.room.withTransaction

class SohojRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val customerDao = database.customerDao()
    private val transactionDao = database.transactionDao()

    val firstUserFlow: Flow<User?> = userDao.getFirstUserFlow()

    suspend fun getFirstUser(): User? = userDao.getFirstUser()

    suspend fun getUserById(userId: Int): User? = userDao.getUserById(userId)

    suspend fun registerUser(
        name: String,
        phone: String,
        shopName: String,
        shopType: String,
        upiId: String,
        pin: String
    ): Long {
        val user = User(
            name = name,
            phone = phone,
            shopName = shopName,
            shopType = shopType,
            upiId = upiId,
            pin = pin
        )
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    fun getCustomers(ownerId: Int): Flow<List<Customer>> {
        return customerDao.getCustomersByOwner(ownerId)
    }

    suspend fun getCustomerById(customerId: Int): Customer? {
        return customerDao.getCustomerById(customerId)
    }

    suspend fun insertCustomer(ownerId: Int, name: String, phone: String, email: String?, isJoined: Boolean = false): Long {
        val customer = Customer(
            ownerId = ownerId,
            name = name,
            phone = phone,
            email = email,
            totalDues = 0.0,
            isJoined = isJoined
        )
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
    }

    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCustomer(customerId)
    }

    fun getTransactionsForOwner(ownerId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByOwner(ownerId)
    }

    suspend fun addTransaction(
        customerId: Int,
        amount: Double,
        type: String, // "GIVE" or "GET"
        description: String,
        paymentMethod: String // "CASH", "UPI", "OTHER"
    ): Long {
        val timestamp = System.currentTimeMillis()
        val transaction = Transaction(
            customerId = customerId,
            amount = amount,
            type = type,
            description = description,
            paymentMethod = paymentMethod,
            timestamp = timestamp
        )

        // Delta calculation for Customer's dues:
        // GIVE means we lent money/sold on credit -> Customer's debt increases (+amount)
        // GET means they paid us -> Customer's debt decreases (-amount)
        val delta = if (type == "GIVE") amount else -amount

        var transactionId: Long = 0
        database.withTransaction {
            transactionId = transactionDao.insertTransaction(transaction)
            customerDao.updateCustomerDues(customerId, delta, timestamp)
        }
        return transactionId
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        // Revert balance change on customer dues:
        // If deleted transaction was GIVE, we decrease customer's dues (delta = -amount)
        // If deleted transaction was GET, we increase customer's dues (delta = +amount)
        val delta = if (transaction.type == "GIVE") -transaction.amount else transaction.amount
        val timestamp = System.currentTimeMillis()

        database.withTransaction {
            transactionDao.deleteTransaction(transaction)
            customerDao.updateCustomerDues(transaction.customerId, delta, timestamp)
        }
    }
}
