package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.SampleData
import com.example.model.ActivityItem
import com.example.model.ActivityType
import com.example.model.DebtTransfer
import com.example.model.Expense
import com.example.model.ExpenseCategory
import com.example.model.Group
import com.example.model.MemberSplit
import com.example.model.ReceiptExtraction
import com.example.model.SplitType
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToLong

data class ExpenseUiState(
  val users: List<User> = SampleData.users,
  val currentUser: User = SampleData.currentUser,
  val groups: List<Group> = SampleData.initialGroups,
  val expenses: List<Expense> = SampleData.initialExpenses,
  val activities: List<ActivityItem> = SampleData.initialActivities,
  val selectedGroupId: String? = null,
  val selectedExpense: Expense? = null,
  val isDarkMode: Boolean = false,
  val currencySymbol: String = "₹",
  val isSimulatedLoading: Boolean = false,
  val isDebtSimplificationActive: Boolean = true,
  val celebrationMessage: String? = null,
  val currentTab: NavigationTab = NavigationTab.HOME,
  val scannedReceiptDraft: ReceiptExtraction? = null,
  val activityFilter: String = "All",
  val showOnboarding: Boolean = false,
  val showAuthDialog: Boolean = false,
)

enum class NavigationTab {
  HOME,
  GROUPS,
  ACTIVITY,
  PROFILE,
}

class ExpenseViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(ExpenseUiState())
  val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

  fun setTab(tab: NavigationTab) {
    _uiState.update { it.copy(currentTab = tab, selectedGroupId = null) }
  }

  fun toggleDarkMode() {
    _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
  }

  fun toggleSimulatedLoading() {
    _uiState.update { it.copy(isSimulatedLoading = !it.isSimulatedLoading) }
  }

  fun setCurrency(symbol: String) {
    _uiState.update { it.copy(currencySymbol = symbol) }
  }

  fun toggleDebtSimplification() {
    _uiState.update { it.copy(isDebtSimplificationActive = !it.isDebtSimplificationActive) }
  }

  fun selectGroup(groupId: String?) {
    _uiState.update { it.copy(selectedGroupId = groupId) }
  }

  fun selectExpense(expense: Expense?) {
    _uiState.update { it.copy(selectedExpense = expense) }
  }

  fun setActivityFilter(filter: String) {
    _uiState.update { it.copy(activityFilter = filter) }
  }

  fun showOnboarding(show: Boolean) {
    _uiState.update { it.copy(showOnboarding = show) }
  }

  fun showAuthDialog(show: Boolean) {
    _uiState.update { it.copy(showAuthDialog = show) }
  }

  fun clearCelebration() {
    _uiState.update { it.copy(celebrationMessage = null) }
  }

  fun createGroup(name: String, emoji: String, memberIds: List<String>, description: String = "") {
    val newGroup = Group(
      id = "g_${System.currentTimeMillis()}",
      name = name.ifBlank { "New Group" },
      emoji = emoji.ifBlank { "💳" },
      memberIds = if (memberIds.contains(_uiState.value.currentUser.id)) memberIds else memberIds + _uiState.value.currentUser.id,
      createdBy = _uiState.value.currentUser.id,
      createdAt = "Today",
      totalSpent = 0.0,
      description = description,
    )
    val newActivity = ActivityItem(
      id = "a_${System.currentTimeMillis()}",
      type = ActivityType.GROUP_CREATED,
      title = "You created \"$name\"",
      subtitle = "${newGroup.memberIds.size} members joined",
      amount = 0.0,
      isUserReceiving = null,
      timestamp = "Just now",
      emoji = emoji.ifBlank { "👥" },
    )
    _uiState.update {
      it.copy(
        groups = listOf(newGroup) + it.groups,
        activities = listOf(newActivity) + it.activities,
        celebrationMessage = "Group \"$name\" created! 🎉",
      )
    }
  }

  fun addExpense(
    groupId: String,
    description: String,
    totalAmount: Double,
    paidByUserId: String,
    category: ExpenseCategory,
    splitType: SplitType,
    memberSplits: List<MemberSplit>,
    receiptMerchant: String? = null,
  ) {
    val newExpense = Expense(
      id = "e_${System.currentTimeMillis()}",
      groupId = groupId,
      description = description.ifBlank { "Expense" },
      totalAmount = totalAmount,
      paidByUserId = paidByUserId,
      category = category,
      splitType = splitType,
      splits = memberSplits,
      receiptMerchant = receiptMerchant,
      createdAt = "Just now",
    )

    val groupName = _uiState.value.groups.find { it.id == groupId }?.name ?: "Group"
    val payerName = _uiState.value.users.find { it.id == paidByUserId }?.name ?: "Someone"
    val isAryanPayer = paidByUserId == _uiState.value.currentUser.id
    val aryanShare = memberSplits.find { it.userId == _uiState.value.currentUser.id }?.amount ?: 0.0

    val (title, subtitle, isReceiving) = if (isAryanPayer) {
      val othersOwe = totalAmount - aryanShare
      Triple("You added $description", "$groupName • You receive ${_uiState.value.currencySymbol}${formatAmount(othersOwe)}", true)
    } else {
      Triple("$payerName added $description", "$groupName • You owe ${_uiState.value.currencySymbol}${formatAmount(aryanShare)}", false)
    }

    val newActivity = ActivityItem(
      id = "a_${System.currentTimeMillis()}",
      type = ActivityType.EXPENSE_ADDED,
      title = title,
      subtitle = subtitle,
      amount = totalAmount,
      isUserReceiving = isReceiving,
      timestamp = "Just now",
      emoji = category.emoji,
    )

    _uiState.update { state ->
      val updatedGroups = state.groups.map { g ->
        if (g.id == groupId) g.copy(totalSpent = g.totalSpent + totalAmount) else g
      }
      state.copy(
        expenses = listOf(newExpense) + state.expenses,
        groups = updatedGroups,
        activities = listOf(newActivity) + state.activities,
        scannedReceiptDraft = null,
        celebrationMessage = "Expense added successfully! 💸",
      )
    }
  }

  fun deleteExpense(expenseId: String) {
    val expense = _uiState.value.expenses.find { it.id == expenseId } ?: return
    _uiState.update { state ->
      val updatedGroups = state.groups.map { g ->
        if (g.id == expense.groupId) g.copy(totalSpent = maxOf(0.0, g.totalSpent - expense.totalAmount)) else g
      }
      state.copy(
        expenses = state.expenses.filter { it.id != expenseId },
        groups = updatedGroups,
        selectedExpense = null,
      )
    }
  }

  fun settleDebt(fromUserId: String, toUserId: String, amount: Double, note: String = "Marked as Paid") {
    val fromUser = _uiState.value.users.find { it.id == fromUserId }?.name ?: "Member"
    val toUser = _uiState.value.users.find { it.id == toUserId }?.name ?: "Member"
    val currentUserId = _uiState.value.currentUser.id

    val (title, isReceiving) = when {
      fromUserId == currentUserId -> Pair("You settled with $toUser", false)
      toUserId == currentUserId -> Pair("$fromUser paid you", true)
      else -> Pair("$fromUser settled with $toUser", null)
    }

    val newActivity = ActivityItem(
      id = "a_${System.currentTimeMillis()}",
      type = ActivityType.SETTLEMENT_DONE,
      title = title,
      subtitle = "$note • ${_uiState.value.currencySymbol}${formatAmount(amount)}",
      amount = amount,
      isUserReceiving = isReceiving,
      timestamp = "Just now",
      emoji = "🤝",
    )

    _uiState.update { state ->
      state.copy(
        activities = listOf(newActivity) + state.activities,
        celebrationMessage = "All settled! 🎉",
      )
    }
  }

  fun setScannedReceipt(extraction: ReceiptExtraction?) {
    _uiState.update { it.copy(scannedReceiptDraft = extraction) }
  }

  // --- Financial Calculation Helpers ---

  /**
   * Computes pairwise net balances for a specific group or across all groups.
   * Returns a map of (Pair(UserA, UserB) -> amount A owes B).
   */
  fun calculatePairwiseDebts(groupId: String? = null): List<DebtTransfer> {
    val state = _uiState.value
    val targetExpenses = if (groupId != null) {
      state.expenses.filter { it.groupId == groupId }
    } else {
      state.expenses
    }

    // Map of Pair(fromUser, toUser) -> amount
    val netBalances = mutableMapOf<Pair<String, String>, Double>()

    for (expense in targetExpenses) {
      val payerId = expense.paidByUserId
      for (split in expense.splits) {
        if (split.userId != payerId && split.amount > 0.0) {
          // split.userId owes payerId split.amount
          val key = Pair(split.userId, payerId)
          netBalances[key] = (netBalances[key] ?: 0.0) + split.amount
        }
      }
    }

    // Offset direct bilateral debts (if A owes B and B owes A)
    val processedPairs = mutableSetOf<Pair<String, String>>()
    val simplifiedTransfers = mutableListOf<DebtTransfer>()

    val allUsers = state.users.map { it.id }
    for (i in allUsers.indices) {
      for (j in i + 1 until allUsers.size) {
        val u1 = allUsers[i]
        val u2 = allUsers[j]
        val u1OwesU2 = netBalances[Pair(u1, u2)] ?: 0.0
        val u2OwesU1 = netBalances[Pair(u2, u1)] ?: 0.0

        val diff = u1OwesU2 - u2OwesU1
        if (diff > 0.01) {
          simplifiedTransfers.add(DebtTransfer(fromUserId = u1, toUserId = u2, amount = diff))
        } else if (diff < -0.01) {
          simplifiedTransfers.add(DebtTransfer(fromUserId = u2, toUserId = u1, amount = abs(diff)))
        }
      }
    }

    if (!state.isDebtSimplificationActive) {
      return simplifiedTransfers
    }

    // Multi-party Debt Simplification (Minimizing Cash Flow algorithm)
    return runDebtSimplificationAlgorithm(simplifiedTransfers)
  }

  /**
   * Pure Debt Simplification algorithm (Min Cash Flow).
   * Reduces n-party cyclic debts (Aryan->Rahul->Aman->Aryan) into minimal direct transactions.
   */
  private fun runDebtSimplificationAlgorithm(directTransfers: List<DebtTransfer>): List<DebtTransfer> {
    val netBalanceMap = mutableMapOf<String, Double>()
    for (transfer in directTransfers) {
      netBalanceMap[transfer.fromUserId] = (netBalanceMap[transfer.fromUserId] ?: 0.0) - transfer.amount
      netBalanceMap[transfer.toUserId] = (netBalanceMap[transfer.toUserId] ?: 0.0) + transfer.amount
    }

    // Positive = Creditor (should receive money), Negative = Debtor (owes money)
    val creditors = mutableListOf<Pair<String, Double>>()
    val debtors = mutableListOf<Pair<String, Double>>()

    for ((userId, balance) in netBalanceMap) {
      val rounded = (balance * 100).roundToLong() / 100.0
      if (rounded > 0.01) {
        creditors.add(Pair(userId, rounded))
      } else if (rounded < -0.01) {
        debtors.add(Pair(userId, abs(rounded)))
      }
    }

    val result = mutableListOf<DebtTransfer>()
    var cIdx = 0
    var dIdx = 0

    while (cIdx < creditors.size && dIdx < debtors.size) {
      val (creditorId, creditAmount) = creditors[cIdx]
      val (debtorId, debtAmount) = debtors[dIdx]

      val settleAmount = min(creditAmount, debtAmount)
      result.add(DebtTransfer(fromUserId = debtorId, toUserId = creditorId, amount = settleAmount))

      val remainingCredit = creditAmount - settleAmount
      val remainingDebt = debtAmount - settleAmount

      if (remainingCredit <= 0.01) {
        cIdx++
      } else {
        creditors[cIdx] = Pair(creditorId, remainingCredit)
      }

      if (remainingDebt <= 0.01) {
        dIdx++
      } else {
        debtors[dIdx] = Pair(debtorId, remainingDebt)
      }
    }

    return result
  }

  fun getOverallUserFinancials(): Triple<Double, Double, Double> {
    val currentUserId = _uiState.value.currentUser.id
    val allDebts = calculatePairwiseDebts(groupId = null)

    var youOwe = 0.0
    var youAreOwed = 0.0

    for (debt in allDebts) {
      if (debt.fromUserId == currentUserId) {
        youOwe += debt.amount
      } else if (debt.toUserId == currentUserId) {
        youAreOwed += debt.amount
      }
    }

    val netBalance = youAreOwed - youOwe
    return Triple(youOwe, youAreOwed, netBalance)
  }

  fun formatAmount(amount: Double): String {
    return String.format("%,.2f", amount)
  }
}
