package com.example.data

import com.example.model.ActivityItem
import com.example.model.ActivityType
import com.example.model.Expense
import com.example.model.ExpenseCategory
import com.example.model.Group
import com.example.model.MemberSplit
import com.example.model.ReceiptExtraction
import com.example.model.ReceiptItem
import com.example.model.SplitType
import com.example.model.User

object SampleData {
  val currentUser = User(
    id = "u1",
    name = "Aryan Raj",
    email = "aryan@example.com",
    avatarInitials = "AR",
    avatarColorHex = 0xFF4338CA,
    isCurrentUser = true,
  )

  val users = listOf(
    currentUser,
    User(
      id = "u2",
      name = "Rahul Sharma",
      email = "rahul@example.com",
      avatarInitials = "RS",
      avatarColorHex = 0xFF059669,
    ),
    User(
      id = "u3",
      name = "Aman Verma",
      email = "aman@example.com",
      avatarInitials = "AV",
      avatarColorHex = 0xFFD97706,
    ),
    User(
      id = "u4",
      name = "Priya Singh",
      email = "priya@example.com",
      avatarInitials = "PS",
      avatarColorHex = 0xFFDB2777,
    ),
    User(
      id = "u5",
      name = "Rohit Mehta",
      email = "rohit@example.com",
      avatarInitials = "RM",
      avatarColorHex = 0xFF0284C7,
    ),
    User(
      id = "u6",
      name = "Sneha Patel",
      email = "sneha@example.com",
      avatarInitials = "SP",
      avatarColorHex = 0xFF7C3AED,
    ),
  )

  val initialGroups = listOf(
    Group(
      id = "g1",
      name = "Goa Trip",
      emoji = "🏝️",
      memberIds = listOf("u1", "u2", "u3", "u4", "u5", "u6"),
      createdBy = "u1",
      createdAt = "10 Sep 2026",
      totalSpent = 18450.0,
      description = "South Goa beach villa, scooty rentals & shack dinners",
    ),
    Group(
      id = "g2",
      name = "Flatmates 402",
      emoji = "🏠",
      memberIds = listOf("u1", "u2", "u3"),
      createdBy = "u2",
      createdAt = "01 Aug 2026",
      totalSpent = 34200.0,
      description = "Groceries, electricity, high-speed WiFi, cook salary",
    ),
    Group(
      id = "g3",
      name = "Weekend Trek",
      emoji = "⛰️",
      memberIds = listOf("u1", "u4", "u5"),
      createdBy = "u4",
      createdAt = "02 Sep 2026",
      totalSpent = 8600.0,
      description = "Kalsubai sunrise trek gear, cabs & breakfast",
    ),
    Group(
      id = "g4",
      name = "Dinner Club",
      emoji = "🍕",
      memberIds = listOf("u1", "u2", "u3", "u4", "u5"),
      createdBy = "u3",
      createdAt = "28 Aug 2026",
      totalSpent = 9800.0,
      description = "Weekly Friday food explorations & artisanal pizza nights",
    ),
  )

