package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.LedgerViewModel
import com.example.data.Customer
import com.example.data.Transaction
import com.example.data.User
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    isDark: Boolean = isSystemInDarkTheme(),
    containerColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val containerBg = containerColor ?: (if (isDark) Color(0xFF8B5E3C) else Color.White)
    val borderCol = if (isDark) ForestGreen.copy(alpha = 0.25f) else ForestGreen.copy(alpha = 0.12f)
    val shadowElev = if (isDark) 1.dp else 4.dp
    
    val cardModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }
    
    Card(
        modifier = cardModifier
            .border(
                width = 1.dp,
                color = borderCol,
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = shadowElev)
    ) {
        Box(
            modifier = Modifier
                .background(containerBg, shape = shape)
        ) {
            content()
        }
    }
}

@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    isDark: Boolean = isSystemInDarkTheme(),
    containerColor: Color = ForestGreen,
    lightShadow: Color? = null,
    darkShadow: Color? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = if (containerColor == Color.White) ForestGreen else Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

// Represents current navigation tab/screen
enum class ActiveScreen {
    Dashboard,
    CustomerDetail,
    Reports,
    Profile
}

@Composable
fun LanguageToggleButton(viewModel: LedgerViewModel, modifier: Modifier = Modifier) {
    val isBengali by viewModel.isBengali.collectAsState()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .border(1.dp, ForestGreen.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .clickable { viewModel.setLanguage(!isBengali) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = "Language Switcher",
            tint = ForestGreen,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (isBengali) "English" else "বাংলা",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )
    }
}

