package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = SohojRepository(database)

    // Language setting: true = Bengali, false = English
    private val prefs = application.getSharedPreferences("sohoj_khata_prefs", android.content.Context.MODE_PRIVATE)
    private val _isBengali = MutableStateFlow(prefs.getBoolean("is_bengali", true))
    val isBengali: StateFlow<Boolean> = _isBengali.asStateFlow()

    fun setLanguage(bengali: Boolean) {
        _isBengali.value = bengali
        prefs.edit().putBoolean("is_bengali", bengali).apply()
    }

    // Monitor if any profile exists in the DB
    val registeredUserFlow: StateFlow<User?> = repository.firstUserFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Current authenticated session
    private val _authenticatedUser = MutableStateFlow<User?>(null)
    val authenticatedUser: StateFlow<User?> = _authenticatedUser.asStateFlow()

    // PIN lock screen state
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    // Selected customer for detail screen
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    // Query list of customers for authenticated shop
    val customers: StateFlow<List<Customer>> = _authenticatedUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getCustomers(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Transactions of the selected customer
    val selectedCustomerTransactions: StateFlow<List<Transaction>> = _selectedCustomer
        .flatMapLatest { customer ->
            if (customer != null) {
                repository.getTransactionsForCustomer(customer.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All transactions for analytics/reporting
    val allTransactions: StateFlow<List<Transaction>> = _authenticatedUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getTransactionsForOwner(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Auto-authenticate and auto-seed default owner Laxmi Grocery / Subir Mukherjee on first launch
        viewModelScope.launch {
            val user = repository.getFirstUser()
            if (user == null) {
                val ownerId = repository.registerUser(
                    name = "Subir Mukherjee",
                    phone = "+91 98765 43210",
                    shopName = "Laxmi Grocery",
                    shopType = "Kirana store",
                    upiId = "laxmigrocery@upi",
                    pin = "1234"
                )
                
                // Seed the 5 customers from screenshots:
                val sujataId = repository.insertCustomer(ownerId.toInt(), "Sujata di", "+91 91234 56780", null, false)
                repository.insertCustomer(ownerId.toInt(), "Imran (mechanic)", "+91 99887 12345", null, false)
                repository.insertCustomer(ownerId.toInt(), "Ravi da", "+91 93333 44455", null, false)
                repository.insertCustomer(ownerId.toInt(), "Amit (3rd floor)", "+91 98765 43210", null, false)
                repository.insertCustomer(ownerId.toInt(), "Priyanka madam", "+91 90000 11122", null, false)

                // Add timeline history for Sujata di (Vegetables ₹320, Cash Received -₹320, Cha ₹50, Gorom moshla ₹60)
                repository.addTransaction(sujataId.toInt(), 320.0, "GIVE", "Vegetables", "CASH")
                repository.addTransaction(sujataId.toInt(), 320.0, "GET", "Paid", "CASH")
                repository.addTransaction(sujataId.toInt(), 50.0, "GIVE", "Cha", "CASH")
                repository.addTransaction(sujataId.toInt(), 60.0, "GIVE", "Gorom moshla", "CASH")

                val newUser = repository.getUserById(ownerId.toInt())
                if (newUser != null) {
                    _authenticatedUser.value = newUser
                    _isLocked.value = false
                }
            } else {
                _authenticatedUser.value = user
                _isLocked.value = false
            }
        }
    }

    // --- Onboarding & Auth Actions ---
    fun registerOwner(
        name: String,
        phone: String,
        shopName: String,
        shopType: String,
        upiId: String,
        pin: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val userId = repository.registerUser(name, phone, shopName, shopType, upiId, pin)
            val user = repository.getUserById(userId.toInt())
            if (user != null) {
                _authenticatedUser.value = user
                _isLocked.value = false
                onSuccess()
            }
        }
    }

    fun loginWithPin(pin: String): Boolean {
        val user = registeredUserFlow.value
        return if (user != null && user.pin == pin) {
            _authenticatedUser.value = user
            _isLocked.value = false
            _pinError.value = null
            true
        } else {
            _pinError.value = "ভুল পিন! আবার চেষ্টা করুন।" // Incorrect PIN! Try again.
            false
        }
    }

    fun logout() {
        _authenticatedUser.value = null
        _isLocked.value = true
        _pinError.value = null
    }

    fun updateProfile(name: String, shopName: String, shopType: String, upiId: String, pin: String) {
        val currentUser = _authenticatedUser.value ?: return
        viewModelScope.launch {
            val updatedUser = currentUser.copy(
                name = name,
                shopName = shopName,
                shopType = shopType,
                upiId = upiId,
                pin = pin
            )
            repository.updateUser(updatedUser)
            _authenticatedUser.value = updatedUser
        }
    }

    // --- Customer Ledger Actions ---
    fun addCustomer(name: String, phone: String, email: String?, isJoined: Boolean = false, onSuccess: () -> Unit) {
        val currentUser = _authenticatedUser.value ?: return
        viewModelScope.launch {
            repository.insertCustomer(currentUser.id, name, phone, email, isJoined)
            onSuccess()
        }
    }

    fun selectCustomer(customer: Customer?) {
        _selectedCustomer.value = customer
    }

    fun refreshSelectedCustomer() {
        val currentSelected = _selectedCustomer.value ?: return
        viewModelScope.launch {
            val freshCustomer = repository.getCustomerById(currentSelected.id)
            _selectedCustomer.value = freshCustomer
        }
    }

    fun updateCustomerDetails(customer: Customer, name: String, phone: String, email: String?) {
        viewModelScope.launch {
            val updated = customer.copy(name = name, phone = phone, email = email)
            repository.updateCustomer(updated)
            _selectedCustomer.value = updated
        }
    }

    fun deleteCustomer(customer: Customer, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            _selectedCustomer.value = null
            onSuccess()
        }
    }

    // --- Transaction Actions ---
    fun addTransaction(
        amount: Double,
        type: String, // GIVE or GET
        description: String,
        paymentMethod: String,
        onSuccess: () -> Unit
    ) {
        val currentCust = _selectedCustomer.value ?: return
        viewModelScope.launch {
            repository.addTransaction(currentCust.id, amount, type, description, paymentMethod)
            refreshSelectedCustomer()
            onSuccess()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            refreshSelectedCustomer()
        }
    }
}
