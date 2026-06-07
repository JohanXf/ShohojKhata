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
        val id = userDao.insertUser(user)
        val insertedUser = user.copy(id = id.toInt())
        
        // Attempt cloud sync
        SupabaseClient.api?.let { api ->
            runCatching { api.upsertUser(insertedUser) }
        }
        
        return id
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        
        // Attempt cloud sync
        SupabaseClient.api?.let { api ->
            runCatching { api.upsertUser(user) }
        }
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
        val id = customerDao.insertCustomer(customer)
        val insertedCustomer = customer.copy(id = id.toInt())

        // Attempt cloud sync
        SupabaseClient.api?.let { api ->
            runCatching { api.upsertCustomers(listOf(insertedCustomer)) }
        }

        return id
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)

        // Attempt cloud sync
        SupabaseClient.api?.let { api ->
            runCatching { api.upsertCustomers(listOf(customer)) }
        }
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)

        // Attempt cloud sync
        SupabaseClient.api?.let { api ->
            runCatching { api.deleteCustomer("id=eq.${customer.id}") }
        }
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

        val delta = if (type == "GIVE") amount else -amount
        var transactionId: Long = 0
        
        database.withTransaction {
            transactionId = transactionDao.insertTransaction(transaction)
            customerDao.updateCustomerDues(customerId, delta, timestamp)
        }

        val insertedTx = transaction.copy(id = transactionId.toInt())

        // Attempt cloud sync of both transaction and updated customer
        SupabaseClient.api?.let { api ->
            runCatching {
                api.upsertTransactions(listOf(insertedTx))
                getCustomerById(customerId)?.let { api.upsertCustomers(listOf(it)) }
            }
        }

        return transactionId
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        val delta = if (transaction.type == "GIVE") -transaction.amount else transaction.amount
        val timestamp = System.currentTimeMillis()

        database.withTransaction {
            transactionDao.deleteTransaction(transaction)
            customerDao.updateCustomerDues(transaction.customerId, delta, timestamp)
        }

        // Attempt cloud sync
        SupabaseClient.api?.let { api ->
            runCatching {
                api.deleteTransaction("id=eq.${transaction.id}")
                getCustomerById(transaction.customerId)?.let { api.upsertCustomers(listOf(it)) }
            }
        }
    }

    // --- High-level Full Database Sync and Merge function ---
    suspend fun syncWithSupabase(): Boolean {
        val api = SupabaseClient.api ?: return false
        try {
            // 1. Sync User Profile
            val localUser = getFirstUser()
            if (localUser != null) {
                runCatching { api.upsertUser(localUser) }
            }

            // 2. Sync Customers (Push)
            val localCustomers = customerDao.getAllCustomersDirect()
            if (localCustomers.isNotEmpty()) {
                api.upsertCustomers(localCustomers)
            }

            // 3. Sync Transactions (Push)
            val localTransactions = transactionDao.getAllTransactionsDirect()
            if (localTransactions.isNotEmpty()) {
                api.upsertTransactions(localTransactions)
            }

            // 4. Remote Pull & Merge
            if (localUser != null) {
                // Pull remote customers for this owner and save locally
                val remoteCustomers = api.getCustomers("ownerId=eq.${localUser.id}")
                for (remoteCust in remoteCustomers) {
                    customerDao.insertCustomer(remoteCust)
                }

                // Pull remote transactions and save locally
                val remoteTransactions = api.getTransactions()
                for (remoteTx in remoteTransactions) {
                    transactionDao.insertTransaction(remoteTx)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun getAllCustomersDirect(): List<Customer> = customerDao.getAllCustomersDirect()
    suspend fun getAllTransactionsDirect(): List<Transaction> = transactionDao.getAllTransactionsDirect()
}