@Composable
fun OnboardingScreen(
    viewModel: LedgerViewModel,
    onSuccess: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var shopType by remember { mutableStateOf(if (isBengali) "মুদি দোকান (Grocery)" else "Grocery Shop") }
    var upiId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }

    var expandedType by remember { mutableStateOf(false) }
    val shopTypes = if (isBengali) {
        listOf(
            "মুদি দোকান (Grocery)",
            "ফার্মেসী (Pharmacy)",
            "কাপড়ের দোকান (Clothing Store)",
            "মোবাইল ও ইলেকট্রনিক্স (Electronics)",
            "রেস্টুরেন্ট ও ক্যাফে (Food & Cafe)",
            "সেলুন ও পার্লার (Salon)",
            "অন্যান্য ব্যবসা (Others)"
        )
    } else {
        listOf(
            "Grocery Shop",
            "Pharmacy",
            "Clothing Store",
            "Mobile & Electronics",
            "Restaurant & Cafe",
            "Salon & Parlor",
            "Other Businesses"
        )
    }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBg)
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    LanguageToggleButton(viewModel = viewModel)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = "App Icon",
                    tint = ForestGreen,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isBengali) "সহজ খাতা (Sohoj Khata)" else "Sohoj Khata",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text(
                    text = if (isBengali) "আপনার দোকানের হিসাব, সুন্দরভাবে রাখা।" else "Your digital shop register. Beautifully kept.",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isBengali) "দোকান খোলার ফর্ম" else "Register Your Shop",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isBengali) "দোকানদারের নাম" else "Owner's Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = ForestGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isBengali) "মোবাইল নম্বর" else "Mobile Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = ForestGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            label = { Text(if (isBengali) "দোকানের নাম" else "Shop Name") },
                            leadingIcon = { Icon(Icons.Default.Store, null, tint = ForestGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Shop Type Dropdown Selector
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = shopType,
                                onValueChange = {},
                                label = { Text(if (isBengali) "ব্যবসার ধরন" else "Business Type") },
                                leadingIcon = { Icon(Icons.Default.Category, null, tint = ForestGreen) },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { expandedType = !expandedType }) {
                                        Icon(Icons.Default.ArrowDropDown, null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedType = true }
                            )
                            DropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = { expandedType = false }
                            ) {
                                shopTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            shopType = type
                                            expandedType = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { upiId = it },
                            label = { Text(if (isBengali) "UPI আইডি / পেমেন্ট নম্বর" else "UPI ID / Pay Number") },
                            leadingIcon = { Icon(Icons.Default.QrCode, null, tint = ForestGreen) },
                            placeholder = { Text(if (isBengali) "merchant@upi বা bKash নম্বর" else "merchant@upi or bKash number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = pin,
                            onValueChange = { if (it.length <= 4) pin = it },
                            label = { Text(if (isBengali) "৪-ডিজিটের পিন লক" else "4-Digit Security PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = ForestGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = confirmPin,
                            onValueChange = { if (it.length <= 4) confirmPin = it },
                            label = { Text(if (isBengali) "পিন নিশ্চিত করুন" else "Confirm PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = ForestGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            if (viewModel.isSupabaseActive()) {
                item {
                    TextButton(onClick = { showRestoreDialog = true }) {
                        Icon(Icons.Default.CloudDownload, null, tint = ForestGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBengali) "আগের খাতা ক্লাউড থেকে রিস্টোর করুন" else "Restore Existing Ledger from Cloud",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Action submit button at bottom overlay
        NeumorphicButton(
            onClick = {
                if (name.isBlank() || phone.isBlank() || shopName.isBlank() || pin.length != 4) {
                    val alertText = if (isBengali) "অনুগ্রহ করে সব তথ্য এবং ৪ ডিজিটের পিন সঠিক দিন" else "Please fill all details and set a 4-digit PIN"
                    Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show()
                } else if (pin != confirmPin) {
                    val alertText = if (isBengali) "পিন দুটি মিলেনি!" else "PINs do not match!"
                    Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.registerOwner(name, phone, shopName, shopType, upiId, pin) {
                        val alertText = if (isBengali) "খাতা সফলভাবে খোলা হয়েছে!" else "Registered ledger successfully!"
                        Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(28.dp),
            containerColor = ForestGreen
        ) {
            Icon(Icons.Default.Check, contentDescription = "Confirm", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isBengali) "নতুন খাতা চালু করুন" else "Launch Shop Ledger", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (showRestoreDialog) {
            RestoreFromCloudDialog(
                viewModel = viewModel,
                onDismiss = { showRestoreDialog = false }
            )
        }
    }
}

@Composable
fun LockScreen(
    viewModel: LedgerViewModel,
    onSuccess: () -> Unit
) {
    val registeredUser by viewModel.registeredUserFlow.collectAsState()
    val pinError by viewModel.pinError.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LanguageToggleButton(viewModel = viewModel)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Safe Lock Icon",
                tint = DeepGold,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = registeredUser?.shopName ?: (if (isBengali) "সহজ খাতা" else "Sohoj Khata"),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = if (isBengali) "খাতা খুলতে পিন কোড দিন" else "Enter PIN to Unlock Ledger",
                fontSize = 14.sp,
                color = Color.LightGray.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // PIN Indicator Dots
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..4) {
                    val isActive = enteredPin.length >= i
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isActive) DeepGold else Color.Gray.copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            pinError?.let {
                val errorMsg = if (it == "ভুল পিন! আবার চেষ্টা করুন।") {
                    if (isBengali) "ভুল পিন! আবার চেষ্টা করুন।" else "Incorrect PIN! Try again."
                } else {
                    it
                }
                Text(errorMsg, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // 3x4 Safe Numeric Keyboard
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            val clearLabel = if (isBengali) "খালি" else "Clear"
            val deleteLabel = if (isBengali) "মুছুন" else "Delete"
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(clearLabel, "0", deleteLabel)
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { num ->
                        IconButton(
                            onClick = {
                                if (num == clearLabel) {
                                    enteredPin = ""
                                } else if (num == deleteLabel) {
                                    if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                } else {
                                    if (enteredPin.length < 4) {
                                        enteredPin += num
                                        if (enteredPin.length == 4) {
                                            val authorized = viewModel.loginWithPin(enteredPin)
                                            if (authorized) {
                                                onSuccess()
                                            } else {
                                                enteredPin = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            if (num == deleteLabel) {
                                Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color.White)
                            } else if (num == clearLabel) {
                                Text("C", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(num, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.isSupabaseActive()) {
            TextButton(
                onClick = { showRestoreDialog = true },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.CloudDownload, null, tint = DeepGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBengali) "ক্লাউড থেকে খাতা রিস্টোর করুন" else "Restore Ledger from Cloud",
                    color = DeepGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        if (showRestoreDialog) {
            RestoreFromCloudDialog(
                viewModel = viewModel,
                onDismiss = { showRestoreDialog = false }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: LedgerViewModel,
    onNavigateToCustomer: (Customer) -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val ownerUser by viewModel.authenticatedUser.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Dues totals reactive calculation
    val (totalWeGet, totalWeGive) = remember(customers) {
        var weGet = 0.0
        var weGive = 0.0
        customers.forEach {
            if (it.totalDues > 0) {
                weGet += it.totalDues
            } else if (it.totalDues < 0) {
                weGive += (-it.totalDues)
            }
        }
        Pair(weGet, weGive)
    }

    val netBalance = totalWeGet - totalWeGive

    Scaffold(
        containerColor = WarmBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Elegant Emerald Top Curved Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForestGreen,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Row 1: App Header & Language Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = ownerUser?.shopName ?: (if (isBengali) "আমার দোকান" else "My Shop"),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = ownerUser?.name ?: (if (isBengali) "মালিক" else "Owner"),
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        LanguageToggleButton(viewModel = viewModel)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Row 2: Total Net Balance Display (Image 1 top element)
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBengali) "মোট নেট হিসাব (Net Balance)" else "Net Balance",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹ ${String.format("%,.0f", netBalance)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Bottom Content Section with Light Mint Ground and elegant white cards
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Side-by-side Receivable / Payable Cards (Exactly resembling Income / Expense in Image 1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Receivable Card ("You Will Get" -> Income style)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, ForestGreen.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(KhataRedBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = KhataRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBengali) "পাবেন" else "You Get",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹ ${String.format("%,.0f", totalWeGet)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KhataRed
                                )
                            }
                        }
                    }

                    // Payable Card ("You Will Give" -> Expense style)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, ForestGreen.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(KhataGreenBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = KhataGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBengali) "দেবেন" else "You Give",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹ ${String.format("%,.0f", totalWeGive)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KhataGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Box and Add New Customer (90-10% Ratio)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(if (isBengali) "নাম বা মোবাইল দিয়ে সার্চ করুন..." else "Search by name or mobile...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ForestGreen) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.weight(0.9f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = ForestGreen.copy(alpha = 0.15f),
                            focusedBorderColor = ForestGreen
                        )
                    )

                    Box(
                        modifier = Modifier
                            .weight(0.1f)
                            .height(56.dp)
                            .background(ForestGreen, shape = RoundedCornerShape(24.dp))
                            .clickable { showAddDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Customer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer list
                val filteredCustomers = remember(customers, searchQuery) {
                    customers.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.phone.contains(searchQuery)
                    }
                }

                if (filteredCustomers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = "Empty Book",
                                tint = ForestGreen.copy(alpha = 0.3f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isEmpty()) {
                                    if (isBengali) "কোনো কাস্টমার যোগ করা হয়নি!" else "No customers have been added!"
                                } else {
                                    if (isBengali) "সার্চে কোনো কাস্টমার মেলেনি!" else "No customers matched your search!"
                                },
                                fontSize = 16.sp,
                                color = NavyDark,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (searchQuery.isEmpty()) {
                                    if (isBengali) "নতুন কাস্টমার যোগ করতে উপরের '+' বোতামে চাপুন।" else "Tap the '+' button above to add your first customer."
                                } else {
                                    if (isBengali) "বানান চেক করে আবার চেষ্টা করুন।" else "Check your query spelling and retry."
                                },
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredCustomers) { cust ->
                            CustomerListItem(viewModel = viewModel, customer = cust, onClick = { onNavigateToCustomer(cust) })
                        }
                    }
                }
            }
        }

        // Add Customer Popup Dialog
        if (showAddDialog) {
            AddCustomerDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                onAdd = { name, phone, email, isJoined ->
                    viewModel.addCustomer(name, phone, email, isJoined) {
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun CustomerListItem(
    viewModel: LedgerViewModel,
    customer: Customer,
    onClick: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = ForestGreen,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circle initials avatar style - white circle background with brown initials
                val initials = if (customer.name.length >= 2) customer.name.take(2) else customer.name.take(1)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = customer.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (customer.isJoined) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color.White.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = if (isBengali) "অনলাইন" else "Online",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color.White.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = if (isBengali) "নিজে যুক্ত" else "Manual",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = customer.phone,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Dues Indicators
            Column(horizontalAlignment = Alignment.End) {
                val dues = customer.totalDues
                when {
                    dues > 0 -> {
                        Text(if (isBengali) "পাবেন" else "Get", fontSize = 11.sp, color = Color(0xFFFFCDD2), fontWeight = FontWeight.Bold)
                        Text("₹ ${String.format("%,.0f", dues)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                    }
                    dues < 0 -> {
                        Text(if (isBengali) "দেবেন" else "Give", fontSize = 11.sp, color = Color(0xFFC8E6C9), fontWeight = FontWeight.Bold)
                        Text("₹ ${String.format("%,.0f", -dues)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                    }
                    else -> {
                        Text(if (isBengali) "সমতা" else "Settled", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                        Text("₹ ০", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomerDialog(
    viewModel: LedgerViewModel,
    onDismiss: () -> Unit,
    onAdd: (String, String, String?, Boolean) -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismiss() }) {
        NeumorphicCard(
            shape = RoundedCornerShape(24.dp),
            containerColor = ForestGreen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isBengali) "নতুন কাস্টমার যোগ করুন" else "Add New Customer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "কাস্টমারের নাম" else "Customer Name", color = Color.White.copy(alpha = 0.85f)) },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ForestGreen,
                        unfocusedTextColor = ForestGreen,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.85f),
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBengali) "মোবাইল নম্বর" else "Mobile Number", color = Color.White.copy(alpha = 0.85f)) },
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = ForestGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ForestGreen,
                        unfocusedTextColor = ForestGreen,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.85f),
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(if (isBengali) "বাতিল" else "Cancel", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    NeumorphicButton(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) {
                                val alert = if (isBengali) "নাম ও মোবাইল দেয়া আবশ্যক" else "Name & Mobile are required"
                                Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                            } else {
                                onAdd(name, phone, null, false)
                            }
                        },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (isBengali) "যোগ করুন" else "Add", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerDetailScreen(
    viewModel: LedgerViewModel,
    onBack: () -> Unit
) {
    val customer by viewModel.selectedCustomer.collectAsState()
    val transactions by viewModel.selectedCustomerTransactions.collectAsState()
    val ownerUser by viewModel.authenticatedUser.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()

    var showUpiQrDialog by remember { mutableStateOf(false) }
    
    // States for the inline Add Entry form
    var inlineAmount by remember { mutableStateOf("") }
    var inlineItemName by remember { mutableStateOf("") }
    var inlineTransType by remember { mutableStateOf("GIVE") } // "GIVE" = Due (বাকি), "GET" = Paid (জমা)
    var pendingDueItems by remember { mutableStateOf(listOf<Pair<String, Double>>()) }

    val context = LocalContext.current

    if (customer == null) {
        onBack()
        return
    }

    Scaffold(
        containerColor = WarmBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // High-fidelity Curved Emerald Top Header (consistent with other screens)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForestGreen,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onBack() }) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = customer!!.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (customer!!.isJoined) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color.White.copy(alpha = 0.22f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = if (isBengali) "অনলাইন" else "Online",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color.White.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = if (isBengali) "নিজে যুক্ত" else "Manual",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = customer!!.phone,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Delete customer ledger
                    IconButton(
                        onClick = {
                            viewModel.deleteCustomer(customer!!) {
                                val alert = if (isBengali) "কাস্টমার খাতা ডিলিট করা হয়েছে!" else "Customer ledger deleted!"
                                Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Delete, "Delete Customer", tint = Color.White)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Item 1: Balance Header Card
                item {
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        containerColor = ForestGreen,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val currentDues = customer!!.totalDues
                            Text(
                                text = if (isBengali) "চলতি ব্যালেন্স" else "RUNNING BALANCE",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "₹ ${String.format("%,.0f", currentDues)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentDues > 0) Color(0xFFFF8A80) else (if (currentDues < 0) Color(0xFF81C784) else Color.White)
                            )

                            Text(
                                text = when {
                                    currentDues > 0 -> if (isBengali) "আপনার কাছে কাস্টমার ঋণী আছেন" else "Customer owes you money"
                                    currentDues < 0 -> if (isBengali) "কাস্টমার আপনার কাছে পাবেন" else "You owe this customer"
                                    else -> if (isBengali) "হিসাব সম্পূর্ণ পরিশোধ করা হয়েছে" else "Ledger completely settled"
                                },
                                fontSize = 13.sp,
                                color = if (currentDues > 0) Color(0xFFFFCDD2) else (if (currentDues < 0) Color(0xFFC8E6C9) else Color.White.copy(alpha = 0.85f)),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            // Action reminders & payment buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // UPI payment request
                                if (ownerUser?.upiId?.isNotBlank() == true && currentDues > 0) {
                                    TextButton(
                                        onClick = { showUpiQrDialog = true },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                                    ) {
                                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBengali) "পেমেন্ট QR কোড" else "Collect via QR", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }
                                }

                                // Share reminder via WhatsApp / SMS
                                TextButton(
                                    onClick = {
                                        if (currentDues <= 0) {
                                            val alert = if (isBengali) "কোনো বকেয়া dues নেই!" else "No arrears details to send!"
                                            Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                                            return@TextButton
                                        }
                                        val shareText = if (isBengali) {
                                            "প্রিয় ${customer!!.name},\nসহজ খাতা (Sohoj Khata) অনুযায়ী '${ownerUser?.shopName}' দোকানে আপনার বকেয়া হিসাব রয়েছে ₹ ${String.format("%,.0f", currentDues)}। অনুগ্রহ করে দ্রুত পরিশোধ করুন।\nধন্যবাদ!"
                                        } else {
                                            "Dear ${customer!!.name},\nAccording to Sohoj Khata, your outstanding balance with '${ownerUser?.shopName}' is ₹ ${String.format("%,.0f", currentDues)}. Please settle it at your earliest convenience.\nThank you!"
                                        }
                                        try {
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                            }
                                            val title = if (isBengali) "রিমাইন্ডার পাঠান" else "Send Payment Reminder"
                                            context.startActivity(Intent.createChooser(intent, title))
                                        } catch (e: Exception) {
                                            val alert = if (isBengali) "শেয়ার করা যায়নি" else "Failed to trigger share"
                                            Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBengali) "রিমাইন্ডার পাঠান" else "Share customer link", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Item 2: Add Entry Card (Inline)
                item {
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = ForestGreen,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            Text(
                                text = if (isBengali) "এন্ট্রি যোগ করুন" else "Add entry",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Tab selector: [+ Due] inside Rust/Red and [- Paid] inside Green
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val dueActive = inlineTransType == "GIVE"
                                NeumorphicButton(
                                    onClick = {
                                        inlineTransType = "GIVE"
                                        inlineAmount = ""
                                        inlineItemName = ""
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    containerColor = if (dueActive) KhataRed else Color.White.copy(alpha = 0.15f),
                                    content = {
                                        Text(
                                            text = if (isBengali) "+ বাকি (Due)" else "+ Due",
                                            color = if (dueActive) Color.White else Color.White.copy(alpha = 0.85f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                )

                                val paidActive = inlineTransType == "GET"
                                NeumorphicButton(
                                    onClick = {
                                        inlineTransType = "GET"
                                        inlineAmount = ""
                                        inlineItemName = ""
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    containerColor = if (paidActive) KhataGreen else Color.White.copy(alpha = 0.15f),
                                    content = {
                                        Text(
                                            text = if (isBengali) "- জমা (Paid)" else "- Paid",
                                            color = if (paidActive) Color.White else Color.White.copy(alpha = 0.85f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                )
                            }

                            if (inlineTransType == "GIVE") {
                                // DUE: Item & Amount side-by-side (50-50%)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = inlineItemName,
                                        onValueChange = { inlineItemName = it },
                                        placeholder = {
                                            Text(
                                                text = if (isBengali) "আইটেম বিবরণী" else "Write item",
                                                fontSize = 13.sp,
                                                color = ForestGreen.copy(alpha = 0.6f)
                                            )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = ForestGreen,
                                            unfocusedTextColor = ForestGreen,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = Color.White,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                    )

                                    OutlinedTextField(
                                        value = inlineAmount,
                                        onValueChange = { inlineAmount = it },
                                        placeholder = {
                                            Text(
                                                text = "₹",
                                                fontSize = 15.sp,
                                                color = ForestGreen.copy(alpha = 0.6f)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = ForestGreen,
                                            unfocusedTextColor = ForestGreen,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = Color.White,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                    )
                                }

                                // List of added pending sub-items
                                if (pendingDueItems.isNotEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        pendingDueItems.forEachIndexed { index, pair ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    IconButton(
                                                        onClick = {
                                                            pendingDueItems = pendingDueItems.toMutableList().apply { removeAt(index) }
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, "Remove", tint = Color(0xFFFF8A80), modifier = Modifier.size(14.dp))
                                                    }
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(pair.first, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                                }
                                                Text("₹ ${String.format("%,.0f", pair.second)}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // Interactive Actions: [+ Add item] and [Save] (50-50%)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val hasInput = inlineItemName.isNotBlank() && inlineAmount.toDoubleOrNull() != null
                                    NeumorphicButton(
                                        onClick = {
                                            val amt = inlineAmount.toDoubleOrNull()
                                            if (inlineItemName.isNotBlank() && amt != null && amt > 0) {
                                                pendingDueItems = pendingDueItems + Pair(inlineItemName.trim(), amt)
                                                inlineItemName = ""
                                                inlineAmount = ""
                                            } else {
                                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সঠিক বিবরণ ও টাকা লিখুন" else "Please enter valid item description and amount", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        containerColor = if (hasInput) Color.White else Color.White.copy(alpha = 0.15f),
                                        content = {
                                            Icon(Icons.Default.Add, null, tint = if (hasInput) ForestGreen else Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isBengali) "+ আইটেম" else "+ Add item",
                                                color = if (hasInput) ForestGreen else Color.White.copy(alpha = 0.5f),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    )

                                    NeumorphicButton(
                                        onClick = {
                                            val amt = inlineAmount.toDoubleOrNull()
                                            val finalItems = pendingDueItems.toMutableList()
                                            if (inlineItemName.isNotBlank() && amt != null && amt > 0) {
                                                finalItems.add(Pair(inlineItemName.trim(), amt))
                                            }

                                            if (finalItems.isEmpty()) {
                                                Toast.makeText(context, if (isBengali) "কোনো আইটেম যোগ করা হয়নি!" else "No items to save!", Toast.LENGTH_SHORT).show()
                                                return@NeumorphicButton
                                            }

                                            var savedCount = 0
                                            finalItems.forEach { (name, amtVal) ->
                                                viewModel.addTransaction(amtVal, "GIVE", name, "CASH") {
                                                    savedCount++
                                                    if (savedCount == finalItems.size) {
                                                        inlineItemName = ""
                                                        inlineAmount = ""
                                                        pendingDueItems = emptyList()
                                                        Toast.makeText(context, if (isBengali) "হিসাব এন্ট্রি সংরক্ষণ করা হয়েছে" else "Due entry saved successfully", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        containerColor = KhataRed,
                                        content = {
                                            Text(
                                                text = if (isBengali) "সংরক্ষণ করুন" else "Save",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    )
                                }
                            } else {
                                // PAID: Item label is statically "Amount" on the left; user types amount on the right (50-50%)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = if (isBengali) "জমা হিসাব (Paid)" else "Amount",
                                        onValueChange = { },
                                        readOnly = true,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = ForestGreen,
                                            unfocusedTextColor = ForestGreen,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = Color.White,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                    )

                                    OutlinedTextField(
                                        value = inlineAmount,
                                        onValueChange = { inlineAmount = it },
                                        placeholder = {
                                            Text(
                                                text = "0",
                                                fontSize = 15.sp,
                                                color = ForestGreen.copy(alpha = 0.6f)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = ForestGreen,
                                            unfocusedTextColor = ForestGreen,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = Color.White,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                    )
                                }

                                NeumorphicButton(
                                    onClick = {
                                        val amt = inlineAmount.toDoubleOrNull()
                                        if (amt != null && amt > 0) {
                                            val desc = if (isBengali) "জমা" else "Paid"
                                            viewModel.addTransaction(amt, "GET", desc, "CASH") {
                                                inlineAmount = ""
                                                Toast.makeText(context, if (isBengali) "পেমেন্ট জমা করা হয়েছে" else "Payment received successfully", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সঠিক টাকা লিখুন" else "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    containerColor = KhataGreen,
                                    content = {
                                        Text(
                                            text = if (isBengali) "সংরক্ষণ করুন (Save)" else "Save",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Item 3: Timeline Subtitle
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "টাইমলাইন" else "TIMELINE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isBengali) "${transactions.size}টি বিবরণী" else "${transactions.size} entries",
                            fontSize = 13.sp,
                            color = NavyDark.copy(alpha = 0.6f)
                        )
                    }
                }

                // Group transactions chronologically by Day
                val sdfDay = SimpleDateFormat("dd MMM", Locale.getDefault())
                val groups = transactions.groupBy { tx ->
                    sdfDay.format(Date(tx.timestamp))
                }

                if (groups.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isBengali) "কোনো বিবরণী নেই" else "No transactions yet.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NavyDark.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                } else {
                    groups.forEach { (dateStr, txs) ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AppCaramel.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Calendar / Day Header
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("📅", fontSize = 14.sp)
                                        Text(
                                            text = dateStr,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AppCaramel
                                        )
                                    }

                                    // Display each item as capsule design rows as in screenshot 3
                                    txs.forEach { tx ->
                                        val isGet = tx.type == "GET"
                                        val rowBg = if (isGet) KhataGreenBg else AppBeige
                                        val rowTextCol = if (isGet) KhataGreen else AppCaramel

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Description Capsule (e.g. Cha, Vegetables)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1.5f)
                                                    .height(48.dp)
                                                    .background(rowBg, RoundedCornerShape(16.dp))
                                                    .border(1.dp, rowTextCol.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                                    .padding(horizontal = 12.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    if (isGet) {
                                                        Text("💵", fontSize = 12.sp)
                                                    }
                                                    Text(
                                                        text = tx.description.ifBlank {
                                                            if (isGet) (if (isBengali) "জমা" else "Paid")
                                                            else (if (isBengali) "বাকি" else "Due")
                                                        },
                                                        fontSize = 14.sp,
                                                        color = rowTextCol,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            // Amount Capsule
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .background(rowBg, RoundedCornerShape(16.dp))
                                                    .border(1.dp, rowTextCol.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                                    .padding(horizontal = 12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${if (isGet) "-" else ""}₹${String.format("%,.0f", tx.amount)}",
                                                    fontSize = 14.sp,
                                                    color = rowTextCol,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            // Revert Clear Action
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteTransaction(tx)
                                                    val alert = if (isBengali) "লেনদেন ডিলিট করা হয়েছে" else "Transaction deleted"
                                                    Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Delete",
                                                    tint = Color.LightGray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Day total
                                    val dayTotal = txs.sumOf { if (it.type == "GET") -it.amount else it.amount }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isBengali) "দিনের সংগ্রহ" else "Day total",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = NavyDark.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "${if (dayTotal < 0) "-" else ""}₹${String.format("%,.0f", if (dayTotal >= 0) dayTotal else -dayTotal)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (dayTotal < 0) KhataGreen else KhataRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Total Due Summary Row (Capsule)
                item {
                    val finalDues = customer!!.totalDues
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .border(1.dp, AppCaramel.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = AppBeige),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("💰", fontSize = 16.sp)
                                Text(
                                    text = if (isBengali) "সর্বমোট বকেয়া (Total Due)" else "Total Due",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppCaramel
                                )
                            }
                            Text(
                                text = "₹ ${String.format("%,.0f", finalDues)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppCaramel
                            )
                        }
                    }
                }
            }
        }
    }

    // QR UPI Bill dialogue
    if (showUpiQrDialog && customer != null) {
        UpiQrCodeDialog(
            viewModel = viewModel,
            upiId = ownerUser?.upiId ?: "",
            merchantName = ownerUser?.shopName ?: "",
            amount = customer!!.totalDues,
            onDismiss = { showUpiQrDialog = false }
        )
    }
}

@Composable
fun TransactionRow(
    viewModel: LedgerViewModel,
    transaction: Transaction,
    onDelete: () -> Unit
) {
    val isGive = transaction.type == "GIVE"
    val isBengali by viewModel.isBengali.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(transaction.timestamp) { dateFormat.format(Date(transaction.timestamp)) }

    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lend/Collect Indicator Arrow Box
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isGive) KhataRedBg else KhataGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGive) Icons.Default.ArrowOutward else Icons.Default.CallReceived,
                        contentDescription = null,
                        tint = if (isGive) KhataRed else KhataGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (transaction.description.isNotBlank()) {
                            transaction.description
                        } else {
                            if (isGive) {
                                if (isBengali) "বাকি এন্ট্রি" else "Due Entry"
                            } else {
                                if (isBengali) "জমা এন্ট্রি" else "Paid Entry"
                            }
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSystemInDarkTheme()) Color.White else NavyDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = formattedTime, fontSize = 11.sp, color = if (isSystemInDarkTheme()) Color.White else Color.Gray)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            val methodText = when (transaction.paymentMethod) {
                                "CASH" -> if (isBengali) "নগদ" else "CASH"
                                "UPI" -> if (isBengali) "UPI/bKash" else "UPI/MFS"
                                else -> if (isBengali) "অন্যান্য" else "OTHER"
                            }
                            Text(methodText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSystemInDarkTheme()) Color.White else Color.Gray)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (isGive) "-" else "+"} ₹ ${String.format("%,.0f", transaction.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGive) KhataRed else (if (isSystemInDarkTheme()) Color(0xFFFFD494) else KhataGreen)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Clear, "Revert Entry", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionEntryDialog(
    viewModel: LedgerViewModel,
    type: String, // GIVE or GET
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val activeType = type
    
    // Set up list state to track added items
    var itemsList by remember { mutableStateOf(listOf<Pair<String, Double>>()) }
    
    var currentItemName by remember { mutableStateOf("") }
    var currentItemAmount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("CASH") } // CASH, UPI, OTHER

    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismiss() }) {
        NeumorphicCard(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isBengali) "নতুন এন্ট্রি যোগ করুন" else "Add Entry Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) Color.White else NavyDark
                )

                // 1. Full-width indicator representing the fixed selected type
                val isDue = activeType == "GIVE"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isDue) KhataRed else KhataGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isDue) Icons.Default.Add else Icons.Default.Remove,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDue) {
                                if (isBengali) "বাকি এন্ট্রি (Due)" else "Due Entry"
                            } else {
                                if (isBengali) "জমা এন্ট্রি (Paid)" else "Paid Entry"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // 2. Input item and amount details (Symmetrical, equal weight & height)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentItemName,
                        onValueChange = { currentItemName = it },
                        placeholder = { 
                            Text(
                                text = if (isBengali) "আইটেমের নাম" else "Item name",
                                fontSize = 13.sp,
                                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = if (isSystemInDarkTheme()) Color.White else ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isSystemInDarkTheme()) Color.White else NavyDark,
                            unfocusedTextColor = if (isSystemInDarkTheme()) Color.White else NavyDark,
                            focusedContainerColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.12f) else Color.White,
                            unfocusedContainerColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.08f) else WarmBg.copy(alpha = 0.6f),
                            focusedBorderColor = if (isSystemInDarkTheme()) Color.White else ForestGreen,
                            unfocusedBorderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.4f),
                            focusedPlaceholderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )

                    OutlinedTextField(
                        value = currentItemAmount,
                        onValueChange = { currentItemAmount = it },
                        placeholder = { 
                            Text(
                                text = if (isBengali) "টাকার পরিমাণ" else "Amount",
                                fontSize = 13.sp,
                                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.6f)
                            ) 
                        },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                color = if (isSystemInDarkTheme()) Color.White else ForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isSystemInDarkTheme()) Color.White else NavyDark,
                            unfocusedTextColor = if (isSystemInDarkTheme()) Color.White else NavyDark,
                            focusedContainerColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.12f) else Color.White,
                            unfocusedContainerColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.08f) else WarmBg.copy(alpha = 0.6f),
                            focusedBorderColor = if (isSystemInDarkTheme()) Color.White else ForestGreen,
                            unfocusedBorderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.4f),
                            focusedPlaceholderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                }

                // 3. Add Item helper button
                NeumorphicButton(
                    onClick = {
                        val amtVal = currentItemAmount.toDoubleOrNull()
                        if (currentItemName.isBlank()) {
                            val errMsg = if (isBengali) "আইটেমের নাম ফাঁকা রাখা যাবে না!" else "Write item name first!"
                            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                        } else if (amtVal == null || amtVal <= 0) {
                            val errMsg = if (isBengali) "সঠিক টাকার পরিমাণ টাইপ করুন!" else "Please type a valid price!"
                            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                        } else {
                            itemsList = itemsList + Pair(currentItemName.trim(), amtVal)
                            currentItemName = ""
                            currentItemAmount = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    containerColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = if (isSystemInDarkTheme()) Color.White else NavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "+ আইটেম যোগ করুন" else "+ Add item",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isSystemInDarkTheme()) Color.White else NavyDark
                    )
                }

                // 4. Render scrolling list of added items
                if (itemsList.isNotEmpty()) {
                    Text(
                        text = if (isBengali) "যোগকৃত তালিকা:" else "Added list:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Gray
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 120.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsList.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.08f) else Color.White)
                                    .border(1.dp, if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = if (activeType == "GIVE") KhataRed else KhataGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.first,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSystemInDarkTheme()) Color.White else NavyDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹ ${String.format("%,.0f", item.second)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSystemInDarkTheme()) Color.White else NavyDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            itemsList = itemsList.filterIndexed { idx, _ -> idx != index }
                                        },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete item",
                                            tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Display running total
                    val runningTotal = itemsList.sumOf { it.second }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSystemInDarkTheme()) {
                                    if (activeType == "GIVE") KhataRed.copy(alpha = 0.15f) else KhataGreen.copy(alpha = 0.15f)
                                } else {
                                    if (activeType == "GIVE") KhataRedBg else KhataGreenBg
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "মোট পরিমাণ" else "Total Invoice",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else (if (activeType == "GIVE") KhataRed else KhataGreen)
                        )
                        Text(
                            text = "₹ ${String.format("%,.0f", runningTotal)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSystemInDarkTheme()) Color.White else (if (activeType == "GIVE") KhataRed else KhataGreen)
                        )
                    }
                }

                // 5. Payment Channel selector
                Column {
                    Text(
                        text = if (isBengali) "লেনদেনের মাধ্যম" else "Payment Channel",
                        fontSize = 12.sp,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = if (isBengali) {
                            listOf("CASH" to "নগদ (Cash)", "UPI" to "UPI/bKash", "OTHER" to "অন্যান্য")
                        } else {
                            listOf("CASH" to "Cash", "UPI" to "UPI/bKash", "OTHER" to "Other")
                        }
                        modes.forEach { (key, label) ->
                            val selected = paymentMethod == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) {
                                            if (isSystemInDarkTheme()) ForestGreen.copy(alpha = 0.25f) else ForestGreen.copy(alpha = 0.1f)
                                        } else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) {
                                            if (isSystemInDarkTheme()) Color.White else ForestGreen
                                        } else {
                                            if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.2f) else Color.LightGray
                                        },
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { paymentMethod = key }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSystemInDarkTheme()) Color.White else (if (selected) ForestGreen else Color.Gray)
                                )
                            }
                        }
                    }
                }

                // 6. Dialogue Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(if (isBengali) "বাতিল" else "Cancel", color = if (isSystemInDarkTheme()) Color.White else Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    NeumorphicButton(
                        onClick = {
                            var finalItems = itemsList
                            val currentAmtDouble = currentItemAmount.toDoubleOrNull()
                            
                            // UX safety net: if user typed something in inputs but forgot to click "+ Add Item", capture it!
                            if (currentItemName.isNotBlank() && currentAmtDouble != null && currentAmtDouble > 0) {
                                finalItems = finalItems + Pair(currentItemName.trim(), currentAmtDouble)
                            }

                            val finalSum = finalItems.sumOf { it.second }
                            if (finalSum <= 0) {
                                val err = if (isBengali) "কমপক্ষে একটি পেমেন্ট আইটেম এন্ট্রি দিন" else "Please list at least one valid item amount"
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            } else {
                                val finalDescStr = finalItems.joinToString(", ") { "${it.first} (₹${String.format("%.0f", it.second)})" }
                                onConfirm(finalSum, finalDescStr, paymentMethod)
                            }
                        },
                        containerColor = if (activeType == "GIVE") KhataRed else KhataGreen,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (isBengali) "সংরক্ষণ করুন" else "Save", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun UpiQrCodeDialog(
    viewModel: LedgerViewModel,
    upiId: String,
    merchantName: String,
    amount: Double,
    onDismiss: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    Dialog(onDismissRequest = { onDismiss() }) {
        NeumorphicCard(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isBengali) "UPI পেমেন্ট QR কোড" else "UPI Instant Payment QR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )

                Text(
                    text = if (isBengali) "কাস্টমারকে স্ক্রিন স্ক্যান করতে বলুন:" else "Ask Customer to Scan QR directly:",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                // Stenciled mockup QR Canvas representation
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(Color.White)
                        .border(3.dp, ForestGreen, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(100.dp), tint = NavyDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("SCAN TO PAY INR", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = merchantName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Text(text = "UPI / MFS: $upiId", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBengali) "পরিশোধের পরিমাণ ₹ ${String.format("%,.0f", amount)}" else "Collection Arrears: ₹ ${String.format("%,.0f", amount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KhataRed
                    )
                }

                NeumorphicButton(
                    onClick = { onDismiss() },
                    containerColor = ForestGreen,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBengali) "সম্পন্ন" else "Done", color = Color.White)
                }
            }
        }
    }
}

data class ChartBarData(val label: String, val giveVal: Float, val getVal: Float)

@Composable
fun ReportsScreen(viewModel: LedgerViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    var filterType by remember { mutableStateOf("ALL") } // ALL, CASH, UPI
    var timeFilter by remember { mutableStateOf("ALL") } // ALL, DAILY, WEEKLY, MONTHLY

    val now = remember { System.currentTimeMillis() }
    val filteredByTime = remember(transactions, timeFilter) {
        val calendarNow = Calendar.getInstance()
        transactions.filter { tx ->
            when (timeFilter) {
                "DAILY" -> {
                    val calTx = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                    calendarNow.get(Calendar.YEAR) == calTx.get(Calendar.YEAR) &&
                    calendarNow.get(Calendar.DAY_OF_YEAR) == calTx.get(Calendar.DAY_OF_YEAR)
                }
                "WEEKLY" -> {
                    val diff = now - tx.timestamp
                    diff <= 7L * 24 * 60 * 60 * 1000
                }
                "MONTHLY" -> {
                    val diff = now - tx.timestamp
                    diff <= 30L * 24 * 60 * 60 * 1000
                }
                else -> true
            }
        }
    }

    val finalFilteredList = remember(filteredByTime, filterType) {
        if (filterType == "ALL") filteredByTime else filteredByTime.filter { it.paymentMethod == filterType }
    }

    // Sum credit vs sum deposits
    val (creditTotal, debitTotal) = remember(finalFilteredList) {
        var creditSum = 0.0
        var debitSum = 0.0
        finalFilteredList.forEach {
            if (it.type == "GIVE") creditSum += it.amount else debitSum += it.amount
        }
        Pair(creditSum, debitSum)
    }

    // Grouping the time-filtered transactions by date (truncated to day)
    val chartData = remember(filteredByTime) {
        val dayFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val grouped = filteredByTime.groupBy { tx ->
            dayFormat.format(Date(tx.timestamp))
        }
        grouped.entries.sortedBy { it.value.first().timestamp }
            .takeLast(5)
            .map { entry ->
                val dayLabel = entry.key
                var giveSum = 0.0f
                var getSum = 0.0f
                entry.value.forEach {
                    if (it.type == "GIVE") giveSum += it.amount.toFloat() else getSum += it.amount.toFloat()
                }
                ChartBarData(dayLabel, giveSum, getSum)
            }
    }

    Scaffold(
        containerColor = WarmBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // High-Fidelity Curved Emerald Header (Matches Dashboard design theme)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForestGreen,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = if (isBengali) "ব্যবসায়িক অ্যানালাইসিস" else "Business Intelligence",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBengali) "দোকানের সর্বমোট লেনদেন এবং ক্যাশ আদায়ের গতিধারা" else "Gain powerful insights into your credits & collections balance",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Item 1: Time filter range selection (Daily, Weekly, Monthly, All)
                item {
                    TimeFilterTabsRow(
                        isBengali = isBengali,
                        selectedFilter = timeFilter,
                        onSelectFilter = { timeFilter = it }
                    )
                }

                // Item 2: Cash/UPI Segment Switch Selector
                item {
                    PaymentMethodFilterRow(
                        isBengali = isBengali,
                        selectedMethod = filterType,
                        onSelectMethod = { filterType = it }
                    )
                }

                // Item 3: Total Credit Given vs Total Cash Collected Metrics
                item {
                    AuditMetricsBlock(
                        isBengali = isBengali,
                        creditTotal = creditTotal,
                        debitTotal = debitTotal
                    )
                }

                // Item 4: Target & Collection Progress Circle (Gauge Card)
                item {
                    CollectionGoalGaugeCard(
                        isBengali = isBengali,
                        creditTotal = creditTotal,
                        debitTotal = debitTotal
                    )
                }

                // Item 5: Custom Canvas Bar Chart for ledger trends
                item {
                    LedgerTrendChartCard(
                        isBengali = isBengali,
                        chartData = chartData
                    )
                }

                // Item 6: Section Header for listing list
                item {
                    Text(
                        text = if (isBengali) "লেনদেনের তালিকা (${finalFilteredList.size}টি)" else "Transactions Listing (${finalFilteredList.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                    )
                }

                // Items: Listing
                if (finalFilteredList.isEmpty()) {
                    item {
                        EmptyStateBlock(isBengali = isBengali)
                    }
                } else {
                    items(finalFilteredList) { tx ->
                        TransactionItemRow(isBengali = isBengali, tx = tx)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeFilterTabsRow(
    isBengali: Boolean,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf(
        "DAILY" to (if (isBengali) "আজ" else "Daily"),
        "WEEKLY" to (if (isBengali) "সветаহ" else "Weekly"), // corrected translated text for better aesthetic representation
        "MONTHLY" to (if (isBengali) "মাস" else "Monthly"),
        "ALL" to (if (isBengali) "সব সময়" else "All Time")
    )
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ForestGreen.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            filters.forEach { (key, label) ->
                val isSelected = selectedFilter == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ForestGreen else Color.Transparent)
                        .clickable { onSelectFilter(key) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else NavyDark.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodFilterRow(
    isBengali: Boolean,
    selectedMethod: String,
    onSelectMethod: (String) -> Unit
) {
    val filters = if (isBengali) {
        listOf("ALL" to "সব লেনদেন", "CASH" to "নগদ", "UPI" to "UPI/bKash")
    } else {
        listOf("ALL" to "All Txns", "CASH" to "Cash Only", "UPI" to "UPI/bKash")
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (key, label) ->
            val isSelected = selectedMethod == key
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ForestGreen.copy(alpha = 0.12f) else Color.White)
                    .border(
                        1.dp,
                        if (isSelected) ForestGreen else Color.LightGray.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectMethod(key) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) ForestGreen else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AuditMetricsBlock(
    isBengali: Boolean,
    creditTotal: Double,
    debitTotal: Double
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ForestGreen,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFFFCDD2), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "বাকি দিয়েছেন" else "Credit Given", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₹ ${String.format("%,.0f", creditTotal)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFF8A80)
                )
            }

            Box(
                modifier = Modifier
                    .height(36.dp)
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFFC8E6C9), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "ক্যাশ উঠেছে" else "Cash Received", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₹ ${String.format("%,.0f", debitTotal)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF81C784)
                )
            }
        }
    }
}

@Composable
fun CollectionGoalGaugeCard(
    isBengali: Boolean,
    creditTotal: Double,
    debitTotal: Double
) {
    val totalVolume = creditTotal + debitTotal
    val collectionRate = if (totalVolume > 0) {
        (debitTotal / totalVolume * 100).toInt()
    } else {
        0
    }

    val healthMsg = when {
        collectionRate >= 80 -> if (isBengali) "চমৎকার ক্যাশ আদায়, সুস্থ্য ব্যবসা!" else "Excellent collection health!"
        collectionRate >= 50 -> if (isBengali) "মোটামুটি আদায়ে ব্যালেন্স রয়েছে" else "Healthy balanced cashflow."
        collectionRate > 0 -> if (isBengali) "বাকির পরিমাণ বেশি, দ্রুত আদায় করুন!" else "High Outstanding dues. Immediate collection needed."
        else -> if (isBengali) "কোনো আদান-প্রদান শুরু হয়নি" else "No outstanding collections records found."
    }

    val dueText = if (isBengali) {
        "বাকি পাওনা: ₹ ${String.format("%,.0f", maxOf(0.0, creditTotal - debitTotal))}"
    } else {
        "Pending Dues: ₹ ${String.format("%,.0f", maxOf(0.0, creditTotal - debitTotal))}"
    }

    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ForestGreen,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = if (isBengali) "লেনদেন আদায় অগ্রগতি" else "Ledger Collection Progress",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Part: Beautiful Circular Progress Indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { collectionRate.toFloat() / 100f },
                        modifier = Modifier.size(72.dp),
                        color = Color.White,
                        strokeWidth = 8.dp,
                        trackColor = Color.White.copy(alpha = 0.15f),
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$collectionRate%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right Part: Detailed dynamic analytics description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = healthMsg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dueText,
                        fontSize = 12.sp,
                        color = Color(0xFFFFCDD2),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBengali) "মোট লেনদেনের সাপেক্ষে আদায়ের হার" else "Ratio of total collections",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
fun LedgerTrendChartCard(
    isBengali: Boolean,
    chartData: List<ChartBarData>
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ForestGreen,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "ট্রেণ্ড অ্যানালাইসিস (Trend)" else "Weekly & Daily Trend",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Give Legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFFF8A80))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "বাকি" else "Give",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Get Legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF81C784))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "আদায়" else "Got",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (chartData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "গ্রাফ প্রদর্শনের জন্য পর্যাপ্ত ডেটা নেই" else "Not enough ledger records for graph projection",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            } else {
                // Let's calculate nice scaling for Canvas
                val maxVal = remember(chartData) {
                    chartData.flatMap { listOf(it.giveVal, it.getVal) }.maxOrNull()?.let { if (it > 0) it else 1f } ?: 1f
                }

                // Custom Jetpack Compose Canvas Row of bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartData.forEach { data ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.width(48.dp)
                        ) {
                            // Two slim side-by-side vertical progress bars
                            Row(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Draw Give Bar (Red)
                                val giveHeightShare = (data.giveVal / maxVal).coerceIn(0.01f, 1f)
                                Box(
                                    modifier = Modifier
                                        .width(7.dp)
                                        .fillMaxHeight(giveHeightShare)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(Color(0xFFFF8A80))
                                )

                                // Draw Get Bar (Green)
                                val getHeightShare = (data.getVal / maxVal).coerceIn(0.01f, 1f)
                                Box(
                                    modifier = Modifier
                                        .width(7.dp)
                                        .fillMaxHeight(getHeightShare)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(Color(0xFF81C784))
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Day label
                            Text(
                                text = data.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateBlock(isBengali: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Analytics,
                contentDescription = null,
                tint = ForestGreen.copy(alpha = 0.25f),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isBengali) "কোনো লেনদেনের হিসাব পাওয়া যায়নি!" else "No transactions found!",
                fontSize = 14.sp,
                color = NavyDark.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isBengali) "অন্য আরেকটি ক্যাটাগরি বা সময় নির্বাচন করে চেষ্টা করুন" else "Try filtering a different window range or payment type",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun TransactionItemRow(isBengali: Boolean, tx: Transaction) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ForestGreen,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
            val txDate = remember(tx.timestamp) { dateFormat.format(Date(tx.timestamp)) }
            val isGive = tx.type == "GIVE"

            Column {
                Text(
                    text = if (tx.description.isNotBlank()) {
                        tx.description
                    } else {
                        if (isGive) {
                            if (isBengali) "বাকি বিক্রি" else "Sales on Credit"
                        } else {
                            if (isBengali) "টাকা আদায়" else "Payment Received"
                        }
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                val methodText = when (tx.paymentMethod) {
                    "CASH" -> if (isBengali) "নগদ" else "CASH"
                    "UPI" -> if (isBengali) "UPI/bKash" else "UPI/MFS"
                    else -> if (isBengali) "অন্যান্য" else "OTHER"
                }
                Text("$txDate • $methodText", fontSize = 11.sp, color = Color.White.copy(alpha = 0.82f))
            }

            Text(
                text = if (isBengali) {
                    "${if (isGive) "বাকি " else "জমা "} ₹ ${String.format("%,.0f", tx.amount)}"
                } else {
                    "${if (isGive) "DUE " else "PAID "} ₹ ${String.format("%,.0f", tx.amount)}"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isGive) Color(0xFFFF8A80) else Color(0xFF81C784)
            )
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: LedgerViewModel,
    onBack: () -> Unit = {}
) {
    val ownerUser by viewModel.authenticatedUser.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    val context = LocalContext.current

    if (ownerUser == null) return

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = WarmBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
        ) {
            // 1. Unified Placement-Style Header Row (Matches screenshot title & back button spacing)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), CircleShape)
                            .clickable { onBack() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = if (isBengali) "প্রোফাইল সেটিংস" else "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Profile Summary Card (Matches screenshot layout)
                val firstLetter = ownerUser?.name?.firstOrNull()?.uppercase() ?: "S"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { showEditProfileDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Styled circle avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = firstLetter,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBengali) "হাই, ${ownerUser?.name}" else "Hi, ${ownerUser?.name}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ownerUser?.shopName ?: "",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 3. Supabase Cloud Sync Card (Matches middle promotional card placement style)
                val isSupabaseActive = viewModel.isSupabaseActive()
                val syncState by viewModel.syncState.collectAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1.2f).padding(end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isBengali) "সুপাবেস ক্লাউড সিঙ্ক" else "Supabase Cloud Sync",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = if (isSupabaseActive) {
                                    when (syncState) {
                                        LedgerViewModel.SyncState.SYNCING -> if (isBengali) "ক্লাউড ডেটা সিঙ্ক হচ্ছে..." else "Syncing database with Supabase..."
                                        LedgerViewModel.SyncState.SUCCESS -> if (isBengali) "সাফল্যজনকভাবে ক্লাউডে সিঙ্কড!" else "Synced securely with Supabase!"
                                        LedgerViewModel.SyncState.ERROR -> if (isBengali) "সংযোগ ত্রুটি! অফলাইনে সংরক্ষিত।" else "Sync failed. Local backup ok."
                                        else -> if (isBengali) "আপনার হিসাব সুরক্ষিতভাবে ক্লাউডে সংরক্ষিত।" else "Ledger securely backed up in the cloud."
                                    }
                                } else {
                                    if (isBengali) "আপনার খাতা ক্লাউডে ব্যাকআপ রাখতে সিক্রেটস সেট করুন।" else "Configure Supabase values in Secrets to activate backup."
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 18.sp
                            )

                            if (isSupabaseActive) {
                                Button(
                                    onClick = { viewModel.triggerSync() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onSurface,
                                        contentColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(50.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Sync",
                                        tint = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBengali) "এখনই সিঙ্ক করুন" else "Sync Now",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }

                        // Sync icon / cloud visual elements
                        Box(
                            modifier = Modifier
                                .weight(0.8f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSupabaseActive) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Section Label: Other Settings
                Text(
                    text = if (isBengali) "অন্যান্য সেটিংস" else "Other Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

                // 5. Unified Option Settings Card (Matches screenshot grouped items wrapper)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        SettingsListItem(
                            icon = Icons.Default.Person,
                            title = if (isBengali) "আমার প্রোফাইল" else "My Account",
                            onItemClick = { showEditProfileDialog = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        SettingsListItem(
                            icon = Icons.Default.Language,
                            title = if (isBengali) "ভাষা পরিবর্তন (Language)" else "App Language",
                            trailingContent = {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ForestGreen.copy(alpha = 0.08f))
                                        .clickable { viewModel.setLanguage(!isBengali) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBengali) "বাংলা" else "English",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch",
                                        tint = ForestGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        // 3. Dynamic Notifications enabled switch
                        val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
                        SettingsListItem(
                            icon = Icons.Default.Notifications,
                            title = if (isBengali) "বার্তা নোটিফিকেশন" else "Notifications",
                            trailingContent = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ForestGreen,
                                        checkedTrackColor = ForestGreen.copy(alpha = 0.3f),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        if (isSupabaseActive) {
                            SettingsListItem(
                                icon = Icons.Default.CloudDownload,
                                title = if (isBengali) "ক্লাউড থেকে রিস্টোর" else "Restore from Cloud",
                                onItemClick = { showRestoreDialog = true }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = Color.LightGray.copy(alpha = 0.3f),
                                thickness = 1.dp
                            )
                        }

                        SettingsListItem(
                            icon = Icons.Default.ExitToApp,
                            title = if (isBengali) "নিরাপদ লক আউট" else "Secure Lockout",
                            titleColor = Color.Red,
                            onItemClick = {
                                viewModel.logout()
                                val logoutAlert = if (isBengali) "নিরাপদ লগ-আউট সম্পন্ন!" else "Secured exit complete!"
                                Toast.makeText(context, logoutAlert, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Show edit profile overlay modal/dialog
        if (showEditProfileDialog && ownerUser != null) {
            EditProfileDialog(
                ownerUser = ownerUser!!,
                viewModel = viewModel,
                isBengali = isBengali,
                onDismiss = { showEditProfileDialog = false }
            )
        }

        // Show cloud restoration dialog
        if (showRestoreDialog) {
            RestoreFromCloudDialog(
                viewModel = viewModel,
                onDismiss = { showRestoreDialog = false }
            )
        }
    }
}

@Composable
fun SettingsListItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onItemClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onItemClick != null) Modifier.clickable { onItemClick() } else Modifier)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon in custom small circle background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = titleColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    ownerUser: User,
    viewModel: LedgerViewModel,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(ownerUser.name) }
    var shopName by remember { mutableStateOf(ownerUser.shopName) }
    var shopType by remember { mutableStateOf(ownerUser.shopType) }
    var upiId by remember { mutableStateOf(ownerUser.upiId) }
    var pin by remember { mutableStateOf(ownerUser.pin) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBengali) "প্রোফাইল সংশোধন" else "Edit Profile Information",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ForestGreen
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "মালিকের নাম" else "Owner Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text(if (isBengali) "দোকানের নাম" else "Shop Name") },
                    leadingIcon = { Icon(Icons.Default.Store, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = shopType,
                    onValueChange = { shopType = it },
                    label = { Text(if (isBengali) "ব্যবসার ধরন" else "Business Type") },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text(if (isBengali) "পেমেন্ট UPI আইডি" else "Payment UPI ID") },
                    leadingIcon = { Icon(Icons.Default.QrCode, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
                    label = { Text(if (isBengali) "পিন লক পাসওয়ার্ড" else "Security PIN Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = ForestGreen) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || shopName.isBlank() || pin.length != 4) {
                        val alert = if (isBengali) "অনুগ্রহ করে সব তথ্য সঠিক দিন" else "Please correctly fill all information"
                        Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateProfile(name, shopName, shopType, upiId, pin)
                        val successAlert = if (isBengali) "প্রোফাইল পরিবর্তন সফল হয়েছে!" else "Profile details updated!"
                        Toast.makeText(context, successAlert, Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text(if (isBengali) "সংরক্ষণ" else "Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun RestoreFromCloudDialog(
    viewModel: LedgerViewModel,
    onDismiss: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var statusMsg by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBengali) "ক্লাউড থেকে খাতা রিস্টোর করুন" else "Restore Ledger from Cloud",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ForestGreen
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = if (isBengali) "আপনার নিবন্ধিত ইমেইল আইডি এবং ৪-ডিজিটের পিন কোড দিন।" else "Provide your registered email and 4-digit PIN.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
                    label = { Text(if (isBengali) "৪-ডিজিট পিন" else "4-Digit PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        focusedLabelColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (statusMsg.isNotEmpty() || syncState == LedgerViewModel.SyncState.SYNCING) {
                    val displayMsg = if (syncState == LedgerViewModel.SyncState.SYNCING) {
                        if (isBengali) "খাতা ডাউনলোড হচ্ছে, অনুগ্রহ করে অপেক্ষা করুন..." else "Downloading ledger, please wait..."
                    } else {
                        statusMsg
                    }
                    Text(
                        text = displayMsg,
                        color = if (syncState == LedgerViewModel.SyncState.ERROR) Color.Red else ForestGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isBlank() || pin.length < 4) {
                        statusMsg = if (isBengali) "সঠিক ইমেইল এবং ৪ অঙ্কের পিন লিখুন।" else "Enter a valid email and 4-digit PIN."
                        return@Button
                    }
                    viewModel.signInWithSupabaseEmail(email, pin) { success, msg ->
                        statusMsg = msg
                        if (success) {
                            Toast.makeText(context, if (isBengali) "সাফল্যজনকভাবে খাতা রিস্টোর হয়েছে!" else "Ledger successfully restored!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text(if (isBengali) "রিস্টোর করুন" else "Restore", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel", color = Color.Gray)
            }
        }
    )
}

