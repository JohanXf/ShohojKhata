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

    // Dark mode setting: true = Dark Mode (opposite), false = Light Mode (normal)
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }

    // Notifications enabled setting: true = Enabled, false = Disabled
    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
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

    // Google Sign-In authentication states
    private val _googleEmail = MutableStateFlow<String?>(prefs.getString("google_email", null))
    val googleEmail: StateFlow<String?> = _googleEmail.asStateFlow()

    private val _googleName = MutableStateFlow<String?>(prefs.getString("google_name", null))
    val googleName: StateFlow<String?> = _googleName.asStateFlow()

    private val _googlePhone = MutableStateFlow<String?>(prefs.getString("google_phone", ""))
    val googlePhone: StateFlow<String?> = _googlePhone.asStateFlow()

    // Active App Mode role: "MERCHANT" or "CLIENT"
    private val _appMode = MutableStateFlow(prefs.getString("app_mode", "CLIENT") ?: "CLIENT")
    val appMode: StateFlow<String> = _appMode.asStateFlow()

    // Premium Subscription Status for Store Creation
    private val _isPremiumMerchant = MutableStateFlow(prefs.getBoolean("is_premium_merchant", false))
    val isPremiumMerchant: StateFlow<Boolean> = _isPremiumMerchant.asStateFlow()

    // Set of Joined Shop IDs by this Client
    private val _joinedStores = MutableStateFlow<Set<String>>(prefs.getStringSet("joined_stores", emptySet()) ?: emptySet())
    val joinedStores: StateFlow<Set<String>> = _joinedStores.asStateFlow()

    fun signInWithGoogle(name: String, email: String) {
        _googleEmail.value = email
        _googleName.value = name
        prefs.edit()
            .putString("google_email", email)
            .putString("google_name", name)
            .apply()
    }

    fun linkPhoneAndPin(phone: String, pin: String) {
        _googlePhone.value = phone
        prefs.edit().putString("google_phone", phone).apply()
        
        // Save security credentials in active user DB profile
        val currentUser = _authenticatedUser.value
        if (currentUser != null) {
            viewModelScope.launch {
                val updated = currentUser.copy(phone = phone, pin = pin)
                repository.updateUser(updated)
                _authenticatedUser.value = updated
            }
        }
    }

    fun setAppMode(mode: String) {
        _appMode.value = mode
        prefs.edit().putString("app_mode", mode).apply()
    }

    fun activatePremiumMerchant(shopName: String, shopType: String, location: String, upiId: String) {
        _isPremiumMerchant.value = true
        prefs.edit().putBoolean("is_premium_merchant", true).apply()
        
        // Update user shop properties
        viewModelScope.launch {
            val user = repository.getFirstUser()
            if (user != null) {
                val updated = user.copy(
                    shopName = shopName,
                    shopType = shopType,
                    upiId = upiId,
                    name = _googleName.value ?: user.name
                )
                repository.updateUser(updated)
                _authenticatedUser.value = updated
            }
        }
    }

    fun joinShop(shopId: String) {
        val currentSet = _joinedStores.value.toMutableSet()
        currentSet.add(shopId)
        _joinedStores.value = currentSet
        prefs.edit().putStringSet("joined_stores", currentSet).apply()

        val targetOwnerId = when (shopId) {
            "1" -> 1
            "2" -> 2
            "3" -> 3
            else -> shopId.toIntOrNull() ?: (_authenticatedUser.value?.id ?: 1)
        }

        // Associate or insert matching customer profile inside merchant's books so they sync
        viewModelScope.launch {
            val name = _googleName.value ?: "Faizen Ahmed"
            val phone = _googlePhone.value?.takeIf { it.isNotEmpty() } ?: "+91 98765 00000"
            val allCustomers = repository.getCustomers(targetOwnerId).first()
            val existing = allCustomers.firstOrNull { it.name.lowercase() == name.lowercase() }
            if (existing == null) {
                repository.insertCustomer(
                    ownerId = targetOwnerId,
                    name = name,
                    phone = phone,
                    email = _googleEmail.value,
                    isJoined = true
                )
            } else {
                repository.updateCustomer(existing.copy(isJoined = true))
            }
        }
    }

    fun leaveShop(shopId: String) {
        val currentSet = _joinedStores.value.toMutableSet()
        currentSet.remove(shopId)
        _joinedStores.value = currentSet
        prefs.edit().putStringSet("joined_stores", currentSet).apply()

        val targetOwnerId = when (shopId) {
            "1" -> 1
            "2" -> 2
            "3" -> 3
            else -> shopId.toIntOrNull() ?: (_authenticatedUser.value?.id ?: 1)
        }

        viewModelScope.launch {
            val name = _googleName.value ?: "Faizen Ahmed"
            val allCustomers = repository.getCustomers(targetOwnerId).first()
            val existing = allCustomers.firstOrNull { it.name.lowercase() == name.lowercase() }
            if (existing != null) {
                repository.updateCustomer(existing.copy(isJoined = false))
            }
        }
    }

    fun logoutGoogle() {
        _googleEmail.value = null
        _googleName.value = null
        _googlePhone.value = ""
        _isPremiumMerchant.value = false
        _joinedStores.value = emptySet()
        prefs.edit()
            .remove("google_email")
            .remove("google_name")
            .remove("google_phone")
            .remove("is_premium_merchant")
            .remove("joined_stores")
            .apply()
    }

    // PIN lock screen state
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    // Selected customer for detail screen
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    // Supabase Synchronization State
    enum class SyncState { IDLE, SYNCING, SUCCESS, ERROR }
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun isSupabaseActive(): Boolean = false

    fun triggerSync() {
        viewModelScope.launch {
            _syncState.value = SyncState.SYNCING
            val success = repository.syncWithFirebase()
            if (success) {
                _syncState.value = SyncState.SUCCESS
                // Refresh active customer data to update screens
                refreshSelectedCustomer()
            } else {
                _syncState.value = SyncState.ERROR
            }
        }
    }

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
            var user = repository.getFirstUser()
            if (user == null) {
                // Seed default Shop Owner 1 (Laxmi Grocery)
                repository.registerUser(
                    name = "Subir Mukherjee",
                    phone = "+91 98765 43210",
                    shopName = "Laxmi Grocery",
                    shopType = "Kirana store",
                    upiId = "laxmigrocery@upi",
                    pin = "1234",
                    id = 1
                )
                // Seed Owner 2 (Mayer Doa Enterprise)
                repository.registerUser(
                    name = "Abdul Hamid",
                    phone = "+91 98765 88888",
                    shopName = "Mayer Doa Enterprise",
                    shopType = "Electronics & hardware",
                    upiId = "mayerdoa@upi",
                    pin = "5678",
                    id = 2
                )
                // Seed Owner 3 (Milon Tea Stall)
                repository.registerUser(
                    name = "Milon Kanti",
                    phone = "+91 98765 99999",
                    shopName = "Milon Tea Stall",
                    shopType = "Tea & snacks",
                    upiId = "milontea@upi",
                    pin = "0000",
                    id = 3
                )

                // Seed some customers for Owner 1 (Laxmi Grocery)
                val sujataId = repository.insertCustomer(1, "Sujata di", "+91 91234 56780", null, false)
                repository.insertCustomer(1, "Imran (mechanic)", "+91 99887 12345", null, false)
                repository.insertCustomer(1, "Ravi da", "+91 93333 44455", null, false)
                repository.insertCustomer(1, "Amit (3rd floor)", "+91 98765 43210", null, false)
                repository.insertCustomer(1, "Priyanka madam", "+91 90000 11122", null, false)

                // Add Sujata di's transactions
                repository.addTransaction(sujataId.toInt(), 320.0, "GIVE", "Vegetables", "CASH")
                repository.addTransaction(sujataId.toInt(), 320.0, "GET", "Paid", "CASH")
                repository.addTransaction(sujataId.toInt(), 50.0, "GIVE", "Cha", "CASH")
                repository.addTransaction(sujataId.toInt(), 60.0, "GIVE", "Gorom moshla", "CASH")

                // Seed "Faizen Ahmed" customer accounts under all three shops to show live bills
                val faizen1 = repository.insertCustomer(1, "Faizen Ahmed", "+91 91234 00000", "faizennahmed@gmail.com", true)
                repository.addTransaction(faizen1.toInt(), 120.0, "GIVE", "Amul Butter & bread", "CASH")
                repository.addTransaction(faizen1.toInt(), 120.0, "GIVE", "Cadbury Celebration pack", "CASH")

                val faizen2 = repository.insertCustomer(2, "Faizen Ahmed", "+91 91234 00000", "faizennahmed@gmail.com", true)
                repository.addTransaction(faizen2.toInt(), 1500.0, "GIVE", "Electric Stand Fan", "UPI")
                repository.addTransaction(faizen2.toInt(), 300.0, "GET", "Advance payment", "UPI")

                val faizen3 = repository.insertCustomer(3, "Faizen Ahmed", "+91 91234 00000", "faizennahmed@gmail.com", true)
                repository.addTransaction(faizen3.toInt(), 25.0, "GIVE", "Lemon Tea & Biscuits", "CASH")
                repository.addTransaction(faizen3.toInt(), 30.0, "GIVE", "Special Milk Tea & Cake", "CASH")
                repository.addTransaction(faizen3.toInt(), 30.0, "GIVE", "Hot Tea x 3", "CASH")

                // Seed Siam Rahman customer accounts
                val siam1 = repository.insertCustomer(1, "Siam Rahman", "+91 99887 76655", "siam.rahman@gmail.com", true)
                repository.addTransaction(siam1.toInt(), 90.0, "GIVE", "Rin soap & Surf Excel", "CASH")

                user = repository.getFirstUser()
            }

            if (user != null) {
                _authenticatedUser.value = user
                _isLocked.value = false
                if (isSupabaseActive()) {
                    triggerSync()
                }
            } else {
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

                // Synchronously try signing up with Supabase Auth

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

            // Background Supabase sign-in & sync
            
            true
        } else {
            _pinError.value = "ভুল পিন! আবার চেষ্টা করুন।" // Incorrect PIN! Try again.
            false
        }
    }

    // Custom Cloud restoration using Supabase Auth Email and PIN
    fun signInWithSupabaseEmail(email: String, pin: String, onResult: (Boolean, String) -> Unit) {
        onResult(false, "Supabase support removed.")
    }

    fun logout() {
        _authenticatedUser.value = null
        _isLocked.value = true
        _pinError.value = null
    }

    fun updateProfile(name: String, shopName: String, shopType: String, upiId: String, pin: String, avatarUrl: String? = null, bannerUrl: String? = null) {
        val currentUser = _authenticatedUser.value ?: return
        viewModelScope.launch {
            val updatedUser = currentUser.copy(
                name = name,
                shopName = shopName,
                shopType = shopType,
                upiId = upiId,
                pin = pin,
                avatarUrl = avatarUrl ?: currentUser.avatarUrl,
                bannerUrl = bannerUrl ?: currentUser.bannerUrl
            )
            repository.updateUser(updatedUser)
            _authenticatedUser.value = updatedUser
            
            if (isSupabaseActive()) {
                triggerSync()
            }
        }
    }

    // --- Customer Ledger Actions ---
    fun addCustomer(name: String, phone: String, email: String?, isJoined: Boolean = false, onSuccess: () -> Unit) {
        val currentUser = _authenticatedUser.value ?: return
        viewModelScope.launch {
            repository.insertCustomer(currentUser.id, name, phone, email, isJoined)
            triggerSync()
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
            triggerSync()
        }
    }

    fun deleteCustomer(customer: Customer, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            _selectedCustomer.value = null
            triggerSync()
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
            triggerSync()
            onSuccess()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            refreshSelectedCustomer()
            triggerSync()
        }
    }
}
