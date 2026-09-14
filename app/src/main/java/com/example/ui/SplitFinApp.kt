package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DebtTransfer
import com.example.model.Expense
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.AddExpenseScreen
import com.example.ui.screens.BalancesScreen
import com.example.ui.screens.ExpenseDetailsDialog
import com.example.ui.screens.GroupDetailsScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReceiptScanScreen
import com.example.ui.theme.LocalFinTechColors
import com.example.ui.theme.SplitFinTheme
import com.example.viewmodel.ExpenseViewModel
import com.example.viewmodel.NavigationTab

enum class ActiveView {
  MAIN_TABS,
  BALANCES,
}

@Composable
fun SplitFinApp(
  viewModel: ExpenseViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  var activeView by remember { mutableStateOf(ActiveView.MAIN_TABS) }

  var isAddExpenseOpen by remember { mutableStateOf(false) }
  var addExpenseGroupId by remember { mutableStateOf<String?>(null) }
  var isScannerOpen by remember { mutableStateOf(false) }

  // Quick Settle Target modal from GroupDetails
  var quickSettleTransfer by remember { mutableStateOf<DebtTransfer?>(null) }

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.celebrationMessage) {
    uiState.celebrationMessage?.let { msg ->
      snackbarHostState.showSnackbar(
        message = msg,
        duration = SnackbarDuration.Short,
      )
      viewModel.clearCelebration()
    }
  }

  SplitFinTheme(darkTheme = uiState.isDarkMode) {
    val extColors = LocalFinTechColors.current

    if (uiState.showOnboarding) {
      OnboardingScreen(
        onFinish = { viewModel.showOnboarding(false) },
      )
    } else {
      Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
          if (uiState.selectedGroupId == null && !isAddExpenseOpen && !isScannerOpen) {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface,
              tonalElevation = 8.dp,
              modifier = Modifier
                .height(68.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            ) {
              val homeSelected = activeView == ActiveView.MAIN_TABS && uiState.currentTab == NavigationTab.HOME
              val groupsSelected = activeView == ActiveView.MAIN_TABS && uiState.currentTab == NavigationTab.GROUPS
              val balancesSelected = activeView == ActiveView.BALANCES
              val activitySelected = activeView == ActiveView.MAIN_TABS && uiState.currentTab == NavigationTab.ACTIVITY
              val profileSelected = activeView == ActiveView.MAIN_TABS && uiState.currentTab == NavigationTab.PROFILE

              NavigationBarItem(
                selected = homeSelected,
                onClick = {
                  activeView = ActiveView.MAIN_TABS
                  viewModel.setTab(NavigationTab.HOME)
                },
                icon = {
                  Icon(
                    if (homeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                  )
                },
                label = { Text("Home", fontSize = 11.sp, fontWeight = if (homeSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.testTag("nav_tab_home"),
              )

              NavigationBarItem(
                selected = groupsSelected,
                onClick = {
                  activeView = ActiveView.MAIN_TABS
                  viewModel.setTab(NavigationTab.GROUPS)
                },
                icon = {
                  Icon(
                    if (groupsSelected) Icons.Filled.Group else Icons.Outlined.Group,
                    contentDescription = "Groups",
                  )
                },
                label = { Text("Groups", fontSize = 11.sp, fontWeight = if (groupsSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.testTag("nav_tab_groups"),
              )

              NavigationBarItem(
                selected = balancesSelected,
                onClick = {
                  activeView = ActiveView.BALANCES
                },
                icon = {
                  Icon(
                    if (balancesSelected) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "Balances",
                  )
                },
                label = { Text("Balances", fontSize = 11.sp, fontWeight = if (balancesSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.testTag("nav_tab_balances"),
              )

              NavigationBarItem(
                selected = activitySelected,
                onClick = {
                  activeView = ActiveView.MAIN_TABS
                  viewModel.setTab(NavigationTab.ACTIVITY)
                },
                icon = {
                  Icon(
                    Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = "Activity",
                  )
                },
                label = { Text("Activity", fontSize = 11.sp, fontWeight = if (activitySelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.testTag("nav_tab_activity"),
              )

              NavigationBarItem(
                selected = profileSelected,
                onClick = {
                  activeView = ActiveView.MAIN_TABS
                  viewModel.setTab(NavigationTab.PROFILE)
                },
                icon = {
                  Icon(
                    if (profileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile",
                  )
                },
                label = { Text("Profile", fontSize = 11.sp, fontWeight = if (profileSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.testTag("nav_tab_profile"),
              )
            }
          }
        },
        floatingActionButton = {
          // Floating Action Button on Home Screen
          if (uiState.selectedGroupId == null && activeView == ActiveView.MAIN_TABS && uiState.currentTab == NavigationTab.HOME && !isAddExpenseOpen) {
            FloatingActionButton(
              onClick = {
                addExpenseGroupId = null
                isAddExpenseOpen = true
              },
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = Color.White,
              shape = CircleShape,
              modifier = Modifier.testTag("main_add_expense_fab"),
            ) {
              Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
          }
        },
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        ) {
          // Screen Routing Logic
          if (uiState.selectedGroupId != null) {
            GroupDetailsScreen(
              groupId = uiState.selectedGroupId!!,
              viewModel = viewModel,
              uiState = uiState,
              onBack = { viewModel.selectGroup(null) },
              onSelectExpense = { viewModel.selectExpense(it) },
              onAddExpenseInGroup = { grpId ->
                addExpenseGroupId = grpId
                isAddExpenseOpen = true
              },
              onOpenSettleModal = { from, to, amount ->
                quickSettleTransfer = DebtTransfer(from, to, amount)
              },
            )
          } else if (activeView == ActiveView.BALANCES) {
            BalancesScreen(
              viewModel = viewModel,
              uiState = uiState,
            )
          } else {
            when (uiState.currentTab) {
              NavigationTab.HOME -> {
                HomeScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                  onNavigateToGroups = { viewModel.setTab(NavigationTab.GROUPS) },
                  onSelectGroup = { viewModel.selectGroup(it) },
                  onSelectExpense = { viewModel.selectExpense(it) },
                  onOpenAddExpense = {
                    addExpenseGroupId = null
                    isAddExpenseOpen = true
                  },
                  onOpenScanReceipt = { isScannerOpen = true },
                  onOpenBalances = { activeView = ActiveView.BALANCES },
                )
              }
              NavigationTab.GROUPS -> {
                GroupsScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                  onSelectGroup = { viewModel.selectGroup(it) },
                )
              }
              NavigationTab.ACTIVITY -> {
                ActivityScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                )
              }
              NavigationTab.PROFILE -> {
                ProfileScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                  onShowOnboarding = { viewModel.showOnboarding(true) },
                )
              }
            }
          }
        }
      }

      // Add Expense Fullscreen Dialog / Screen
      if (isAddExpenseOpen) {
        Dialog(
          onDismissRequest = { isAddExpenseOpen = false },
          properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
          AddExpenseScreen(
            viewModel = viewModel,
            uiState = uiState,
            initialGroupId = addExpenseGroupId,
            onDismiss = { isAddExpenseOpen = false },
            onOpenScanner = { isScannerOpen = true },
          )
        }
      }

      // Receipt Scan Screen
      if (isScannerOpen) {
        Dialog(
          onDismissRequest = { isScannerOpen = false },
          properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
          ReceiptScanScreen(
            viewModel = viewModel,
            uiState = uiState,
            onClose = { isScannerOpen = false },
            onUseExtractedExpense = {
              isScannerOpen = false
              isAddExpenseOpen = true
            },
          )
        }
      }

      // Expense Details Sheet / Dialog
      uiState.selectedExpense?.let { expense ->
        ExpenseDetailsDialog(
          expense = expense,
          viewModel = viewModel,
          uiState = uiState,
          onDismiss = { viewModel.selectExpense(null) },
        )
      }

      // Quick Settle Up Dialog from GroupDetails
      quickSettleTransfer?.let { transfer ->
        val toUser = uiState.users.find { it.id == transfer.toUserId }
        val fromUser = uiState.users.find { it.id == transfer.fromUserId }
        Dialog(onDismissRequest = { quickSettleTransfer = null }) {
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
          ) {
            androidx.compose.foundation.layout.Column(
              modifier = Modifier.padding(22.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(14.dp),
            ) {
              Text(
                text = "Settle Debt",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = "${fromUser?.name ?: "Someone"} pays ${toUser?.name ?: "Someone"}",
                style = MaterialTheme.typography.bodyMedium,
                color = extColors.textMuted,
              )
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(transfer.amount)}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
              )
              androidx.compose.material3.Button(
                onClick = {
                  viewModel.settleDebt(transfer.fromUserId, transfer.toUserId, transfer.amount, "Direct settlement")
                  quickSettleTransfer = null
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
              ) {
                Text("Mark as Paid", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
