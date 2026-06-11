package com.example.data

import kotlinx.coroutines.flow.Flow
import androidx.room.withTransaction
import kotlinx.coroutines.tasks.await

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
        pin: String,
        id: Int = 0
    ): Long {
        val finalId = if (id > 0) id else {
            (java.util.UUID.randomUUID().hashCode() and 0x7FFFFFFF).let { if (it == 0) 1 else it }
        }
        val user = User(
            id = finalId,
            name = name,
            phone = phone,
            shopName = shopName,
            shopType = shopType,
            upiId = upiId,
            pin = pin
        )
        val insertedId = userDao.insertUser(user)
        val insertedUser = user.copy(id = insertedId.toInt())
        

        
        return insertedId
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
        val randomId = (java.util.UUID.randomUUID().hashCode() and 0x7FFFFFFF).let { if (it == 0) 1 else it }
        val customer = Customer(
            id = randomId,
            ownerId = ownerId,
            name = name,
            phone = phone,
            email = email,
            totalDues = 0.0,
            isJoined = isJoined
        )
        val id = customerDao.insertCustomer(customer)
        val insertedCustomer = customer.copy(id = id.toInt())



        return id
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
        val randomTxId = (java.util.UUID.randomUUID().hashCode() and 0x7FFFFFFF).let { if (it == 0) 1 else it }
        val transaction = Transaction(
            id = randomTxId,
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



        return transactionId
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        val delta = if (transaction.type == "GIVE") -transaction.amount else transaction.amount
        val timestamp = System.currentTimeMillis()

        database.withTransaction {
            transactionDao.deleteTransaction(transaction)
            customerDao.updateCustomerDues(transaction.customerId, delta, timestamp)
        }


    }

    suspend fun syncWithFirebase(): Boolean {
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val dbMap = mutableMapOf<String, Any>()
            
            val user = userDao.getFirstUser()
            if (user != null) {
                dbMap["user"] = user
                
                val customers = customerDao.getAllCustomersDirect()
                dbMap["customers"] = customers
                
                val txs = transactionDao.getAllTransactionsDirect()
                dbMap["transactions"] = txs
                
                // Upload all backup to Firebase Firestore
                firestore.collection("backups").document("user_${user.id}").set(dbMap).await()
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun updateCustomerDirectly(customer: Customer) {
        customerDao.insertCustomer(customer)
    }

    suspend fun replaceCustomerTransactions(customerId: Int, txList: List<Transaction>) {
        database.withTransaction {
            transactionDao.deleteTransactionsByCustomer(customerId)
            for (tx in txList) {
                transactionDao.insertTransaction(tx)
            }
        }
    }

    suspend fun getAllCustomersDirect(): List<Customer> = customerDao.getAllCustomersDirect()
    suspend fun getAllTransactionsDirect(): List<Transaction> = transactionDao.getAllTransactionsDirect()
}
