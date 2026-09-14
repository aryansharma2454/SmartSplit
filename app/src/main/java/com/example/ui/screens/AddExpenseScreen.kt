package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExpenseCategory
import com.example.model.MemberSplit
import com.example.model.ReceiptExtraction
import com.example.model.SplitType
import com.example.model.User
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  initialGroupId: String? = null,
  onDismiss: () -> Unit,
  onOpenScanner: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  val defaultGroup = uiState.groups.find { it.id == initialGroupId } ?: uiState.groups.firstOrNull()

  var selectedGroupId by remember { mutableStateOf(defaultGroup?.id ?: "") }
  var description by remember { mutableStateOf("") }
  var amountInput by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
  var paidByUserId by remember { mutableStateOf(uiState.currentUser.id) }
  var splitType by remember { mutableStateOf(SplitType.EQUAL) }

  val activeGroup = uiState.groups.find { it.id == selectedGroupId } ?: defaultGroup
  val groupMembers = uiState.users.filter { activeGroup?.memberIds?.contains(it.id) == true }

  val selectedSplitMembers = remember { mutableStateListOf<String>() }

  // Sync members when group changes
  LaunchedEffect(selectedGroupId) {
    selectedSplitMembers.clear()
    selectedSplitMembers.addAll(groupMembers.map { it.id })
  }

  // Pre-fill from scanned receipt draft if present
  LaunchedEffect(uiState.scannedReceiptDraft) {
    uiState.scannedReceiptDraft?.let { draft ->
      description = "${draft.merchant} Bill"
      amountInput = String.format("%.2f", draft.total)
    }
  }

  val totalAmount = amountInput.toDoubleOrNull() ?: 0.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface)
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Top Bar
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("add_expense_close_btn"),
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }

        Text(
          text = "Add Expense",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
        )

        TextButton(
          onClick = onOpenScanner,
          modifier = Modifier.testTag("add_expense_scan_btn"),
        ) {
          Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Scan", fontWeight = FontWeight.SemiBold)
        }
      }
    }

    // Scanned receipt notification banner if active
    uiState.scannedReceiptDraft?.let { draft ->
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Scanned: ${draft.merchant}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
              Text(
                text = "${draft.items.size} itemized lines extracted automatically",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
              )
            }
          }
        }
      }
    }

    // Large Amount Input
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "ENTER AMOUNT",
          style = MaterialTheme.typography.labelSmall,
          color = extColors.textMuted,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          Text(
            text = uiState.currencySymbol,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.width(4.dp))
          OutlinedTextField(
            value = amountInput,
            onValueChange = { input ->
              if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                amountInput = input
              }
            },
            placeholder = { Text("0.00", fontSize = 32.sp, fontWeight = FontWeight.Bold) },
            textStyle = MaterialTheme.typography.displayMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Start,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .width(220.dp)
              .testTag("expense_amount_input"),
          )
        }
      }
    }

    // Description Input
    item {
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("What was this expense for?") },
        placeholder = { Text("e.g. Beach Shack Dinner, Taxi, Drinks") },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("expense_desc_input"),
      )
    }

    // Category Selector Chips
    item {
      Text(
        text = "Category",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(ExpenseCategory.entries) { cat ->
          val isSelected = selectedCategory == cat
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { selectedCategory = cat }
              .testTag("category_chip_${cat.name}"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(text = cat.emoji, fontSize = 14.sp)
              Text(
                text = cat.displayName.split(" ").first(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }

    // Group Selection Chips
    item {
      Text(
        text = "Choose Group",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(uiState.groups) { grp ->
          val isSelected = selectedGroupId == grp.id
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.5.dp else 0.dp,
              color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            ),
            modifier = Modifier
              .clickable { selectedGroupId = grp.id }
              .testTag("group_selector_${grp.id}"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(text = grp.emoji)
              Text(
                text = grp.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }

    // Paid By Section
    item {
      Text(
        text = "Paid By",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(groupMembers) { user ->
          val isSelected = paidByUserId == user.id
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { paidByUserId = user.id }
              .testTag("paid_by_chip_${user.id}"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              UserAvatar(user = user, size = 22.dp, fontSize = 9)
              Text(
                text = if (user.id == uiState.currentUser.id) "You" else user.name.split(" ").first(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }

    // Split Between Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Split Between (${selectedSplitMembers.size})",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
        )

        TextButton(onClick = {
          if (selectedSplitMembers.size == groupMembers.size) {
            selectedSplitMembers.clear()
            selectedSplitMembers.add(uiState.currentUser.id)
          } else {
            selectedSplitMembers.clear()
            selectedSplitMembers.addAll(groupMembers.map { it.id })
          }
        }) {
          Text(if (selectedSplitMembers.size == groupMembers.size) "Select None" else "Select All", fontSize = 12.sp)
        }
      }

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        groupMembers.forEach { user ->
          val isSelected = selectedSplitMembers.contains(user.id)
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.dp else 0.dp,
              color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            ),
            modifier = Modifier
              .clickable {
                if (isSelected) selectedSplitMembers.remove(user.id) else selectedSplitMembers.add(user.id)
              }
              .testTag("split_member_${user.id}"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              UserAvatar(user = user, size = 20.dp, fontSize = 9)
              Text(
                text = if (user.id == uiState.currentUser.id) "You" else user.name.split(" ").first(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }

    // Split Type Segmented Control (Section 15: Equal | Exact | Percentage)
    item {
      Text(
        text = "Split Type",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(4.dp),
      ) {
        SplitType.entries.forEach { type ->
          val isSelected = splitType == type
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
            shadowElevation = if (isSelected) 2.dp else 0.dp,
            modifier = Modifier
              .weight(1f)
              .clickable { splitType = type }
              .testTag("split_type_${type.name}"),
          ) {
            Text(
              text = type.label,
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) MaterialTheme.colorScheme.primary else extColors.textMuted,
              modifier = Modifier.padding(vertical = 10.dp),
            )
          }
        }
      }
    }

    // Real-Time Calculation Preview Card (Section 15)
    item {
      val splitCount = selectedSplitMembers.size.coerceAtLeast(1)
      val perPerson = if (splitCount > 0 && totalAmount > 0) totalAmount / splitCount else 0.0

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "${uiState.currencySymbol}${viewModel.formatAmount(totalAmount)} ÷ $splitCount people",
              style = MaterialTheme.typography.bodyMedium,
              color = extColors.textMuted,
            )
            Text(
              text = "Split calculation preview",
              style = MaterialTheme.typography.labelSmall,
              color = extColors.textMuted,
            )
          }

          Text(
            text = "${uiState.currencySymbol}${viewModel.formatAmount(perPerson)} each",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }
    }

    // Submit Button
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = {
          if (totalAmount > 0 && description.isNotBlank() && selectedSplitMembers.isNotEmpty() && selectedGroupId.isNotBlank()) {
            val count = selectedSplitMembers.size
            val each = totalAmount / count
            val splits = selectedSplitMembers.map { MemberSplit(it, each) }
            viewModel.addExpense(
              groupId = selectedGroupId,
              description = description.trim(),
              totalAmount = totalAmount,
              paidByUserId = paidByUserId,
              category = selectedCategory,
              splitType = splitType,
              memberSplits = splits,
              receiptMerchant = uiState.scannedReceiptDraft?.merchant,
            )
            onDismiss()
          }
        },
        enabled = totalAmount > 0 && description.isNotBlank() && selectedSplitMembers.isNotEmpty(),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("submit_add_expense_btn"),
      ) {
        Text(
          text = "Add Expense • ${uiState.currencySymbol}${viewModel.formatAmount(totalAmount)}",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
        )
      }
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
