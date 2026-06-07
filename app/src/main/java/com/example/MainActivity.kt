package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LedgerViewModel = viewModel()
            val isBengali by viewModel.isBengali.collectAsState()

            MyApplicationTheme(isBengali = isBengali) {
                val registeredUser by viewModel.registeredUserFlow.collectAsState()
                val authenticatedUser by viewModel.authenticatedUser.collectAsState()
                val googleEmail by viewModel.googleEmail.collectAsState()
                val isLocked by viewModel.isLocked.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        googleEmail == null -> {
                            // Step 1: Zero-Friction Google Sign-In Splash Screen
                            GoogleSignInScreen(viewModel = viewModel)
                        }
                        registeredUser == null -> {
                            // Loading state during first launch DB seeding or flow loading
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                CircularProgressIndicator(color = com.example.ui.theme.ForestGreen)
                            }
                        }
                        else -> {
                            // App authenticated with dynamic role modes!
                            MainAppContainer(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: LedgerViewModel) {
    var activeTab by remember { mutableStateOf(ActiveScreen.Dashboard) }
    var showClientSettings by remember { mutableStateOf(false) }
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    val appMode by viewModel.appMode.collectAsState()
    val isPremiumMerchant by viewModel.isPremiumMerchant.collectAsState()

    // Properly capture system Android back-press to exit customer detail back to list
    if (activeTab == ActiveScreen.Dashboard && selectedCustomer != null) {
        BackHandler {
            viewModel.selectCustomer(null)
        }
    }

    Scaffold(
        bottomBar = {
            if (appMode == "MERCHANT" || appMode == "CLIENT") {
                Box(
                    modifier = Modifier.navigationBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                com.example.ui.theme.ForestGreen.copy(alpha = 0.15f),
                                RoundedCornerShape(32.dp)
                            )
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            modifier = Modifier.height(72.dp)
                        ) {
                            NavigationBarItem(
                                selected = activeTab == ActiveScreen.Dashboard,
                                onClick = {
                                    viewModel.selectCustomer(null)
                                    activeTab = ActiveScreen.Dashboard
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (appMode == "CLIENT") {
                                            if (activeTab == ActiveScreen.Dashboard) Icons.Filled.Storefront else Icons.Outlined.Storefront
                                        } else {
                                            if (activeTab == ActiveScreen.Dashboard) Icons.Filled.Book else Icons.Outlined.Book
                                        },
                                        contentDescription = if (appMode == "CLIENT") {
                                            if (isBengali) "দোকান সমূহ" else "Shops"
                                        } else {
                                            if (isBengali) "আমানত খাতা" else "Ledger book"
                                        }
                                    )
                                },
                                label = { 
                                    Text(
                                        text = if (appMode == "CLIENT") {
                                            if (isBengali) "দোকান" else "Shops"
                                        } else {
                                            if (isBengali) "খাতা" else "Ledger"
                                        },
                                        fontWeight = if (activeTab == ActiveScreen.Dashboard) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    ) 
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = com.example.ui.theme.MintGreenLight,
                                    selectedIconColor = com.example.ui.theme.ForestGreen,
                                    unselectedIconColor = Color.Gray,
                                    selectedTextColor = com.example.ui.theme.ForestGreen,
                                    unselectedTextColor = Color.Gray
                                )
                            )
                            NavigationBarItem(
                                selected = activeTab == ActiveScreen.Reports,
                                onClick = { activeTab = ActiveScreen.Reports },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == ActiveScreen.Reports) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                                        contentDescription = if (isBengali) "রিপোর্ট" else "Reports"
                                    )
                                },
                                label = { 
                                    Text(
                                        text = if (isBengali) "রিপোর্ট" else "Reports",
                                        fontWeight = if (activeTab == ActiveScreen.Reports) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    ) 
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = com.example.ui.theme.MintGreenLight,
                                    selectedIconColor = com.example.ui.theme.ForestGreen,
                                    unselectedIconColor = Color.Gray,
                                    selectedTextColor = com.example.ui.theme.ForestGreen,
                                    unselectedTextColor = Color.Gray
                                )
                            )
                            NavigationBarItem(
                                selected = activeTab == ActiveScreen.Profile,
                                onClick = { activeTab = ActiveScreen.Profile },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == ActiveScreen.Profile) Icons.Filled.Settings else Icons.Outlined.Settings,
                                        contentDescription = if (isBengali) "প্রোফাইল" else "Settings"
                                    )
                                },
                                label = { 
                                    Text(
                                        text = if (appMode == "CLIENT") {
                                            if (isBengali) "সেটিংস" else "Settings"
                                        } else {
                                            if (isBengali) "প্রোফাইল" else "Profile"
                                        },
                                        fontWeight = if (activeTab == ActiveScreen.Profile) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    ) 
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = com.example.ui.theme.MintGreenLight,
                                    selectedIconColor = com.example.ui.theme.ForestGreen,
                                    unselectedIconColor = Color.Gray,
                                    selectedTextColor = com.example.ui.theme.ForestGreen,
                                    unselectedTextColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (appMode == "CLIENT") {
                when (activeTab) {
                    ActiveScreen.Dashboard -> {
                        ClientPortalScreen(
                            viewModel = viewModel,
                            onOpenSettings = { activeTab = ActiveScreen.Profile }
                        )
                    }
                    ActiveScreen.Reports -> {
                        ClientReportsScreen(viewModel = viewModel)
                    }
                    ActiveScreen.Profile -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onBack = { activeTab = ActiveScreen.Dashboard }
                        )
                    }
                    else -> { /* No-op */ }
                }
            } else {
                if (!isPremiumMerchant) {
                    PremiumMerchantGate(
                        viewModel = viewModel,
                        onSuccess = {
                            // Store creation complete, load merchant app
                        }
                    )
                } else {
                    when (activeTab) {
                        ActiveScreen.Dashboard -> {
                            if (selectedCustomer != null) {
                                CustomerDetailScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.selectCustomer(null) }
                                )
                            } else {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToCustomer = { cust -> viewModel.selectCustomer(cust) }
                                )
                            }
                        }
                        ActiveScreen.Reports -> {
                            ReportsScreen(viewModel = viewModel)
                        }
                        ActiveScreen.Profile -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                onBack = { activeTab = ActiveScreen.Dashboard }
                            )
                        }
                        else -> { /* No-op or exhaustiveness compliance */ }
                    }
                }
            }
        }
    }
}
