package com.example.model

enum class ExpenseCategory(val displayName: String, val emoji: String) {
  FOOD("Food & Dining", "🍕"),
  TRAVEL("Travel & Transit", "🚕"),
  SHOPPING("Shopping", "🛍️"),
  HOTEL("Stay & Hotel", "🏨"),
  ENTERTAINMENT("Entertainment", "🎬"),
  BILLS("Bills & Utilities", "⚡"),
  OTHER("General / Other", "📦"),
}

enum class SplitType(val label: String) {
  EQUAL("Equal"),
  EXACT("Exact"),
  PERCENTAGE("Percentage"),
}

data class User(
  val id: String,
  val name: String,
  val email: String,
  val avatarInitials: String,
  val avatarColorHex: Long,
  val isCurrentUser: Boolean = false,
)

data class Group(
  val id: String,
  val name: String,
  val emoji: String,
  val memberIds: List<String>,
  val createdBy: String,
  val createdAt: String,
  val totalSpent: Double,
  val description: String = "",
)

data class ReceiptItem(
  val name: String,
  val amount: Double,
)

data class ReceiptExtraction(
  val merchant: String,
  val total: Double,
  val date: String,
  val items: List<ReceiptItem>,
)

data class MemberSplit(
  val userId: String,
  val amount: Double,
  val percentage: Double = 0.0,
)

data class Expense(
  val id: String,
  val groupId: String,
  val description: String,
  val totalAmount: Double,
  val paidByUserId: String,
  val category: ExpenseCategory = ExpenseCategory.FOOD,
  val splitType: SplitType = SplitType.EQUAL,
  val splits: List<MemberSplit>,
  val receiptMerchant: String? = null,
  val receiptItems: List<ReceiptItem> = emptyList(),
  val createdAt: String,
)

data class DebtTransfer(
  val fromUserId: String,
  val toUserId: String,
  val amount: Double,
)

enum class ActivityType {
  EXPENSE_ADDED,
  SETTLEMENT_DONE,
  GROUP_CREATED,
}

data class ActivityItem(
  val id: String,
  val type: ActivityType,
  val title: String,
  val subtitle: String,
  val amount: Double,
  val isUserReceiving: Boolean?,
  val timestamp: String,
  val emoji: String,
)
