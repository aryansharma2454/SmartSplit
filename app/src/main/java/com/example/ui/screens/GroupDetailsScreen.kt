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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Expense
import com.example.model.Group
import com.example.ui.components.AvatarStack
import com.example.ui.components.DebtTransferFlowCard
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ExpenseCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun GroupDetailsScreen(
  groupId: String,
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  onBack: () -> Unit,
  onSelectExpense: (Expense) -> Unit,
  onAddExpenseInGroup: (String) -> Unit,
  onOpenSettleModal: (fromUserId: String, toUserId: String, amount: Double) -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  val group = uiState.groups.find { it.id == groupId }

  if (group == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Group not found")
    }
    return
  }

  val groupMembers = uiState.users.filter { group.memberIds.contains(it.id) }
  val groupExpenses = uiState.expenses.filter { it.groupId == groupId }
  val groupDebts = viewModel.calculatePairwiseDebts(groupId = groupId)

  // Compute Aryan's net balance in this group
  var aryanNetBalance = 0.0
  for (d in groupDebts) {
    if (d.toUserId == uiState.currentUser.id) aryanNetBalance += d.amount
    if (d.fromUserId == uiState.currentUser.id) aryanNetBalance -= d.amount
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Top Bar
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("group_details_back_btn"),
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(text = group.emoji, fontSize = 20.sp)
              Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
              )
            }
            Text(
              text = "${group.memberIds.size} members",
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted,
            )
          }
        }

        AvatarStack(users = groupMembers, maxCount = 3)
      }
    }

    // Financial Summary Card (Section 13)
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, extColors.cardBorder, RoundedCornerShape(20.dp))
          .testTag("group_financial_summary_card"),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            // Total Spending
            Column {
              Text(
                text = "Total Spending",
                style = MaterialTheme.typography.labelMedium,
                color = extColors.textMuted,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(group.totalSpent)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            // Your Balance
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Your Balance",
                style = MaterialTheme.typography.labelMedium,
                color = extColors.textMuted,
              )
              Spacer(modifier = Modifier.height(4.dp))
              val (balanceText, balanceColor) = when {
                aryanNetBalance > 0.01 -> Pair(
                  "+${uiState.currencySymbol}${viewModel.formatAmount(aryanNetBalance)}",
                  extColors.moneyReceive,
                )
                aryanNetBalance < -0.01 -> Pair(
                  "-${uiState.currencySymbol}${viewModel.formatAmount(kotlin.math.abs(aryanNetBalance))}",
                  extColors.moneyOwe,
                )
                else -> Pair("Settled up", extColors.moneySettled)
              }
              Text(
                text = balanceText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = balanceColor,
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action buttons: Add Expense & Settle Up
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Button(
              onClick = { onAddExpenseInGroup(group.id) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("group_add_expense_btn"),
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add Expense", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                // Find any debt where Aryan owes or is owed
                val directDebt = groupDebts.find { it.fromUserId == uiState.currentUser.id || it.toUserId == uiState.currentUser.id }
                if (directDebt != null) {
                  onOpenSettleModal(directDebt.fromUserId, directDebt.toUserId, directDebt.amount)
                } else if (groupDebts.isNotEmpty()) {
                  val first = groupDebts.first()
                  onOpenSettleModal(first.fromUserId, first.toUserId, first.amount)
                }
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("group_settle_up_btn"),
            ) {
              Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Settle Up", fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }

    // Who Owes Whom section (Section 13)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "Who Owes Whom",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text = if (uiState.isDebtSimplificationActive) "Direct simplified settlement routes" else "Pairwise debts",
            style = MaterialTheme.typography.bodySmall,
            color = extColors.textMuted,
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
          modifier = Modifier.clickable { viewModel.toggleDebtSimplification() },
        ) {
          Text(
            text = if (uiState.isDebtSimplificationActive) "Simplified ✓" else "Exact",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }
    }

    if (groupDebts.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = extColors.moneyReceive,
              modifier = Modifier.size(22.dp),
            )
            Text(
              text = "Everyone is completely settled up in this group! 🎉",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium,
            )
          }
        }
      }
    } else {
      items(groupDebts) { transfer ->
        val fromUser = uiState.users.find { it.id == transfer.fromUserId } ?: return@items
        val toUser = uiState.users.find { it.id == transfer.toUserId } ?: return@items

        DebtTransferFlowCard(
          fromUser = fromUser,
          toUser = toUser,
          amount = transfer.amount,
          currency = uiState.currencySymbol,
          onSettleClick = {
            onOpenSettleModal(transfer.fromUserId, transfer.toUserId, transfer.amount)
          },
        )
      }
    }

    // Expense List Timeline (Section 14)
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Expenses Timeline",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
          text = "${groupExpenses.size} items",
          style = MaterialTheme.typography.labelMedium,
          color = extColors.textMuted,
        )
      }
    }

    if (groupExpenses.isEmpty()) {
      item {
        EmptyStateView(
          title = "No expenses yet",
          description = "Add the first expense for ${group.name}.",
          buttonText = "Add Expense",
          onButtonClick = { onAddExpenseInGroup(group.id) },
        )
      }
    } else {
      items(groupExpenses) { expense ->
        val payer = uiState.users.find { it.id == expense.paidByUserId }?.name ?: "Member"
        val userShare = expense.splits.find { it.userId == uiState.currentUser.id }?.amount ?: 0.0

        ExpenseCard(
          expense = expense,
          payerName = payer,
          groupName = group.name,
          userShareAmount = userShare,
          isUserPayer = expense.paidByUserId == uiState.currentUser.id,
          currency = uiState.currencySymbol,
          onClick = { onSelectExpense(expense) },
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}
