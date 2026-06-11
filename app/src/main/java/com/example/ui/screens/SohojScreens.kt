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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontFamily

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    isDark: Boolean = isSystemInDarkTheme(),
    containerColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val containerBg = containerColor ?: MaterialTheme.colorScheme.surface
    val borderCol = ForestGreen.copy(alpha = 0.12f)
    val shadowElev = 2.dp
    
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
                            fontFamily = FontFamily.Serif,
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹ ${String.format("%,.0f", totalWeGet)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Serif,
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹ ${String.format("%,.0f", totalWeGive)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Serif,
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
                        Text("₹ ${String.format("%,.0f", dues)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = Color(0xFFFF8A80))
                    }
                    dues < 0 -> {
                        Text(if (isBengali) "দেবেন" else "Give", fontSize = 11.sp, color = Color(0xFFC8E6C9), fontWeight = FontWeight.Bold)
                        Text("₹ ${String.format("%,.0f", -dues)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = Color(0xFF81C784))
                    }
                    else -> {
                        Text(if (isBengali) "সমতা" else "Settled", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                        Text(if (isBengali) "₹ ০" else "₹ 0", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = Color.White)
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
            containerColor = Color(0xFF222222),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isBengali) "নতুন কাস্টমার যোগ করুন" else "Add New Customer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "কাস্টমারের নাম" else "Customer Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1C1C1C),
                        unfocusedTextColor = Color(0xFF1C1C1C),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = ForestGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = ForestGreen,
                        unfocusedLeadingIconColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBengali) "মোবাইল নম্বর" else "Mobile Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = ForestGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1C1C1C),
                        unfocusedTextColor = Color(0xFF1C1C1C),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = ForestGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = ForestGreen,
                        unfocusedLeadingIconColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(if (isBengali) "বাতিল" else "Cancel", color = Color.LightGray, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) {
                                val alert = if (isBengali) "নাম ও মোবাইল দেয়া আবশ্যক" else "Name & Mobile are required"
                                Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                            } else {
                                onAdd(name, phone, null, false)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(if (isBengali) "যোগ করুন" else "Add", color = Color.White, fontWeight = FontWeight.Bold)
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
                                                text = if (isBengali) "আইটেম" else "Add item",
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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                            color = AppCaramel.copy(alpha = 0.6f)
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
                            .border(1.dp, Color(0xFF333333).copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "₹ ${String.format("%,.0f", finalDues)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                        text = if (isBengali) "আইটেম যোগ করুন" else "Add item",
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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var editableUpiId by remember { mutableStateOf(upiId) }
    var editableName by remember { mutableStateOf(merchantName) }
    var editableAmount by remember { mutableStateOf(if (amount > 0) String.format(Locale.US, "%.0f", amount) else "") }
    var editableNote by remember { mutableStateOf(if (isBengali) "বকেয়া বিল পরিশোধ" else "Dues payment") }
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            kotlinx.coroutines.delay(2000)
            isCopied = false
        }
    }

    val computedUriString = remember(editableUpiId, editableName, editableAmount, editableNote) {
        val amtVal = editableAmount.toDoubleOrNull()
        val amtPart = if (amtVal != null && amtVal > 0) String.format(Locale.US, "%.2f", amtVal) else ""
        
        "upi://pay?pa=${editableUpiId.trim()}" +
                (if (editableName.isNotBlank()) "&pn=${Uri.encode(editableName.trim())}" else "") +
                (if (amtPart.isNotEmpty()) "&am=$amtPart" else "") +
                "&cu=INR" +
                (if (editableNote.isNotBlank()) "&tn=${Uri.encode(editableNote.trim())}" else "")
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        NeumorphicCard(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isBengali) "UPI পেমেন্ট লিংক ও QR জেনারেটর" else "UPI Link & QR Generator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isBengali) {
                        "ডিজিটাল লিংক বা QR কোড তৈরি করে কাস্টমারের সাথে শেয়ার করুন"
                    } else {
                        "Create real-time custom UPI links or QR to share instantly"
                    },
                    fontSize = 12.sp,
                    color = AppCaramel.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Input fields section
                OutlinedTextField(
                    value = editableUpiId,
                    onValueChange = { editableUpiId = it },
                    label = { Text(if (isBengali) "UPI VPA আইডি" else "Merchant UPI VPA") },
                    placeholder = { Text("e.g. shopowner@upi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = AppCaramel.copy(alpha = 0.4f),
                        focusedLabelColor = ForestGreen,
                        unfocusedLabelColor = AppCaramel.copy(alpha = 0.7f),
                        focusedTextColor = AppCaramel,
                        unfocusedTextColor = AppCaramel
                    )
                )

                OutlinedTextField(
                    value = editableName,
                    onValueChange = { editableName = it },
                    label = { Text(if (isBengali) "গ্রহীতার নাম" else "Payee / Merchant Name") },
                    placeholder = { Text("e.g. Laxmi Grocery") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = AppCaramel.copy(alpha = 0.4f),
                        focusedLabelColor = ForestGreen,
                        unfocusedLabelColor = AppCaramel.copy(alpha = 0.7f),
                        focusedTextColor = AppCaramel,
                        unfocusedTextColor = AppCaramel
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editableAmount,
                        onValueChange = { editableAmount = it },
                        label = { Text(if (isBengali) "পরিমাণ (₹)" else "Amount (₹)") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = AppCaramel.copy(alpha = 0.4f),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = AppCaramel.copy(alpha = 0.7f),
                            focusedTextColor = AppCaramel,
                            unfocusedTextColor = AppCaramel
                        )
                    )

                    OutlinedTextField(
                        value = editableNote,
                        onValueChange = { editableNote = it },
                        label = { Text(if (isBengali) "নোট / বিবরণ" else "Payment Note") },
                        placeholder = { Text("e.g. Invoice #1") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = AppCaramel.copy(alpha = 0.4f),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = AppCaramel.copy(alpha = 0.7f),
                            focusedTextColor = AppCaramel,
                            unfocusedTextColor = AppCaramel
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Generated String Block
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = (if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isBengali) "জেনারেট করা UPI লিংক (UPI String):" else "Generated UPI Pay Intent String:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        SelectionContainer {
                            Text(
                                text = computedUriString,
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = if (isSystemInDarkTheme()) Color(0xFFE0E0E0) else Color(0xFF333333),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Sharing Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy button
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(computedUriString))
                            isCopied = true
                            Toast.makeText(
                                context,
                                if (isBengali) "লিংক ক্লিপবোর্ডে কপি হয়েছে!" else "UPI intent link copied to clipboard!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) KhataGreen else ForestGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCopied) (if (isBengali) "কপি হয়েছে" else "Copied") else (if (isBengali) "কপি করুন" else "Copy Link"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Share button
                    Button(
                        onClick = {
                            try {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, computedUriString)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, if (isBengali) "লিংক শেয়ার করুন" else "Share Payment Link"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error sharing link", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "শেয়ার করুন" else "Share Link",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // WhatsApp Sharing Card
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        try {
                            val msg = if (isBengali) {
                                "প্রিয় কাস্টমার, আপনার পরিশোধযোগ্য ₹ $editableAmount বকেয়া বিল এই লিংকের মাধ্যমে পরিশোধ করুন:\n$computedUriString\nবিনীত, $editableName - সহজ খাতা (SohojKhata)"
                            } else {
                                "Dear customer, please pay your due billing amount of ₹ $editableAmount using this UPI instant pay link:\n$computedUriString\nRegards, $editableName - ShohojKhata"
                            }
                            val uri = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(msg)}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, if (isBengali) "হোয়াটসঅ্যাপ পাওয়া যায়নি অথবা সমস্যা হচ্ছে" else "WhatsApp request failed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF25D366), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SendToMobile,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBengali) "হোয়াটসঅ্যাপে কাস্টমারকে রিকোয়েস্ট পাঠান" else "Request Customer via WhatsApp",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = if (isBengali) "সরাসরি রেডিমেড টেক্সট মেসেজ ও পেমেন্ট লিংক শেয়ার করুন" else "Instantly share prefilled message & link details",
                                fontSize = 9.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Beautiful interactive QR Display container
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(3.dp, ForestGreen, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (editableAmount.isNotBlank() && editableAmount.toDoubleOrNull() != null) {
                                "PAY ₹${editableAmount}"
                            } else {
                                "SCAN TO PAY"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                Text(
                    text = if (isBengali) {
                        "নোট: কাস্টমার যেকোনো UPI অ্যাপ (GPay, PhonePe, Paytm, bKash) দিয়ে স্ক্যান করে টাকা পাঠাতে পারবেন"
                    } else {
                        "Info: Scanning this QR or clicking the link opens any dynamic UPI app (GPay, PhonePe, Paytm, BHIM) on customer's phone"
                    },
                    fontSize = 9.sp,
                    color = AppCaramel.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                NeumorphicButton(
                    onClick = { onDismiss() },
                    containerColor = ForestGreen,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(if (isBengali) "সম্পন্ন" else "Done", color = Color.White, fontWeight = FontWeight.Bold)
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

    val finalFilteredList = filteredByTime

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
                        text = if (isBengali) "দেনার মোট হিসাব ও আদায়ের গতিধারা" else "Gain insights into your credit & collection",
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

                // Payment Method filter removed as requested

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
        collectionRate >= 80 -> if (isBengali) "ক্যাশ আদায় চমৎকার!" else "Excellent cash collection."
        collectionRate >= 50 -> if (isBengali) "ক্যাশ আদায় সন্তোষজনক।" else "Healthy balanced cashflow."
        collectionRate > 0 -> if (isBengali) "তাড়াতাড়ি বাকি আদায় করুন।" else "Collect dues soon."
        else -> if (isBengali) "কোনো আদান-প্রদান শুরু হয়নি।" else "No transactions found."
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
                        text = if (isBengali) "মোট আদায়ের হার" else "Cash collection rate",
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
                    text = if (isBengali) "লেনদেন ট্রেন্ড" else "Ledger Trend",
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
                        text = if (isBengali) "পর্যাপ্ত ডেটা নেই" else "Not enough data",
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
                Text(txDate, fontSize = 11.sp, color = Color.White.copy(alpha = 0.82f))
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
    val googleName by viewModel.googleName.collectAsState()
    val context = LocalContext.current

    val displayName = ownerUser?.name ?: googleName ?: "Faizen Ahmed"
    val isSupabaseActive = viewModel.isSupabaseActive()
    val isPremiumMerchant by viewModel.isPremiumMerchant.collectAsState()

    var showEditUserSettingsDialog by remember { mutableStateOf(false) }
    var showEditShopSettingsDialog by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }

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
                val firstLetter = displayName.firstOrNull()?.uppercase() ?: "S"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .let { 
                            if (ownerUser != null) {
                                it.clickable { showEditUserSettingsDialog = true }
                            } else {
                                it
                            }
                        },
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
                                text = if (isBengali) "হাই, $displayName" else "Hi, $displayName",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ownerUser?.shopName ?: (if (isBengali) "কাস্টমার অ্যাকাউন্ট" else "Customer Connected Account"),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        if (ownerUser != null) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // 3. Brand-aligned High Impact Premium Storefront Paywall / Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPremiumMerchant) ForestGreen else MaterialTheme.colorScheme.surface
                    ),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isPremiumMerchant) DeepGold else ForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (isBengali) "সহজ খাতা প্রিমিয়াম" else "Shohoj Khata Premium",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isPremiumMerchant) Color.White else ForestGreen
                                )
                            }

                            Text(
                                text = if (isPremiumMerchant) {
                                    if (isBengali) "আপনার প্রিমিয়াম স্টোর অ্যাক্সেস সফলভাবে সক্রিয় করা হয়েছে! সুবিধা উপভোগ করুন।" 
                                    else "Premium Store access is active! Public directory indexing & real-time client push sync are fully live."
                                } else {
                                    if (isBengali) "পাবলিক ডিরেক্টরিতে আপনার দোকান তালিকাভুক্ত করুন এবং গ্রাহকদের সরাসরি লাইভ বিল আপডেট পাঠান।" 
                                    else "Publish your storefront online to the public directory so regular customers can link instantly & stream balance status."
                                },
                                fontSize = 13.sp,
                                color = if (isPremiumMerchant) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 18.sp
                            )

                            if (isPremiumMerchant) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isBengali) "প্রিমিয়াম স্টোর অ্যাক্টিভ ✨" else "Active Premium Status ✨",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { showPremiumDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ForestGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(50.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Text(
                                        text = if (isBengali) "প্রিমিয়াম স্টোর চালু করুন" else "Activate Premium • ₹999",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Premium illustration element on the right
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
                                    .background(if (isPremiumMerchant) Color.White.copy(alpha = 0.15f) else DeepGold.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPremiumMerchant) Icons.Default.WorkspacePremium else Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = if (isPremiumMerchant) DeepGold else ForestGreen,
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
                            title = if (isBengali) "আমার প্রোফাইল ও অ্যাকাউন্ট" else "My Account & Profile",
                            onItemClick = { showEditUserSettingsDialog = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        if (ownerUser != null) {
                            SettingsListItem(
                                icon = Icons.Default.Storefront,
                                title = if (isBengali) "দোকানের সেটিংস" else "Shop Settings",
                                onItemClick = { showEditShopSettingsDialog = true }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = Color.LightGray.copy(alpha = 0.3f),
                                thickness = 1.dp
                            )
                        }

                        // 2. Segmented control language selector exactly matching requested designs
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isBengali) "ভাষা" else "App Language",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // "বাংলা" Segment (Selected if isBengali is true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (isBengali) Color.White else Color(0xFF1F1F1F)
                                        )
                                        .border(
                                            width = if (isBengali) 1.5.dp else 1.dp,
                                            color = if (isBengali) Color.White else Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .clickable { if (!isBengali) viewModel.setLanguage(true) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "বাংলা",
                                        fontSize = 15.sp,
                                        fontWeight = if (isBengali) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isBengali) Color(0xFF121212) else Color.White.copy(alpha = 0.65f)
                                    )
                                }

                                // "English" Segment (Selected if isBengali is false)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (!isBengali) Color.White else Color(0xFF1F1F1F)
                                        )
                                        .border(
                                            width = if (!isBengali) 1.5.dp else 1.dp,
                                            color = if (!isBengali) Color.White else Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .clickable { if (isBengali) viewModel.setLanguage(false) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "English",
                                        fontSize = 15.sp,
                                        fontWeight = if (!isBengali) FontWeight.Bold else FontWeight.Medium,
                                        color = if (!isBengali) Color(0xFF121212) else Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        val appMode by viewModel.appMode.collectAsState()
                        // Segmented control User Mode selector matching language selector style and requested design
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isBengali) "ব্যবহারকারীর ধরণ" else "User Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isMerchant = appMode == "MERCHANT"
                                // "🏪 Merchant" Segment (Selected if isMerchant is true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (isMerchant) Color.White else Color(0xFF1F1F1F)
                                        )
                                        .border(
                                            width = if (isMerchant) 1.5.dp else 1.dp,
                                            color = if (isMerchant) Color.White else Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .clickable { if (!isMerchant) viewModel.setAppMode("MERCHANT") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isBengali) "দোকানদার" else "Merchant",
                                        fontSize = 15.sp,
                                        fontWeight = if (isMerchant) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isMerchant) Color(0xFF121212) else Color.White.copy(alpha = 0.65f)
                                    )
                                }

                                // "👥 Client" Segment (Selected if isMerchant is false/CLIENT)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (!isMerchant) Color.White else Color(0xFF1F1F1F)
                                        )
                                        .border(
                                            width = if (!isMerchant) 1.5.dp else 1.dp,
                                            color = if (!isMerchant) Color.White else Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .clickable { if (isMerchant) viewModel.setAppMode("CLIENT") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isBengali) "কাস্টমার" else "Client",
                                        fontSize = 15.sp,
                                        fontWeight = if (!isMerchant) FontWeight.Bold else FontWeight.Medium,
                                        color = if (!isMerchant) Color(0xFF121212) else Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }

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
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF4CAF50),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray,
                                        checkedBorderColor = Color(0xFF4CAF50),
                                        uncheckedBorderColor = Color.Gray
                                    )
                                )
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        val syncState by viewModel.syncState.collectAsState()
                        SettingsListItem(
                            icon = Icons.Default.CloudSync,
                            title = if (isBengali) "ক্লাউড সিকিউর ব্যাকআপ" else "Upload / Sync to Supabase",
                            trailingContent = {
                                val statusText = when (syncState) {
                                    LedgerViewModel.SyncState.SYNCING -> if (isBengali) "সিঙ্ক..." else "Syncing..."
                                    LedgerViewModel.SyncState.SUCCESS -> if (isBengali) "সফল ✅" else "Success ✅"
                                    LedgerViewModel.SyncState.ERROR -> if (isBengali) "ব্যর্থ ❌" else "Failed ❌"
                                    else -> if (isBengali) "ব্যাকআপ" else "Backup"
                                }
                                val badgeBg = when (syncState) {
                                    LedgerViewModel.SyncState.SYNCING -> Color.Gray.copy(alpha = 0.2f)
                                    LedgerViewModel.SyncState.SUCCESS -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    LedgerViewModel.SyncState.ERROR -> Color.Red.copy(alpha = 0.15f)
                                    else -> Color.Gray.copy(alpha = 0.15f)
                                }
                                val badgeColor = when (syncState) {
                                    LedgerViewModel.SyncState.SYNCING -> Color.LightGray
                                    LedgerViewModel.SyncState.SUCCESS -> Color(0xFF4CAF50)
                                    LedgerViewModel.SyncState.ERROR -> Color.Red
                                    else -> Color.LightGray
                                }
                                Text(
                                    text = statusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            },
                            onItemClick = {
                                viewModel.triggerSync()
                                val msg = if (isBengali) "ক্লাউড আপলোড শুরু হয়েছে..." else "Uploading database to Supabase Cloud..."
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        SettingsListItem(
                            icon = Icons.Default.ExitToApp,
                            title = if (isBengali) "লগআউট (গুগল অ্যাকাউন্ট)" else "Log Out (Google Account)",
                            titleColor = Color.Red,
                            onItemClick = {
                                viewModel.logoutGoogle()
                                val logoutAlert = if (isBengali) "সাফল্যজনকভাবে লগআউট সম্পন্ন!" else "Secured Google log-out complete!"
                                Toast.makeText(context, logoutAlert, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Show edit user settings dialog
        if (showEditUserSettingsDialog && ownerUser != null) {
            EditUserSettingsDialog(
                ownerUser = ownerUser!!,
                viewModel = viewModel,
                isBengali = isBengali,
                onDismiss = { showEditUserSettingsDialog = false }
            )
        }

        // Show edit shop settings dialog
        if (showEditShopSettingsDialog && ownerUser != null) {
            EditShopSettingsDialog(
                ownerUser = ownerUser!!,
                viewModel = viewModel,
                isBengali = isBengali,
                onDismiss = { showEditShopSettingsDialog = false }
            )
        }

        // Show Premium Paywall Simulation Dialog
        if (showPremiumDialog) {
            PremiumPaywallDialog(
                ownerUser = ownerUser,
                viewModel = viewModel,
                isBengali = isBengali,
                onDismiss = { showPremiumDialog = false }
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
fun EditUserSettingsDialog(
    ownerUser: User,
    viewModel: LedgerViewModel,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(ownerUser.name) }
    var pin by remember { mutableStateOf(ownerUser.pin) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131316),
        title = {
            Text(
                text = if (isBengali) "প্রোফাইল সংশোধন" else "Edit Profile Information",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
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
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = ForestGreen,
                        unfocusedLeadingIconColor = Color.Gray
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
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = ForestGreen,
                        unfocusedLeadingIconColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || pin.length != 4) {
                        val alert = if (isBengali) "অনুগ্রহ করে সব তথ্য সঠিক দিন" else "Please correctly fill all information"
                        Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateProfile(name, ownerUser.shopName, ownerUser.shopType, ownerUser.upiId, pin)
                        val successAlert = if (isBengali) "প্রোফাইল পরিবর্তন সফল হয়েছে!" else "Profile details updated!"
                        Toast.makeText(context, successAlert, Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(if (isBengali) "সংরক্ষণ" else "Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel", color = Color.LightGray)
            }
        }
    )
}

@Composable
fun EditShopSettingsDialog(
    ownerUser: User,
    viewModel: LedgerViewModel,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    var shopName by remember { mutableStateOf(ownerUser.shopName) }
    val rawShopType = ownerUser.shopType
    val defaultCats = listOf("Grocery", "Pharmacy", "Cafe & Tea")
    val initialIsOther = defaultCats.none { rawShopType.startsWith(it) }

    var selectedCategoryTab by remember {
        mutableStateOf(
            if (initialIsOther) "Other"
            else defaultCats.firstOrNull { rawShopType.startsWith(it) } ?: "Grocery"
        )
    }
    var customCategoryName by remember {
        mutableStateOf(
            if (initialIsOther) rawShopType else ""
        )
    }
    var upiId by remember { mutableStateOf(ownerUser.upiId) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131316),
        title = {
            Text(
                text = if (isBengali) "দোকানের সেটিংস" else "Edit Shop Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text(if (isBengali) "দোকানের নাম" else "Shop Name") },
                    leadingIcon = { Icon(Icons.Default.Store, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = ForestGreen,
                        unfocusedLeadingIconColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (isBengali) "দোকানের ক্যাটাগরি" else "Shop Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Grocery", "Pharmacy").forEach { cat ->
                            val isSel = selectedCategoryTab == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF2E7D32) else Color.White.copy(alpha = 0.08f))
                                        .clickable { 
                                            selectedCategoryTab = cat
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Cafe & Tea", "Other").forEach { cat ->
                                val isSel = selectedCategoryTab == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF2E7D32) else Color.White.copy(alpha = 0.08f))
                                        .clickable { 
                                            selectedCategoryTab = cat
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }
                    }

                    if (selectedCategoryTab == "Other") {
                        OutlinedTextField(
                            value = customCategoryName,
                            onValueChange = { customCategoryName = it },
                            label = { Text(if (isBengali) "কাস্টম ক্যাটাগরি" else "Custom Category") },
                            placeholder = { Text("e.g. Cloth Store") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color(0xFF1C1C1E),
                                unfocusedContainerColor = Color(0xFF1C1C1E),
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedLabelColor = Color(0xFF2E7D32),
                                unfocusedLabelColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text(if (isBengali) "পেমেন্ট UPI আইডি" else "Payment UPI ID") },
                    leadingIcon = { Icon(Icons.Default.QrCode, null, tint = ForestGreen) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = ForestGreen,
                        unfocusedLeadingIconColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCategory = if (selectedCategoryTab == "Other") {
                        if (customCategoryName.isNotBlank()) customCategoryName else "Other Shop"
                    } else {
                        "$selectedCategoryTab Shop"
                    }
                    if (shopName.isBlank()) {
                        val alert = if (isBengali) "দয়া করে দোকানের নাম দিন" else "Please enter a shop name"
                        Toast.makeText(context, alert, Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateProfile(
                            name = ownerUser.name,
                            shopName = shopName,
                            shopType = finalCategory,
                            upiId = upiId,
                            pin = ownerUser.pin
                        )
                        val successAlert = if (isBengali) "দোকানের সেটিংস আপডেট করা হয়েছে!" else "Shop details updated!"
                        Toast.makeText(context, successAlert, Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(if (isBengali) "সংরক্ষণ" else "Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel", color = Color.LightGray)
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
        containerColor = Color(0xFF131316),
        title = {
            Text(
                text = if (isBengali) "ক্লাউড থেকে খাতা রিস্টোর করুন" else "Restore Ledger from Cloud",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
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
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.Gray
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
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.Gray
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
                        color = if (syncState == LedgerViewModel.SyncState.ERROR) Color.Red else Color(0xFF2E7D32),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(if (isBengali) "রিস্টোর করুন" else "Restore", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel", color = Color.LightGray)
            }
        }
    )
}

@Composable
fun PlanComparisonTable(isBengali: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131316)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "ফিচার" else "Feature",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = if (isBengali) "ফ্রি (₹০)" else "Free (₹0)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isBengali) "প্রিমিয়াম (₹৯৯৯)" else "Premium (₹999)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = DeepGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Comparison Rows
            val comparisons = listOf(
                Triple(
                    if (isBengali) "গ্রাহক সংখ্যা" else "Customers",
                    if (isBengali) "সর্বোচ্চ ১৫" else "Max 15 customers",
                    if (isBengali) "আনলিমিটেড" else "Unlimited customers"
                ),
                Triple(
                    if (isBengali) "হোয়াটসঅ্যাপ" else "WhatsApp",
                    if (isBengali) "নেই" else "No WhatsApp reminders",
                    if (isBengali) "১-ক্লিক" else "Yes (1-Click WhatsApp reminders)"
                ),
                Triple(
                    if (isBengali) "পাবলিক ডিরেক্টরি" else "Shop Directory",
                    if (isBengali) "পাবলিক স্টোর" else "Public (Listed in global Shop Section)",
                    if (isBengali) "পাবলিক স্টোর" else "Public (Listed in global Shop Section)"
                ),
                Triple(
                    if (isBengali) "আইটেমযুক্ত হিসাব" else "Itemized Entries",
                    if (isBengali) "৫০ চা, ৫০ আদা" else "Itemized entries (50 cha, 50 ada)",
                    if (isBengali) "৫০ চা, ৫০ আদা" else "Itemized entries (50 cha, 50 ada)"
                ),
                Triple(
                    if (isBengali) "ক্লায়েন্ট ড্যাশবোর্ড" else "Client UI Live",
                    if (isBengali) "রিয়েল-টাইম" else "Real-Time line-item details on client UI",
                    if (isBengali) "রিয়েল-টাইম" else "Real-Time line-item details on client UI"
                )
            )

            comparisons.forEach { (feature, free, premium) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.weight(1.1f)
                    )
                    
                    // Free Column
                    Box(
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (free.contains("No") || free.contains("নেই") || free.contains("Max") || free.contains("সর্বোচ্চ")) Color.Red.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = free,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal,
                                color = if (free.contains("No") || free.contains("নেই")) Color(0xFFFF8A80) else Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }
                    }

                    // Premium Column
                    Box(
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = premium,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF81C784),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            }
        }
    }
}

@Composable
fun PremiumPaywallDialog(
    ownerUser: User?,
    viewModel: LedgerViewModel,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isPremiumMerchant by viewModel.isPremiumMerchant.collectAsState()
    var step by remember { mutableStateOf(if (isPremiumMerchant) 3 else 0) } // 0 = Perks, 1 = Config, 2 = Process Payment, 3 = Success Screen
    
    var shopName by remember { mutableStateOf(ownerUser?.shopName ?: "") }
    var shopType by remember { mutableStateOf(ownerUser?.shopType ?: "Grocery Shop") }
    var location by remember { mutableStateOf("Garia, Kolkata") }
    var upiId by remember { mutableStateOf(ownerUser?.upiId ?: "merchant@upi") }

    val defaultCats = listOf("Grocery", "Pharmacy", "Cafe & Tea")
    val initialIsOther = ownerUser != null && ownerUser.shopType.isNotBlank() && defaultCats.none { ownerUser.shopType.startsWith(it) }
    var selectedCategoryTab by remember {
        mutableStateOf(
            if (initialIsOther) "Other"
            else defaultCats.firstOrNull { shopType.startsWith(it) } ?: "Grocery"
        )
    }
    var customCategoryName by remember {
        mutableStateOf(
            if (initialIsOther) ownerUser.shopType else ""
        )
    }

    // If step == 2: Simulate payment network transit
    var paymentProgress by remember { mutableStateOf(0f) }
    var paymentStatusText by remember { mutableStateOf("") }

    LaunchedEffect(step) {
        if (step == 2) {
            paymentProgress = 0f
            paymentStatusText = if (isBengali) "নিরাপদ পেমেন্ট গেটওয়েতে সংযোগ করা হচ্ছে..." else "Opening secure UPI checkout gateway..."
            kotlinx.coroutines.delay(600)
            paymentProgress = 0.4f
            paymentStatusText = if (isBengali) "ট্রানজ্যাকশন অনুমোদিত হচ্ছে..." else "Authorizing ₹999 subscription charge..."
            kotlinx.coroutines.delay(800)
            paymentProgress = 0.8f
            paymentStatusText = if (isBengali) "পেমেন্ট সফলভাবে নিশ্চিত করা হয়েছে!" else "Secure UPI Payment authorization successful!"
            kotlinx.coroutines.delay(600)
            paymentProgress = 1.0f
            step = 3
        }
    }

    Dialog(onDismissRequest = { if (step != 2) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131316)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step == 0) { // Step 0: High-Impact Perks display
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DeepGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = DeepGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = if (isBengali) "সহজ খাতা প্রিমিয়াম" else "ShohojKhata Premium",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepGold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isBengali) "মাত্র ₹৯৯৯/বছরে আনলিমিটেড কাস্টমার পান!" 
                        else "₹999/year • Unlock Unlimited Customers & Growth!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.15f), thickness = 1.dp)

                    // Interactive Plan Comparison Table!
                    PlanComparisonTable(isBengali = isBengali)

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isBengali) "পরে করুন" else "Maybe Later",
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1.3f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isBengali) "পরবর্তী" else "Continue • ₹999",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else if (step == 1) { // Step 1: Configuration Form
                    Text(
                        text = if (isBengali) "স্টোরফ্রন্ট কনফিগার করুন" else "Configure Store Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text(if (isBengali) "দোকানের নাম" else "Store / Business Name") },
                        placeholder = { Text("e.g. Laxmi Store") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color(0xFF1C1C1E),
                            unfocusedContainerColor = Color(0xFF1C1C1E),
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = Color(0xFF2E7D32),
                            unfocusedLabelColor = Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isBengali) "দোকানের ক্যাটাগরি" else "Choose Store Category",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Grocery", "Pharmacy").forEach { cat ->
                                val isSel = selectedCategoryTab == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF2E7D32) else Color.White.copy(alpha = 0.08f))
                                        .clickable { 
                                            selectedCategoryTab = cat
                                            shopType = "$cat Shop"
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Cafe & Tea", "Other").forEach { cat ->
                                val isSel = selectedCategoryTab == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF2E7D32) else Color.White.copy(alpha = 0.08f))
                                        .clickable { 
                                            selectedCategoryTab = cat
                                            if (cat == "Other") {
                                                shopType = if (customCategoryName.isNotBlank()) customCategoryName else "Other Shop"
                                            } else {
                                                shopType = "$cat Shop"
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }

                        if (selectedCategoryTab == "Other") {
                            OutlinedTextField(
                                value = customCategoryName,
                                onValueChange = { 
                                    customCategoryName = it
                                    shopType = if (it.isNotBlank()) it else "Other Shop"
                                },
                                label = { Text(if (isBengali) "কাস্টম ক্যাটাগরি" else "Custom Category Name") },
                                placeholder = { Text(if (isBengali) "যেমন: Cloth Store" else "e.g. Cloth Store") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.LightGray,
                                    focusedContainerColor = Color(0xFF121214),
                                    unfocusedContainerColor = Color(0xFF121214),
                                    focusedBorderColor = Color(0xFF2E7D32),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                    focusedLabelColor = Color(0xFF2E7D32),
                                    unfocusedLabelColor = Color.Gray,
                                    focusedPlaceholderColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.Gray
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBengali) "অবস্থান / ঠিকানা" else "Store Location") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color(0xFF1C1C1E),
                            unfocusedContainerColor = Color(0xFF1C1C1E),
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = Color(0xFF2E7D32),
                            unfocusedLabelColor = Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text(if (isBengali) "ইউপিআই আইডি (পেমেন্ট সংগ্রহের জন্য)" else "Merchant Payment UPI ID") },
                        placeholder = { Text("e.g. business@upi") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color(0xFF1C1C1E),
                            unfocusedContainerColor = Color(0xFF1C1C1E),
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = Color(0xFF2E7D32),
                            unfocusedLabelColor = Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { step = 0 },
                            modifier = Modifier.weight(0.4f)
                        ) {
                            Text(text = if (isBengali) "পিছনে" else "Back", color = Color.LightGray)
                        }

                        Button(
                            onClick = {
                                if (shopName.isNotBlank() && location.isNotBlank() && upiId.isNotBlank()) {
                                    step = 2
                                }
                            },
                            enabled = shopName.isNotBlank() && location.isNotBlank() && upiId.isNotBlank(),
                            modifier = Modifier.weight(0.6f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                disabledContainerColor = Color(0xFF2E7D32).copy(alpha = 0.25f),
                                contentColor = Color.White,
                                disabledContentColor = Color.White.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = if (isBengali) "পেমেন্ট সিমুলেশন" else "Proceed with Pay", color = Color.White)
                        }
                    }
                } else if (step == 2) { // Step 2: Simulated payment processing
                    CircularProgressIndicator(
                        progress = { paymentProgress },
                        color = ForestGreen,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(56.dp)
                    )

                    Text(
                        text = if (isBengali) "পেমেন্ট প্রসেসিং হচ্ছে..." else "Processing Subscription Securely...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = paymentStatusText,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    LinearProgressIndicator(
                        progress = { paymentProgress },
                        color = DeepGold,
                        trackColor = Color.LightGray.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                } else if (step == 3) { // Step 3: Success Screen
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ForestGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = if (isBengali) "পেমেন্ট সফল হয়েছে! 🎉" else "Subscription Successful! 🎉",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreen,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isBengali) "অভিনন্দন! আপনার স্টোরটি সক্রিয় হয়েছে এবং স্থানীয় কাস্টমারদের জন্য পাবলিক ডিরেক্টরিতে প্রকাশিত হয়েছে।" 
                        else "Congratulations! Your premium storefront is active and now publicly live on the local directory mall.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isPremiumMerchant) {
                            OutlinedButton(
                                onClick = { step = 1 },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, ForestGreen)
                            ) {
                                Text(
                                    text = if (isBengali) "তথ্য সংশোধন" else "Edit Details",
                                    color = ForestGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.activatePremiumMerchant(shopName, shopType, location, upiId)
                                Toast.makeText(
                                    context,
                                    if (isBengali) "সহজ খাতা প্রিমিয়াম সেটিং সংরক্ষিত!" else "Store features synchronized successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = if (isPremiumMerchant) {
                                    if (isBengali) "সংরক্ষণ করুন" else "Save & Close"
                                } else {
                                    if (isBengali) "ঠিক আছে" else "Finish Setup"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppModeSwitcherRow(viewModel: LedgerViewModel) {
    val appMode by viewModel.appMode.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFCECECE))
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2C2C2C))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (appMode == "MERCHANT") MaterialTheme.colorScheme.background else Color.Transparent)
                    .clickable { viewModel.setAppMode("MERCHANT") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isBengali) "🏪 দোকানদার" else "🏪 Merchant",
                    color = if (appMode == "MERCHANT") MaterialTheme.colorScheme.surface else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (appMode == "CLIENT") MaterialTheme.colorScheme.background else Color.Transparent)
                    .clickable { viewModel.setAppMode("CLIENT") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isBengali) "👥 কাস্টমার" else "👥 Client",
                    color = if (appMode == "CLIENT") MaterialTheme.colorScheme.surface else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GoogleSignInScreen(viewModel: LedgerViewModel) {
    val isBengali by viewModel.isBengali.collectAsState()
    var showAccountChooser by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf("ONBOARDING") } // "ONBOARDING" or "CREATE_ACCOUNT"
    var simulatedEmail by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (currentScreen == "ONBOARDING") {
        // --- SCREEN A: PREMIUM INTERLOCKING ONBOARDING SPLASH ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF09090B)) // Dark sleek cosmic night background
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Top Logo Block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF18C22)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Sohoj Logo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "সহজ খাতা" else "Shohoj Khata!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    LanguageToggleButton(viewModel = viewModel)
                }

                // 2. Interlocking Tilted Stack of Cards (Matches requested Lose It! aesthetic)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Card 1: Back-Left Tilted Card (White)
                    Card(
                        modifier = Modifier
                            .size(width = 175.dp, height = 195.dp)
                            .offset(x = (-80).dp, y = (-25).dp)
                            .rotate(-12f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Ledger",
                                tint = Color(0xFF1C1C1E),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isBengali) "খাতা হিসাব\nসহজে রাখুন" else "Track running\nledger easily",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1C1E),
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Card 2: Back-Right Tilted Card (Orange Theme)
                    Card(
                        modifier = Modifier
                            .size(width = 175.dp, height = 195.dp)
                            .offset(x = 80.dp, y = (-10).dp)
                            .rotate(14f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF18C22)),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "UPI Collections",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isBengali) "ডিজিটাল পেমেন্ট\nসংগ্রহ ও সিঙ্ক" else "Direct UPI\ncollections",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Card 3: Front Overlapping Centered Card (Glassmorphic Frosted Card representation)
                    Card(
                        modifier = Modifier
                            .size(width = 190.dp, height = 210.dp)
                            .offset(x = 0.dp, y = 45.dp)
                            .rotate(-4f)
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                                RoundedCornerShape(26.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(26.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.15f),
                                            Color.White.copy(alpha = 0.04f)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Cloud Sync",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = if (isBengali) "লাইভ ক্লাউড\nসিঙ্ক্রোনাইজেশন" else "Real-time live\ncloud sync",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                // 3. Action Control Block
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isBengali) "১-ক্লিকেই দ্রুত সাইনআপ এবং লাইভ ব্যাকআপ" else "Zero-friction instant Google signup with real-time sync",
                        fontSize = 13.sp,
                        color = Color.LightGray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Button(
                        onClick = { currentScreen = "CREATE_ACCOUNT" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFF18C22), Color(0xFFD66D0E))
                                    )
                                )
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBengali) "শুরু করুন" else "Get Started",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Button(
                        onClick = { 
                            currentScreen = "CREATE_ACCOUNT"
                            showAccountChooser = true 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = if (isBengali) "সাইন-ইন করুন" else "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else {
        // --- SCREEN B: MODERN AUTH / CREATE ACCOUNT ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF09090B)) // Dark sleek background matching Image 1
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Top Bar with back navigation of mockup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentScreen = "ONBOARDING" },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1C1C1E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF18C22)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Sohoj Logo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "সহজ খাতা" else "Shohoj Khata",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // 2. Middle Authentication Block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // Title and descriptions
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBengali) "নতুন অ্যাকাউন্ট খুলুন" else "Create Account",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isBengali) "ডিজিটাল খাতা সুরক্ষিত রাখতে একটি অ্যাকাউন্ট তৈরি করুন ও ভেরিফাই করুন।"
                                   else "Create a secure account to organize your shop running ledger books.",
                            fontSize = 13.sp,
                            color = Color.LightGray.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    // Social login lists
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Google Button
                        Button(
                            onClick = { showAccountChooser = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131316)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "G",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color(0xFFEA4335)
                                    )
                                }
                                Text(
                                    text = if (isBengali) "গুগল অ্যাকাউন্ট দিয়ে সাইন-ইন" else "Continue with Google",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Guest / Optional AppAuth representation button exactly matching layout (e.g. Apple button representation)
                        Button(
                            onClick = { 
                                // Simulate Guest Access and seeding
                                viewModel.signInWithGoogle("Guest User", "guest.shohoj@gmail.com")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131316)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsRun,
                                    contentDescription = "Guest",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (isBengali) "সরাসরি গেস্ট হিসেবে ব্যবহার করুন" else "Continue as Guest",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // OR divider
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                        Text(
                            text = if (isBengali) "অথবা ইমেইল দিয়ে" else "Or with email",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray.copy(alpha = 0.4f)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                    }

                    // Local focus clearance to avoid soft keyboard IME transition timeouts
                    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

                    // Email Address text input
                    OutlinedTextField(
                        value = simulatedEmail,
                        onValueChange = { simulatedEmail = it },
                        placeholder = { Text(if (isBengali) "ইমেইল এড্রেস লিখুন" else "Email address", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color(0xFF131316),
                            unfocusedContainerColor = Color(0xFF131316),
                            focusedBorderColor = Color(0xFFF18C22),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (simulatedEmail.isBlank() || !simulatedEmail.contains("@")) {
                                    Toast.makeText(context, if (isBengali) "সঠিক ইমেইল এড্রেস প্রবেশ করান" else "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.signInWithGoogle(simulatedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }, simulatedEmail)
                                }
                            }
                        )
                    )

                    // Gradient Submit Button
                    Button(
                        onClick = {
                            if (simulatedEmail.isBlank() || !simulatedEmail.contains("@")) {
                                Toast.makeText(context, if (isBengali) "সঠিক ইমেইল এড্রেস প্রবেশ করান" else "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.signInWithGoogle(simulatedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }, simulatedEmail)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFF18C22), Color(0xFFD66D0E))
                                    )
                                )
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBengali) "অ্যাকাউন্ট তৈরি করুন" else "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // 3. Bottom Footer Links
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isBengali) "আগের একাউন্ট আছে? লগ-ইন" else "Have an account? Log In",
                        color = Color.LightGray.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showAccountChooser = true }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isBengali) "ব্যবহারের শর্তাবলী  ।  গোপনীয়তা নীতি" else "Terms of Service  |  Privacy Policy",
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Account Chooser Dialog
        if (showAccountChooser) {
            AlertDialog(
                onDismissRequest = { showAccountChooser = false },
                containerColor = Color(0xFF131316),
                title = {
                    Text(
                        text = if (isBengali) "একটি অ্যাকাউন্ট বেছে নিন" else "Choose an account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        listOf(
                            "Faizen Ahmed" to "faizennahmed@gmail.com",
                            "Siam Rahman" to "siam.rahman@gmail.com"
                        ).forEach { (name, email) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.signInWithGoogle(name, email)
                                        showAccountChooser = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF18C22).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = name.first().toString().uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFFF18C22)
                                        )
                                    }
                                    Column {
                                        Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                                        Text(email, fontSize = 12.sp, color = Color.LightGray.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showAccountChooser = false }) {
                        Text(if (isBengali) "বাদ দিন" else "Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun PremiumMerchantGate(
    viewModel: LedgerViewModel,
    onSuccess: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    var currentStep by remember { mutableStateOf("PLANS") } // "PLANS", "CHECKOUT", "STORE_CONFIG"

    // Card details state
    var cardHolder by remember { mutableStateOf("Bertil Boisen") }
    var cardNumber by remember { mutableStateOf("1234 1234 1234 1234") }
    var cardExpiry by remember { mutableStateOf("12/27") }
    var cardCvv by remember { mutableStateOf("552") }

    // Store config state
    var shopName by remember { mutableStateOf("") }
    var shopType by remember { mutableStateOf("Grocery Shop") }

    val defaultGateCats = remember { listOf("Grocery", "Pharmacy", "Cafe & Tea") }
    var selectedGateCategoryTab by remember { mutableStateOf("Grocery") }
    var customGateCategoryName by remember { mutableStateOf("") }

    var location by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }

    val context = LocalContext.current

    when (currentStep) {
        "PLANS" -> {
            // --- PAYWALL STEP 1: SUBSCRIPTION PLANS & FAQS ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WarmBg)
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top back panel representation of mockup
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { 
                                    // Soft back or switch appMode back to guest CLI
                                    viewModel.setAppMode("CLIENT") 
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                        Text(
                            text = if (isBengali) "সাবস্ক্রিপশন প্ল্যান" else "Subscription Plans",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    // Onboarding Get Premium Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(
                            text = if (isBengali) "প্রিমিয়াম স্টোর পান" else "Get Premium",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isBengali) "এক্সক্লুসিভ ফিচার এবং বিজ্ঞাপন মুক্ত ডিরেক্টরি সিঙ্কের জন্য প্রিমিয়াম কিনুন!"
                                   else "Subscribe to Premium for exclusive features\nand live ledger stream updates!",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }

                    // Premium Color Plan Card (Styled with current theme highlights)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary // Matches ForestGreen active color
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "Crown Premium",
                                    tint = DeepGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isBengali) "₹৯৯৯" else "₹999",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isBengali) " / বাৎসরিক" else " / year",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }

                            Text(
                                text = if (isBengali) "আনলিমিটেড কাস্টমার ও ১-ক্লিক হোয়াটসঅ্যাপ রিমাইন্ডার" else "Unlimited customers & 1-click WhatsApp reminders",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Plan button
                            Button(
                                onClick = { currentStep = "CHECKOUT" },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "প্ল্যান সাবস্ক্রাইব করুন" else "Manage plan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // ShohojKhata Plan Comparison board directly integrated into scroll list
                    Text(
                        text = if (isBengali) "প্ল্যান তুলনা করুন" else "Compare Plans",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Start).padding(top = 8.dp, start = 4.dp)
                    )

                    PlanComparisonTable(isBengali = isBengali)

                    // FAQs Accordion Board (Matches bottom of Image 2 mockup)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "প্রায়শই জিজ্ঞাসিত প্রশ্নাবলী (FAQs)" else "Frequently asked questions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        var activeFaqIndex by remember { mutableStateOf(-1) }
                        val faqsList = listOf(
                            (if (isBengali) "ক্লাউড সিঙ্ক্রোনাইজেশন কিভাবে কাজ করে?" else "How does cloud sync update works?") to 
                            (if (isBengali) "আপনি যখনই কোনো নতুন লেনদেন লিখবেন, সহজ খাতা তা সরাসরি সার্ভারে ক্লাউড সিঙ্ক করে ফেলে যাতে কাস্টমার সাথে সাথেই লাইভ আপডেট দেখতে পায়।" 
                             else "Every invoice transaction details sync immediately to secure cloud databases so your regular clients can monitor it in real-time."),
                            
                            (if (isBengali) "কাস্টমার ফ্রিতে অ্যাপ ব্যবহার করতে পারবে?" else "Can regular customers use the system for free?") to 
                            (if (isBengali) "হ্যাঁ! কাস্টমার সম্পূর্ণ বিনামূল্যে সহজ খাতা প্লেস্টোর থেকে ব্যবহার করে আপনার লভ্যাংশ এবং রানিং বিল দেখতে পারে।" 
                             else "Absolutely! Clients can view their running ledger book and live ledger updates completely free using Client Mode on their devices."),

                            (if (isBengali) "আমি কি কোনো কাস্টমারকে লেজার বুক থেকে বাদ দিতে পারবো?" else "Can I disconnect or remove any joined clients?") to 
                            (if (isBengali) "হ্যাঁ, যেকোনো কাস্টমারকে প্রোফাইল ট্যাব অথবা কাস্টমার ব্যালেন্স উইন্ডো থেকে এক ক্লিকেই রিমুভ বা ব্লক করা যাবে।" 
                             else "Yes! You can instantly review, block, or delete client profiles and uncollectable ledger entities straight via settings panel.")
                        )

                        faqsList.forEachIndexed { idx, (q, a) ->
                            val isFaqOpen = activeFaqIndex == idx
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                onClick = { activeFaqIndex = if (isFaqOpen) -1 else idx }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = q,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(0.9f)
                                        )
                                        Icon(
                                            imageVector = if (isFaqOpen) Icons.Default.Remove else Icons.Default.Add,
                                            contentDescription = "Expand FAQ indicator",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    AnimatedVisibility(visible = isFaqOpen) {
                                        Text(
                                            text = a,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            lineHeight = 18.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        "CHECKOUT" -> {
            // --- PAYWALL STEP 2: PREMIUM ADD CARD / MASTER CARD FORM ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WarmBg)
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top checkout Back header panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { currentStep = "PLANS" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back to plans",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                        Text(
                            text = if (isBengali) "কার্ড যুক্ত করুন" else "Add card",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    // STUNNING MASTER CARD UI COMPOSABLE (Updates in real-time!)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF2E2E38), Color(0xFF141419))
                                    )
                                )
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Master Card",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                // Wifi contact symbol icon representation
                                Icon(
                                    imageVector = Icons.Default.Contactless,
                                    contentDescription = "Contactless symbol",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // Dynamic typed Card Number
                            Text(
                                text = if (cardNumber.isNotBlank()) cardNumber else "•••• •••• •••• ••••",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Expiry and Name Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "CARD HOLDER NAME",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.LightGray.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = if (cardHolder.isNotBlank()) cardHolder.uppercase() else "BERTI BOISEN",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "EXPIRY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.LightGray.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = if (cardExpiry.isNotBlank()) cardExpiry else "MM/YY",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Mastercard Red/Yellow circle icon overlay
                                    Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEA3725))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF9900).copy(alpha = 0.82f))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Card Payment Input Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Cardholder Name Input
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBengali) "কার্ডধারীর নাম" else "Card holder name",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            OutlinedTextField(
                                value = cardHolder,
                                onValueChange = { cardHolder = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        // Card Number Input
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBengali) "কার্ড নম্বর" else "Card Number",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        // Code details grid Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Expiry Date Input
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "মেয়াদ উত্তীর্ণের তারিখ" else "Expiry Date",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            // CVV Code Input
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "CVV",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { cardCvv = it },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Checkout Action Add Card submit button
                    Button(
                        onClick = {
                            if (cardHolder.isBlank() || cardNumber.isBlank() || cardExpiry.isBlank() || cardCvv.isBlank()) {
                                Toast.makeText(context, if (isBengali) "দয়া করে সব পেমেন্ট তথ্য পূরণ করুন" else "Please fill all card details", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, if (isBengali) "পেমেন্ট অথোরাইজেশন সফল হয়েছে!" else "Premium deposit verified successfully!", Toast.LENGTH_SHORT).show()
                                currentStep = "STORE_CONFIG"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(
                            text = if (isBengali) "কার্ড যোগ করুন এবং নিশ্চিত করুন" else "Add Card",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        "STORE_CONFIG" -> {
            // --- PAYWALL STEP 3: CONFIGURE STOREFRONT ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WarmBg)
                    .systemBarsPadding()
                    .padding(24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131316)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isBengali) "আপনার দোকান কনফিগার করুন" else "Setup Your Digital Storefront",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Text(
                            text = if (isBengali) "সাবস্ক্রিপশন চালু হয়েছে! নিচের তথ্য দিয়ে আপনার পাবলিক স্টোর ডিরেক্টরি প্রকাশ করুন।"
                                   else "Premium Active! Provide storefront listing details below to start running live accounts.",
                            fontSize = 13.sp,
                            color = Color.LightGray.copy(alpha = 0.8f)
                        )

                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            label = { Text(if (isBengali) "দোকানের নাম" else "Shop Name") },
                            placeholder = { Text("e.g. Laxmi Grocery") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color(0xFF1C1C1E),
                                unfocusedContainerColor = Color(0xFF1C1C1E),
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedLabelColor = Color(0xFF2E7D32),
                                unfocusedLabelColor = Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = if (isBengali) "দোকানের ক্যাটাগরি" else "Shop Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Grocery", "Pharmacy").forEach { cat ->
                                    val isSel = selectedGateCategoryTab == cat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color(0xFF2E7D32) else Color.White.copy(alpha = 0.08f))
                                            .clickable { 
                                                selectedGateCategoryTab = cat
                                                shopType = "$cat Shop"
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else Color.LightGray
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Cafe & Tea", "Other").forEach { cat ->
                                    val isSel = selectedGateCategoryTab == cat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color(0xFF2E7D32) else Color.White.copy(alpha = 0.08f))
                                            .clickable { 
                                                selectedGateCategoryTab = cat
                                                if (cat == "Other") {
                                                    shopType = if (customGateCategoryName.isNotBlank()) customGateCategoryName else "Other Shop"
                                                } else {
                                                    shopType = "$cat Shop"
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else Color.LightGray
                                        )
                                    }
                                }
                            }
                        }

                        if (selectedGateCategoryTab == "Other") {
                            OutlinedTextField(
                                value = customGateCategoryName,
                                onValueChange = { 
                                    customGateCategoryName = it
                                    shopType = if (it.isNotBlank()) it else "Other Shop"
                                },
                                label = { Text(if (isBengali) "কাস্টম ক্যাটাগরি" else "Custom Category Name") },
                                placeholder = { Text(if (isBengali) "যেমন: Cloth Store" else "e.g. Cloth Store") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.LightGray,
                                    focusedContainerColor = Color(0xFF1C1C1E),
                                    unfocusedContainerColor = Color(0xFF1C1C1E),
                                    focusedBorderColor = Color(0xFF2E7D32),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                    focusedLabelColor = Color(0xFF2E7D32),
                                    unfocusedLabelColor = Color.Gray,
                                    focusedPlaceholderColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.Gray
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(if (isBengali) "ঠিকানা / অবস্থান" else "Store Location") },
                            placeholder = { Text("e.g. Garia, Kolkata") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color(0xFF1C1C1E),
                                unfocusedContainerColor = Color(0xFF1C1C1E),
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedLabelColor = Color(0xFF2E7D32),
                                unfocusedLabelColor = Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { upiId = it },
                            label = { Text(if (isBengali) "ইউপিআই আইডি ( পেমেন্টের জন্য)" else "Merchant UPI ID") },
                            placeholder = { Text("e.g. laxmigrocery@upi") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color(0xFF1C1C1E),
                                unfocusedContainerColor = Color(0xFF1C1C1E),
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedLabelColor = Color(0xFF2E7D32),
                                unfocusedLabelColor = Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { currentStep = "CHECKOUT" },
                                modifier = Modifier.weight(0.4f)
                            ) {
                                Text(if (isBengali) "বাতিল" else "Back", color = Color.Gray)
                            }

                            Button(
                                onClick = {
                                    if (shopName.isBlank() || location.isBlank() || upiId.isBlank()) {
                                        return@Button
                                    }
                                    viewModel.activatePremiumMerchant(shopName, shopType, location, upiId)
                                    onSuccess()
                                },
                                enabled = shopName.isNotBlank() && location.isNotBlank() && upiId.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    disabledContainerColor = Color(0xFF2E7D32).copy(alpha = 0.35f),
                                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(0.6f)
                            ) {
                                Text(if (isBengali) "চালু করুন" else "Launch Store", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientPortalScreen(viewModel: LedgerViewModel, onOpenSettings: () -> Unit) {
    val isBengali by viewModel.isBengali.collectAsState()
    val googleName by viewModel.googleName.collectAsState()
    val joinedStores by viewModel.joinedStores.collectAsState()
    val activeMerchant by viewModel.authenticatedUser.collectAsState()
    val isPremiumMerchant by viewModel.isPremiumMerchant.collectAsState()

    var activeClientTab by remember { mutableStateOf("MY_BOOKS") }
    var selectedShopIdForBill by remember { mutableStateOf<String?>(null) }

    val activeClientName = googleName ?: "Faizen Ahmed"
    var ownedCustomersList by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var isLoadingSummary by remember { mutableStateOf(true) }

    LaunchedEffect(activeClientName) {
        try {
            val allCusts = viewModel.repository.getAllCustomersDirect()
            val matches = allCusts.filter { it.name.lowercase() == activeClientName.lowercase() }
            ownedCustomersList = matches
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadingSummary = false
        }
    }

    val dueCustomers = remember(ownedCustomersList, joinedStores) {
        ownedCustomersList.filter { cust ->
            val shopIdStr = when (cust.ownerId) {
                1 -> "1"
                2 -> "2"
                3 -> "3"
                else -> cust.ownerId.toString()
            }
            (cust.isJoined || joinedStores.contains(shopIdStr)) && cust.totalDues > 0
        }
    }
    val totalDuesAmt = remember(dueCustomers) {
        dueCustomers.sumOf { it.totalDues }
    }

    if (selectedShopIdForBill != null) {
        ClientLiveBillView(
            viewModel = viewModel,
            shopId = selectedShopIdForBill!!,
            onBack = { selectedShopIdForBill = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBg)
            .padding(16.dp)
    ) {
        // 1. Client Profile Greeting exactly matching requested design
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 8.dp)
                .statusBarsPadding()
        ) {
            Text(
                text = if (isBengali) "হ্যালো," else "HELLO,",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activeClientName,
                fontSize = 36.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 2. High impact dynamic due-summary monochrome card matching previous theme
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF2C2C2C), Color(0xFF1E1E1E))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBengali) "আপনার দোকানসমূহে সর্বমোট বকেয়া" else "TOTAL DUES ACROSS YOUR SHOPS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF5E6D3),
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = "₹ ${String.format("%,.0f", totalDuesAmt)}",
                        fontSize = 54.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )

                    val shopCountText = if (isBengali) {
                        "${dueCustomers.size} টি দোকান"
                    } else {
                        "${dueCustomers.size} ${if (dueCustomers.size == 1) "shop" else "shops"}"
                    }
                    Text(
                        text = shopCountText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(4.dp)
        ) {
            listOf(
                "MY_BOOKS" to (if (isBengali) "আমার লাইভ বিল" else "My Joined Books"),
                "MALL" to (if (isBengali) "দোকানের তালিকা" else "Shop Directory")
            ).forEach { (tabId, label) ->
                val isSel = activeClientTab == tabId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) ForestGreen else Color.Transparent)
                        .clickable { activeClientTab = tabId }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (activeClientTab == "MALL") {
            val publicShops = remember(activeMerchant, isPremiumMerchant) {
                val list = mutableListOf(
                    Triple("1", "Laxmi Grocery Store", "Grocery Shop • Kolkata, WB"),
                    Triple("2", "Mayer Doa Pharmacy", "Pharmacy • Dhaka, BD"),
                    Triple("3", "Milon Tea Stall", "Restaurant & Cafe • Garia, WB")
                )
                val merchant = activeMerchant
                if (isPremiumMerchant && merchant != null) {
                    list.add(Triple("own", merchant.shopName, "${merchant.shopType} • Kolkata, WB"))
                }
                list
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(publicShops.size) { index ->
                    val (shopId, shopName, shopDesc) = publicShops[index]
                    val isJoined = joinedStores.contains(shopId)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(shopName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(shopDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isJoined) Color.LightGray.copy(alpha = 0.2f) else ForestGreen)
                                    .clickable {
                                        if (isJoined) {
                                            viewModel.leaveShop(shopId)
                                        } else {
                                            viewModel.joinShop(shopId)
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isJoined) (if (isBengali) "যুক্ত" else "Joined ✅") else (if (isBengali) "যোগ দিন" else "➕ Join"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isJoined) Color.Gray else Color.White
                                )
                            }
                        }
                    }
                }
            }
        } else {
            val joinedList = remember(joinedStores) { joinedStores.toList() }

            if (joinedList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "কোনো সংযুক্ত দোকান পাওয়া যায়নি। শপ মল খুঁজে সংযুক্ত হোন!" else "No joined stores found. Join shops to track items live!",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(joinedList.size) { index ->
                        val id = joinedList[index]
                        val name = when (id) {
                            "1" -> "Laxmi Grocery Store"
                            "2" -> "Mayer Doa Pharmacy"
                            "3" -> "Milon Tea Stall"
                            else -> activeMerchant?.shopName ?: "My Store"
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedShopIdForBill = id },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(ForestGreen.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Storefront, null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                                    }
                                    Column {
                                        Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(if (isBengali) "লাইভ ভিউ করতে ক্লিক করুন" else "Tap to view live bookkeeping", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientLiveBillView(
    viewModel: LedgerViewModel,
    shopId: String,
    onBack: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val googleName by viewModel.googleName.collectAsState()
    
    val targetOwnerId = remember(shopId) {
        when (shopId) {
            "1" -> 1
            "2" -> 2
            "3" -> 3
            else -> shopId.toIntOrNull() ?: (viewModel.authenticatedUser.value?.id ?: 1)
        }
    }
    val shopCustomersFlow = remember(viewModel.repository, targetOwnerId) {
        viewModel.repository.getCustomers(targetOwnerId)
    }
    val shopCustomers by shopCustomersFlow.collectAsState(initial = emptyList())
    
    val activeClientName = googleName ?: "Faizen Ahmed"
    val matchingCustomer = remember(shopCustomers, activeClientName) {
        shopCustomers.firstOrNull { it.name.lowercase() == activeClientName.lowercase() }
    }

    val customerTransactions = remember { mutableStateListOf<Transaction>() }
    var totalDues by remember { mutableStateOf(0.0) }

    LaunchedEffect(targetOwnerId, activeClientName) {
        // Do nothing without Supabase
    }

    LaunchedEffect(matchingCustomer) {
        if (matchingCustomer != null) {
            viewModel.repository.getTransactionsForCustomer(matchingCustomer.id).collect { list ->
                customerTransactions.clear()
                customerTransactions.addAll(list)
                totalDues = matchingCustomer.totalDues
            }
        } else {
            customerTransactions.clear()
            totalDues = 0.0
        }
    }

    var showPaymentQr by remember { mutableStateOf(false) }

    val shopTitle = when (shopId) {
        "1" -> "Laxmi Grocery Store"
        "2" -> "Mayer Doa Pharmacy"
        "3" -> "Milon Tea Stall"
        else -> "My Store"
    }

    val shopUpi = when (shopId) {
        "1" -> "laxmigrocery@upi"
        "2" -> "mayerdoa@upi"
        "3" -> "milontea@upi"
        else -> "generic@upi"
    }

    Scaffold(
        containerColor = WarmBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ForestGreen)
                }

                Column {
                    Text(shopTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Text(if (isBengali) "লাইভ সুরক্ষিত লেজার বুক" else "Live Synced Ledger", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBengali) "আপনার মোট বকেয়া পরিমাণ" else "Your Net Account Balance",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    val balanceColor = if (totalDues >= 0) KhataRed else KhataGreen
                    val statusText = if (totalDues >= 0) {
                        if (isBengali) "দেবেন (Due)" else "DUE Owed"
                    } else {
                        if (isBengali) "পাবেন (We Owe You)" else "Advance Paid"
                    }

                    Text(
                        text = "₹${String.format("%,.2f", if (totalDues >= 0) totalDues else -totalDues)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = balanceColor
                    )

                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = balanceColor.copy(alpha = 0.85f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(balanceColor.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (totalDues > 0) {
                        Button(
                            onClick = { showPaymentQr = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) "ইউপিআই পেমেন্ট করুন" else "Pay Dues via UPI / Scan QR",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Text(
                text = if (isBengali) "লাইভ স্টেটমেন্ট খতিয়ান" else "Live Synced Timeline Ledger",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (customerTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "কোনো সাম্প্রতিক লেনদেন পাওয়া যায়নি।" else "No live history registered. Once the merchant links your account, entries stream here in real-time!",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(customerTransactions.size) { index ->
                        val tx = customerTransactions[index]
                        val isDebit = tx.type == "GIVE"

                        val txDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(tx.timestamp))

                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.description.ifEmpty { if (isDebit) (if (isBengali) "বাকি দেওয়া হল" else "Purchase") else (if (isBengali) "ক্যাশ পেমেন্ট" else "Receipt") },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = txDate,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Text(
                                    text = "₹${String.format("%,.0f", tx.amount)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isDebit) KhataRed else KhataGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPaymentQr) {
        UpiQrCodeDialog(
            viewModel = viewModel,
            upiId = shopUpi,
            merchantName = shopTitle,
            amount = totalDues,
            onDismiss = { showPaymentQr = false }
        )
    }
}

@Composable
fun ClientReportsScreen(viewModel: LedgerViewModel) {
    val isBengali by viewModel.isBengali.collectAsState()
    val googleName by viewModel.googleName.collectAsState()
    val activeClientName = googleName ?: "Faizen Ahmed"

    // Load data reactively inside the report
    var ownedCustomersList by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var allTransactionsList by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(activeClientName) {
        isLoading = true
        try {
            val allCusts = viewModel.repository.getAllCustomersDirect()
            val matches = allCusts.filter { it.name.lowercase() == activeClientName.lowercase() }
            ownedCustomersList = matches

            val allTxs = viewModel.repository.getAllTransactionsDirect()
            val custIds = matches.map { it.id }.toSet()
            val matchedTxs = allTxs.filter { custIds.contains(it.customerId) }
            allTransactionsList = matchedTxs.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val totalDues = remember(ownedCustomersList) {
        ownedCustomersList.sumOf { it.totalDues }
    }

    Scaffold(
        containerColor = WarmBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // High-Fidelity Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForestGreen,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .statusBarsPadding()
            ) {
                Column {
                    Text(
                        text = if (isBengali) "ব্যক্তিগত আমানত হিসাব" else "Personal Ledger Statement",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeClientName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ForestGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Section cards
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBengali) "মোট বাকি (ওভারঅল)" else "Total Outstanding Dues",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Analytics,
                                        contentDescription = null,
                                        tint = ForestGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "₹ ${String.format("%,.0f", totalDues)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (totalDues > 0) KhataRed else KhataGreen
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(ForestGreen.copy(alpha = 0.08f))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = if (isBengali) "যোগ দেওয়া খাতা" else "Join Books",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = "${ownedCustomersList.size}",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(ForestGreen.copy(alpha = 0.08f))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = if (isBengali) "মোট লেনদেন" else "Total Transactions",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = "${allTransactionsList.size}",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Shop Breakdown Card
                    item {
                        Text(
                            text = if (isBengali) "দোকান ভিত্তিক হিসাব" else "Shopwise Standing",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (ownedCustomersList.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isBengali) "কোনো তথ্য এখনো নেই।" else "No shop files linked yet.",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        items(ownedCustomersList.size) { index ->
                            val customer = ownedCustomersList[index]
                            
                            // Let's resolve the shopName using ownerId
                            var resolvedShopName by remember { mutableStateOf("") }
                            LaunchedEffect(customer.ownerId) {
                                val user = viewModel.repository.getUserById(customer.ownerId)
                                resolvedShopName = user?.shopName ?: when (customer.ownerId.toString()) {
                                    "1" -> "Laxmi Grocery Store"
                                    "2" -> "Mayer Doa Pharmacy"
                                    "3" -> "Milon Tea Stall"
                                    else -> "My Store"
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = resolvedShopName.ifEmpty { if (isBengali) "লোড হচ্ছে..." else "Loading shop..." },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isBengali) "সংযুক্ত খাতা আইডি: ${customer.id}" else "Synced Book ID # ${customer.id}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    
                                    Text(
                                        text = "₹ ${String.format("%,.0f", customer.totalDues)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (customer.totalDues > 0) KhataRed else KhataGreen
                                    )
                                }
                            }
                        }
                    }

                    // Transactions Log Section Header
                    item {
                        Text(
                            text = if (isBengali) "সম্পূর্ণ লেনদেন ইতিহাস" else "Full Statement History",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (allTransactionsList.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isBengali) "কোনো লেনদেন পাওয়া যায়নি।" else "No transactions found.",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        items(allTransactionsList.size) { index ->
                            val tx = allTransactionsList[index]
                            val isDebit = tx.type == "GIVE"
                            
                            val txDate = remember(tx.timestamp) {
                                val sfd = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                sfd.format(Date(tx.timestamp))
                            }

                            // Find customer corresponding shop name dynamically 
                            var shopNameForTx by remember { mutableStateOf("") }
                            LaunchedEffect(tx.customerId) {
                                val cust = ownedCustomersList.firstOrNull { it.id == tx.customerId }
                                if (cust != null) {
                                    val user = viewModel.repository.getUserById(cust.ownerId)
                                    shopNameForTx = user?.shopName ?: when (cust.ownerId.toString()) {
                                        "1" -> "Laxmi Grocery Store"
                                        "2" -> "Mayer Doa Pharmacy"
                                        "3" -> "Milon Tea Stall"
                                        else -> "My Store"
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = shopNameForTx.ifEmpty { if (isBengali) "লেজার বুক" else "Ledger book" },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = ForestGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tx.description.ifEmpty { if (isDebit) (if (isBengali) "বাকি বিক্রি" else "Purchase on Credit") else (if (isBengali) "টাকা পরিশোধ" else "Payment Received") },
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = txDate,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹ ${String.format("%,.0f", tx.amount)}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = if (isDebit) KhataRed else KhataGreen
                                        )
                                        Text(
                                            text = if (isDebit) (if (isBengali) "বাকি" else "DUE") else (if (isBengali) "পরিশোধিত" else "PAID"),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDebit) KhataRed else KhataGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

