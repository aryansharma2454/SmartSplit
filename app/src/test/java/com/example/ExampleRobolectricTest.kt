package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.ExpenseViewModel
import com.example.model.SplitType
import com.example.model.ExpenseCategory
import com.example.model.MemberSplit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SplitFin", appName)
  }

  @Test
  fun `verify debt simplification algorithm and financial balances`() {
    val viewModel = ExpenseViewModel()
    val (youOwe, youAreOwed, netBalance) = viewModel.getOverallUserFinancials()

    assertTrue("User net balance should be computed", netBalance != 0.0)
    assertEquals(netBalance, youAreOwed - youOwe, 0.01)

    val debts = viewModel.calculatePairwiseDebts(groupId = "g1")
    assertNotNull(debts)
  }

  @Test
  fun `verify expense creation updates state`() {
    val viewModel = ExpenseViewModel()
    val initialExpenseCount = viewModel.uiState.value.expenses.size

    viewModel.addExpense(
      groupId = "g1",
      description = "Test Coffee",
      totalAmount = 200.0,
      paidByUserId = "u1",
      category = ExpenseCategory.FOOD,
      splitType = SplitType.EQUAL,
      memberSplits = listOf(
        MemberSplit("u1", 100.0),
        MemberSplit("u2", 100.0),
      ),
    )

    assertEquals(initialExpenseCount + 1, viewModel.uiState.value.expenses.size)
    assertEquals("Expense added successfully! 💸", viewModel.uiState.value.celebrationMessage)
  }
}