  val initialExpenses = listOf(
    Expense(
      id = "e1",
      groupId = "g1",
      description = "Beach Shack Seafood Dinner",
      totalAmount = 1850.0,
      paidByUserId = "u2", // Rahul paid
      category = ExpenseCategory.FOOD,
      splitType = SplitType.EQUAL,
      splits = listOf(
        MemberSplit("u1", 308.33),
        MemberSplit("u2", 308.33),
        MemberSplit("u3", 308.33),
        MemberSplit("u4", 308.33),
        MemberSplit("u5", 308.34),
        MemberSplit("u6", 308.34),
      ),
      receiptMerchant = "Fisherman's Wharf Shack",
      receiptItems = listOf(
        ReceiptItem("Grilled Kingfish", 850.0),
        ReceiptItem("Butter Garlic Prawns", 650.0),
        ReceiptItem("Fresh Lime Sodas (x4)", 350.0),
      ),
      createdAt = "Today, 8:30 PM",
    ),
    Expense(
      id = "e2",
      groupId = "g1",
      description = "Airport Taxi & Tolls",
      totalAmount = 640.0,
      paidByUserId = "u1", // Aryan paid
      category = ExpenseCategory.TRAVEL,
      splitType = SplitType.EQUAL,
      splits = listOf(
        MemberSplit("u1", 160.0),
        MemberSplit("u2", 160.0),
        MemberSplit("u3", 160.0),
        MemberSplit("u4", 160.0),
      ),
      createdAt = "Today, 3:15 PM",
    ),
    Expense(
      id = "e3",
      groupId = "g1",
      description = "Domino's Late Night Pizza",
      totalAmount = 1249.0,
      paidByUserId = "u1", // Aryan paid
      category = ExpenseCategory.FOOD,
      splitType = SplitType.EQUAL,
      splits = listOf(
        MemberSplit("u1", 312.25),
        MemberSplit("u2", 312.25),
        MemberSplit("u3", 312.25),
        MemberSplit("u4", 312.25),
      ),
      receiptMerchant = "Domino's Pizza",
      receiptItems = listOf(
        ReceiptItem("Peppy Paneer Pizza (Large)", 799.0),
        ReceiptItem("Cheese Garlic Bread & Dip", 250.0),
        ReceiptItem("GST & Delivery Fee", 200.0),
      ),
      createdAt = "Yesterday",
    ),
    Expense(
      id = "e4",
      groupId = "g1",
      description = "Private Villa 2 Nights Booking",
      totalAmount = 12000.0,
      paidByUserId = "u4", // Priya paid
      category = ExpenseCategory.HOTEL,
      splitType = SplitType.EQUAL,
      splits = listOf(
        MemberSplit("u1", 2000.0),
        MemberSplit("u2", 2000.0),
        MemberSplit("u3", 2000.0),
        MemberSplit("u4", 2000.0),
        MemberSplit("u5", 2000.0),
        MemberSplit("u6", 2000.0),
      ),
      createdAt = "12 Sep 2026",
    ),
    Expense(
      id = "e5",
      groupId = "g2",
      description = "Monthly Fiber Broadband",
      totalAmount = 1499.0,
      paidByUserId = "u1", // Aryan paid
      category = ExpenseCategory.BILLS,
      splitType = SplitType.EQUAL,
      splits = listOf(
        MemberSplit("u1", 499.66),
        MemberSplit("u2", 499.67),
        MemberSplit("u3", 499.67),
      ),
      createdAt = "08 Sep 2026",
    ),
  )

  val sampleReceiptExtraction = ReceiptExtraction(
    merchant = "Domino's Pizza",
    total = 1249.0,
    date = "15 Sep 2026",
    items = listOf(
      ReceiptItem("Peppy Paneer Pizza (Large)", 799.0),
      ReceiptItem("Cheese Garlic Bread & Dip", 250.0),
      ReceiptItem("CGST + SGST (18%)", 200.0),
    ),
  )

  val initialActivities = listOf(
    ActivityItem(
      id = "a1",
      type = ActivityType.EXPENSE_ADDED,
      title = "Rahul added Beach Shack Dinner",
      subtitle = "Goa Trip • You owe ₹308",
      amount = 1850.0,
      isUserReceiving = false,
      timestamp = "Just now",
      emoji = "🍕",
    ),
    ActivityItem(
      id = "a2",
      type = ActivityType.EXPENSE_ADDED,
      title = "You added Airport Taxi",
      subtitle = "Goa Trip • You receive ₹160",
      amount = 640.0,
      isUserReceiving = true,
      timestamp = "4 hours ago",
      emoji = "🚕",
    ),
    ActivityItem(
      id = "a3",
      type = ActivityType.SETTLEMENT_DONE,
      title = "You settled with Aman",
      subtitle = "Flatmates 402 • Marked as Paid",
      amount = 300.0,
      isUserReceiving = null,
      timestamp = "Yesterday",
      emoji = "🤝",
    ),
    ActivityItem(
      id = "a4",
      type = ActivityType.EXPENSE_ADDED,
      title = "Priya added Villa Booking",
      subtitle = "Goa Trip • You owe ₹2,000",
      amount = 12000.0,
      isUserReceiving = false,
      timestamp = "2 days ago",
      emoji = "🏨",
    ),
    ActivityItem(
      id = "a5",
      type = ActivityType.SETTLEMENT_DONE,
      title = "Rohit paid you",
      subtitle = "Weekend Trek • Received via UPI",
      amount = 400.0,
      isUserReceiving = true,
      timestamp = "3 days ago",
      emoji = "💰",
    ),
  )
}
