package com.example.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- Authentication & Identity Pipeline ---
    
    val currentUserUid: String?
        get() = auth.currentUser?.uid

    suspend fun getUserProfile(uid: String): FirebaseUser? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(FirebaseUser::class.java)
    }

    suspend fun updateUserProfile(user: FirebaseUser) {
        if (user.uid.isNotEmpty()) {
            firestore.collection("users").document(user.uid).set(user).await()
        }
    }

    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun verifyAndOnboardUser(uid: String, phone: String, pin: String): Boolean {
        val pinHash = hashPin(pin)
        val snapshot = firestore.collection("users").document(uid).get().await()
        return if (!snapshot.exists() || snapshot.getString("phone_number") == null) {
            val newUser = FirebaseUser(
                uid = uid,
                phone_number = phone,
                pin_hash = pinHash
            )
            updateUserProfile(newUser)
            true
        } else {
            val savedHash = snapshot.getString("pin_hash")
            savedHash == pinHash
        }
    }

    // --- Real-Time Sync & Pub/Sub Models (Using Flows) ---

    fun observeShops(): Flow<List<Shop>> = callbackFlow {
        val listener = firestore.collection("shops")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val shops = snapshot.toObjects(Shop::class.java)
                    trySend(shops)
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeMembershipsByMerchant(merchantUid: String): Flow<List<Membership>> = callbackFlow {
        val listener = firestore.collection("memberships")
            .whereEqualTo("shopId", merchantUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val members = snapshot.toObjects(Membership::class.java)
                    trySend(members)
                }
            }
        awaitClose { listener.remove() }
    }
    
    fun observeLedgerEntries(shopId: String, customerUid: String): Flow<List<LedgerEntry>> = callbackFlow {
        val listener = firestore.collection("ledger_entries")
            .whereEqualTo("shopId", shopId)
            .whereEqualTo("customerUid", customerUid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val entries = snapshot.toObjects(LedgerEntry::class.java)
                    trySend(entries)
                }
            }
        awaitClose { listener.remove() }
    }

    // --- Write Operations & Logic Constraints ---

    suspend fun addShop(shop: Shop) {
        val ref = firestore.collection("shops").document()
        val newShop = shop.copy(id = ref.id, ownerId = currentUserUid ?: "")
        ref.set(newShop).await()
    }

    suspend fun addMembership(membership: Membership): Boolean {
        // Enforce limitation limit (Free vs Premium) - checking before write
        val shopOwner = currentUserUid ?: return false
        val userProfile = getUserProfile(shopOwner)
        
        if (userProfile?.planType == "free") {
            // Count active memberships
            val limitCheck = firestore.collection("memberships")
                .whereEqualTo("shopId", shopOwner)
                .get()
                .await()
            if (limitCheck.size() >= 15) {
                // Abort and simulate LIMIT_EXCEEDED_UPGRADE_REQUIRED
                throw Exception("LIMIT_EXCEEDED_UPGRADE_REQUIRED")
            }
        }
        
        val docId = "${shopOwner}_${membership.clientPhoneNumber}"
        val newMembership = membership.copy(id = docId, shopId = shopOwner)
        firestore.collection("memberships").document(docId).set(newMembership).await()
        return true
    }

    suspend fun addLedgerEntry(entry: LedgerEntry) {
        val ref = firestore.collection("ledger_entries").document()
        val newEntry = entry.copy(id = ref.id, shopId = currentUserUid ?: "")
        ref.set(newEntry).await()
        
        // At this point, the backend Cloud Function will trigger the pushing of FCM to client.
    }

    // --- Feature Implementations --- 

    fun generateWhatsAppIntent(context: Context, phoneNumber: String, isPremium: Boolean) {
        if (!isPremium) {
            // Blocked status for free tier
            throw Exception("PREMIUM_FEATURE_LOCKED")
        }
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
        // Assuming activity invocation wrapping this.
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
