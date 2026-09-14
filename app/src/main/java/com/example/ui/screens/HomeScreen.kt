package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Expense
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ExpenseCard
import com.example.ui.components.GroupCard
import com.example.ui.components.ShimmerCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun HomeScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  onNavigateToGroups: () -> Unit,
  onSelectGroup: (String) -> Unit,
  onSelectExpense: (Expense) -> Unit,
  onOpenAddExpense: () -> Unit,
  onOpenScanReceipt: () -> Unit,
  onOpenBalances: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  val (youOwe, youAreOwed, netBalance) = viewModel.getOverallUserFinancials()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Top Greeting & Actions
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          UserAvatar(user = uiState.currentUser, size = 46.dp, fontSize = 16)
          Column {
            Text(
              text = "Good morning, Aryan 👋",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = "Your group finances",
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted,
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Shimmer loading toggle for testing section 24/38
          IconButton(
            onClick = { viewModel.toggleSimulatedLoading() },
            modifier = Modifier.testTag("toggle_loading_button"),
          ) {
            Icon(
              imageVector = Icons.Default.HourglassEmpty,
              contentDescription = "Toggle Shimmer Loading",
              tint = if (uiState.isSimulatedLoading) MaterialTheme.colorScheme.primary else extColors.textMuted,
            )
          }

          // Dark mode toggle
          IconButton(
            onClick = { viewModel.toggleDarkMode() },
            modifier = Modifier.testTag("toggle_dark_mode"),
          ) {
            Icon(
              imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = MaterialTheme.colorScheme.primary,
            )
          }

          // Scan Receipt quick icon
          IconButton(
            onClick = onOpenScanReceipt,
            modifier = Modifier.testTag("home_scan_receipt_btn"),
          ) {
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = "Scan Receipt",
              tint = MaterialTheme.colorScheme.primary,
            )
          }
        }
      }
    }

    if (uiState.isSimulatedLoading) {
      // Skeleton Shimmer Loading States (Section 24 requirement)
      item {
        ShimmerCard(height = 180.dp)
      }
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          ShimmerCard(height = 80.dp, modifier = Modifier.weight(1f))
          ShimmerCard(height = 80.dp, modifier = Modifier.weight(1f))
          ShimmerCard(height = 80.dp, modifier = Modifier.weight(1f))
        }
      }
      item {
        ShimmerCard(height = 130.dp)
      }
      item {
        ShimmerCard(height = 90.dp)
      }
    } else {
      // 1. Large Balance Summary Card
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(
                  MaterialTheme.colorScheme.primary,
                  Color(0xFF312E81),
                  Color(0xFF064E3B),
                ),
              ),
            )
            .testTag("balance_summary_card"),
        ) {
          Column(
            modifier = Modifier.padding(22.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "YOUR BALANCE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
              )

              Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = extColors.moneyReceive,
                    modifier = Modifier.size(14.dp),
                  )
                  Text(
                    text = "↑ 12% last month",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val displayAmount = if (netBalance >= 0) "+ ${uiState.currencySymbol}${viewModel.formatAmount(netBalance)}" else "- ${uiState.currencySymbol}${viewModel.formatAmount(kotlin.math.abs(netBalance))}"
            Text(
              text = displayAmount,
              style = MaterialTheme.typography.displayMedium,
              color = Color.White,
              fontWeight = FontWeight.Bold,
            )

            Text(
              text = if (netBalance >= 0) "You are owed overall" else "You owe overall",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.85f),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .clickable(onClick = onOpenBalances)
                  .testTag("home_view_balances_btn"),
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = "Settle Up",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                  )
                }
              }

              Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .clickable(onClick = onOpenScanReceipt)
                  .testTag("home_scan_receipt_action"),
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Scan Receipt",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                  )
                }
              }
            }
          }
        }
      }

      // 2. Quick Stats Row (Section 10)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          // You Owe
          StatMiniCard(
            title = "You Owe",
            amount = "${uiState.currencySymbol}${viewModel.formatAmount(youOwe)}",
            amountColor = extColors.moneyOwe,
            modifier = Modifier.weight(1f),
            onClick = onOpenBalances,
          )

          // You Are Owed
          StatMiniCard(
            title = "You Are Owed",
            amount = "${uiState.currencySymbol}${viewModel.formatAmount(youAreOwed)}",
            amountColor = extColors.moneyReceive,
            modifier = Modifier.weight(1f),
            onClick = onOpenBalances,
          )

          // Settled
          StatMiniCard(
            title = "Settled",
            amount = "${uiState.currencySymbol}4,250.00",
            amountColor = extColors.moneySettled,
            modifier = Modifier.weight(1f),
            onClick = onOpenBalances,
          )
        }
      }

      // 3. Your Groups Header & Carousel/Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "Your Groups",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = "Tap to manage expenses & balances",
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted,
            )
          }

          TextButton(onClick = onNavigateToGroups) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "See all",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
              )
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
            }
          }
        }
      }

      if (uiState.groups.isEmpty()) {
        item {
          EmptyStateView(
            title = "No groups yet",
            description = "Create your first group and start splitting expenses effortlessly.",
            buttonText = "Create Group",
            onButtonClick = onNavigateToGroups,
          )
        }
      } else {
        items(uiState.groups.take(3)) { group ->
          val members = uiState.users.filter { group.memberIds.contains(it.id) }
          // Compute Aryan's net balance in this group
          val groupDebts = viewModel.calculatePairwiseDebts(groupId = group.id)
          var groupBal = 0.0
          for (d in groupDebts) {
            if (d.toUserId == uiState.currentUser.id) groupBal += d.amount
            if (d.fromUserId == uiState.currentUser.id) groupBal -= d.amount
          }

          GroupCard(
            group = group,
            members = members,
            userBalanceInGroup = groupBal,
            currency = uiState.currencySymbol,
            onClick = { onSelectGroup(group.id) },
          )
        }
      }

      // 4. Recent Activity
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
          )

          Text(
            text = "${uiState.expenses.size} expenses",
            style = MaterialTheme.typography.labelMedium,
            color = extColors.textMuted,
          )
        }
      }

      if (uiState.expenses.isEmpty()) {
        item {
          EmptyStateView(
            title = "No expenses yet",
            description = "Add your first shared expense to start tracking.",
            buttonText = "Add Expense",
            emoji = "💳",
            onButtonClick = onOpenAddExpense,
          )
        }
      } else {
        items(uiState.expenses.take(4)) { expense ->
          val payer = uiState.users.find { it.id == expense.paidByUserId }?.name ?: "Someone"
          val groupName = uiState.groups.find { it.id == expense.groupId }?.name ?: "Group"
          val userShare = expense.splits.find { it.userId == uiState.currentUser.id }?.amount ?: 0.0

          ExpenseCard(
            expense = expense,
            payerName = payer,
            groupName = groupName,
            userShareAmount = userShare,
            isUserPayer = expense.paidByUserId == uiState.currentUser.id,
            currency = uiState.currencySymbol,
            onClick = { onSelectExpense(expense) },
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
fun StatMiniCard(
  title: String,
  amount: String,
  amountColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
) {
  val extColors = LocalFinTechColors.current
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = extColors.textMuted,
        fontWeight = FontWeight.Medium,
      )
      Text(
        text = amount,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = amountColor,
      )
    }
  }
}
