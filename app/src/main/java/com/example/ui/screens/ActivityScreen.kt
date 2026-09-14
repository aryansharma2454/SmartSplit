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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.model.ActivityType
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun ActivityScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  val filterOptions = listOf("All", "Expenses", "Settlements", "Groups")

  val filteredActivities = uiState.activities.filter { item ->
    when (uiState.activityFilter) {
      "Expenses" -> item.type == ActivityType.EXPENSE_ADDED
      "Settlements" -> item.type == ActivityType.SETTLEMENT_DONE
      "Groups" -> item.type == ActivityType.GROUP_CREATED
      else -> true
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Activity",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Recent expenses, settlements and group updates",
        style = MaterialTheme.typography.bodyMedium,
        color = extColors.textMuted,
      )
    }

    // Filter Chips Row (Section 21)
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp),
      ) {
        items(filterOptions) { opt ->
          val isSelected = uiState.activityFilter == opt
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { viewModel.setActivityFilter(opt) }
              .testTag("activity_filter_$opt"),
          ) {
            Text(
              text = opt,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
          }
        }
      }
    }

    if (filteredActivities.isEmpty()) {
      item {
        EmptyStateView(
          title = "You're all caught up",
          description = "New activity will appear here as you and your friends add shared expenses.",
          buttonText = "View Groups",
          emoji = "✨",
          onButtonClick = { viewModel.setTab(com.example.viewmodel.NavigationTab.GROUPS) },
        )
      }
    } else {
      items(filteredActivities) { activity ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp)),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
              ) {
                Text(text = activity.emoji, fontSize = 20.sp)
              }

              Column {
                Text(
                  text = activity.title,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = activity.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = extColors.textMuted,
                )
                Text(
                  text = activity.timestamp,
                  style = MaterialTheme.typography.labelSmall,
                  color = extColors.textMuted,
                )
              }
            }

            if (activity.amount > 0) {
              val (color, prefix) = when (activity.isUserReceiving) {
                true -> Pair(extColors.moneyReceive, "+")
                false -> Pair(extColors.moneyOwe, "-")
                null -> Pair(MaterialTheme.colorScheme.onSurface, "")
              }
              Text(
                text = "$prefix${uiState.currencySymbol}${viewModel.formatAmount(activity.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}
