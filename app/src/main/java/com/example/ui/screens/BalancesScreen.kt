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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DebtTransfer
import com.example.model.User
import com.example.ui.components.DebtTransferFlowCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun BalancesScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  val (youOwe, youAreOwed, netBalance) = viewModel.getOverallUserFinancials()
  val allDebts = viewModel.calculatePairwiseDebts(groupId = null)

  // Debts concerning current user
  val myDebtsToPay = allDebts.filter { it.fromUserId == uiState.currentUser.id }
  val myDebtsToReceive = allDebts.filter { it.toUserId == uiState.currentUser.id }

  var settleTarget by remember { mutableStateOf<DebtTransfer?>(null) }
  var showSettlementSuccessDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Header
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Balances",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Track and settle all your individual debts effortlessly",
        style = MaterialTheme.typography.bodyMedium,
        color = extColors.textMuted,
      )
    }

    // Overall Balance Card (Section 18)
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, extColors.cardBorder, RoundedCornerShape(20.dp)),
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text = "OVERALL BALANCE",
            style = MaterialTheme.typography.labelSmall,
            color = extColors.textMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
          )
          Spacer(modifier = Modifier.height(6.dp))
          val displayBalance = if (netBalance >= 0) "+ ${uiState.currencySymbol}${viewModel.formatAmount(netBalance)}" else "- ${uiState.currencySymbol}${viewModel.formatAmount(kotlin.math.abs(netBalance))}"
          val balanceColor = if (netBalance > 0.01) extColors.moneyReceive else if (netBalance < -0.01) extColors.moneyOwe else extColors.moneySettled

          Text(
            text = displayBalance,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = balanceColor,
          )

          Text(
            text = if (netBalance >= 0) "You are owed" else "You owe",
            style = MaterialTheme.typography.bodyMedium,
            color = extColors.textMuted,
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Total You Owe", style = MaterialTheme.typography.labelSmall, color = extColors.textMuted)
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(youOwe)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = extColors.moneyOwe,
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(extColors.cardBorder),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Total You Are Owed", style = MaterialTheme.typography.labelSmall, color = extColors.textMuted)
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(youAreOwed)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = extColors.moneyReceive,
              )
            }
          }
        }
      }
    }

    // Debt Simplification Section (Section 19)
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
              )
              Text(
                text = "Simplified Settlements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            Switch(
              checked = uiState.isDebtSimplificationActive,
              onCheckedChange = { viewModel.toggleDebtSimplification() },
              modifier = Modifier.testTag("debt_simplification_switch"),
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Our debt algorithm minimizes the total number of transactions needed across your groups. Instead of 3 circular payments, it computes the most direct settlement paths.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
          )
        }
      }
    }

    // You Owe Section (Section 18)
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = extColors.moneyOwe, modifier = Modifier.size(18.dp))
        Text(
          text = "You Owe (${myDebtsToPay.size})",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
        )
      }
    }

    if (myDebtsToPay.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = extColors.moneyReceive, modifier = Modifier.size(18.dp))
            Text(text = "You don't owe anyone right now! 🎉", style = MaterialTheme.typography.bodyMedium)
          }
        }
      }
    } else {
      items(myDebtsToPay) { debt ->
        val toUser = uiState.users.find { it.id == debt.toUserId } ?: return@items
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp)),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              UserAvatar(user = toUser, size = 38.dp)
              Column {
                Text(
                  text = toUser.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text = "You owe",
                  style = MaterialTheme.typography.bodySmall,
                  color = extColors.moneyOwe,
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(debt.amount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = extColors.moneyOwe,
              )
              Button(
                onClick = { settleTarget = debt },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .height(38.dp)
                  .testTag("settle_pay_btn_${debt.toUserId}"),
              ) {
                Text("Pay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // You Are Owed Section (Section 18)
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = extColors.moneyReceive, modifier = Modifier.size(18.dp))
        Text(
          text = "You Are Owed (${myDebtsToReceive.size})",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
        )
      }
    }

    if (myDebtsToReceive.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text = "No pending balances owed to you.",
            style = MaterialTheme.typography.bodyMedium,
            color = extColors.textMuted,
            modifier = Modifier.padding(14.dp),
          )
        }
      }
    } else {
      items(myDebtsToReceive) { debt ->
        val fromUser = uiState.users.find { it.id == debt.fromUserId } ?: return@items
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp)),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              UserAvatar(user = fromUser, size = 38.dp)
              Column {
                Text(
                  text = fromUser.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text = "Owes you",
                  style = MaterialTheme.typography.bodySmall,
                  color = extColors.moneyReceive,
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(debt.amount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = extColors.moneyReceive,
              )
              OutlinedButton(
                onClick = { settleTarget = debt },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(38.dp),
              ) {
                Text("Remind", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp))
    }
  }

  // Settle Up Confirmation Modal (Section 20)
  settleTarget?.let { debt ->
    val fromUser = uiState.users.find { it.id == debt.fromUserId }
    val toUser = uiState.users.find { it.id == debt.toUserId }
    var selectedMethod by remember { mutableStateOf("UPI") }

    Dialog(onDismissRequest = { settleTarget = null }) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
          .testTag("settle_up_modal"),
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Settle Up",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = { settleTarget = null }) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }

          Text(
            text = "Settle with ${toUser?.name ?: "Member"}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )

          Text(
            text = "You owe ${toUser?.name ?: "Member"}",
            style = MaterialTheme.typography.bodyMedium,
            color = extColors.textMuted,
          )

          Text(
            text = "${uiState.currencySymbol}${viewModel.formatAmount(debt.amount)}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )

          // Payment mode selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            listOf("UPI", "Bank Transfer", "Cash").forEach { method ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedMethod == method) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                  width = if (selectedMethod == method) 1.5.dp else 0.dp,
                  color = if (selectedMethod == method) MaterialTheme.colorScheme.primary else Color.Transparent,
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable { selectedMethod = method },
              ) {
                Text(
                  text = method,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (selectedMethod == method) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 10.dp),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = {
              viewModel.settleDebt(debt.fromUserId, debt.toUserId, debt.amount, "Paid via $selectedMethod")
              settleTarget = null
              showSettlementSuccessDialog = true
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("confirm_settlement_btn"),
          ) {
            Text("Confirm Settlement", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
        }
      }
    }
  }

  // All Settled! 🎉 Success celebration dialog (Section 20 requirement)
  if (showSettlementSuccessDialog) {
    Dialog(onDismissRequest = { showSettlementSuccessDialog = false }) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(extColors.moneyReceiveBg),
            contentAlignment = Alignment.Center,
          ) {
            Text(text = "🎉", fontSize = 34.sp)
          }

          Text(
            text = "All settled! 🎉",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )

          Text(
            text = "Payment marked and balances updated across all groups.",
            style = MaterialTheme.typography.bodyMedium,
            color = extColors.textMuted,
            textAlign = TextAlign.Center,
          )

          Button(
            onClick = { showSettlementSuccessDialog = false },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
          ) {
            Text("Awesome!", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
