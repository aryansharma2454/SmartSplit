package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Expense
import com.example.model.User
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun ExpenseDetailsDialog(
  expense: Expense,
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  onDismiss: () -> Unit,
) {
  val extColors = LocalFinTechColors.current
  val payer = uiState.users.find { it.id == expense.paidByUserId }
  val group = uiState.groups.find { it.id == expense.groupId }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("expense_details_dialog"),
    ) {
      LazyColumn(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // Top Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
              ) {
                Text(text = expense.category.emoji, fontSize = 20.sp)
              }
              Text(
                text = expense.category.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = extColors.textMuted,
              )
            }

            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
        }

        // Title & Amount
        item {
          Text(
            text = expense.description,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${uiState.currencySymbol}${viewModel.formatAmount(expense.totalAmount)}",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "Paid by ${payer?.name ?: "Someone"} • ${expense.createdAt}",
            style = MaterialTheme.typography.bodySmall,
            color = extColors.textMuted,
          )
          if (group != null) {
            Text(
              text = "Group: ${group.emoji} ${group.name}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold,
            )
          }
        }

        // Member Split Breakdown (Section 17)
        item {
          Text(
            text = "Split Breakdown (${expense.splits.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
        }

        items(expense.splits) { split ->
          val user = uiState.users.find { it.id == split.userId }
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                user?.let { UserAvatar(user = it, size = 26.dp, fontSize = 10) }
                Text(
                  text = if (user?.isCurrentUser == true) "You" else user?.name ?: "Member",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                )
              }

              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(split.amount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }

        // Itemized Receipt Section if available (Section 17)
        if (expense.receiptItems.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
              Text(
                text = "Itemized Receipt (${expense.receiptMerchant ?: "Merchant"})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
            }
          }

          items(expense.receiptItems) { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(text = item.name, style = MaterialTheme.typography.bodySmall, color = extColors.textMuted)
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(item.amount)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
              )
            }
          }
        }

        // Delete / Close actions
        item {
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedButton(
            onClick = {
              viewModel.deleteExpense(expense.id)
              onDismiss()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("delete_expense_btn"),
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text("Delete Expense", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
