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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun ProfileScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  onShowOnboarding: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  var biometricEnabled by remember { mutableStateOf(true) }
  var notifyExpenses by remember { mutableStateOf(true) }
  var notifySettlements by remember { mutableStateOf(true) }

  val currencies = listOf("₹", "$", "€", "£")

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
        text = "Account & Settings",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
      )
    }

    // User Profile Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, extColors.cardBorder, RoundedCornerShape(20.dp)),
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          UserAvatar(user = uiState.currentUser, size = 64.dp, fontSize = 22)

          Column {
            Text(
              text = uiState.currentUser.name,
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = uiState.currentUser.email,
              style = MaterialTheme.typography.bodyMedium,
              color = extColors.textMuted,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
            ) {
              Text(
                text = "UPI: aryan@okhdfcbank",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              )
            }
          }
        }
      }
    }

    // Currency Selector (Section 22)
    item {
      SettingsSectionTitle(title = "FINANCIAL PREFERENCES")
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, extColors.cardBorder, RoundedCornerShape(18.dp)),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                text = "Default Currency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
              )
              Text(
                text = "Used for new expenses and totals",
                style = MaterialTheme.typography.bodySmall,
                color = extColors.textMuted,
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              currencies.forEach { curr ->
                val isSelected = uiState.currencySymbol == curr
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clickable { viewModel.setCurrency(curr) }
                    .testTag("currency_$curr"),
                ) {
                  Text(
                    text = curr,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  )
                }
              }
            }
          }
        }
      }
    }

    // Preferences & Appearance
    item {
      SettingsSectionTitle(title = "APP PREFERENCES")
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, extColors.cardBorder, RoundedCornerShape(18.dp)),
      ) {
        Column {
          SettingsSwitchRow(
            icon = Icons.Default.DarkMode,
            title = "Dark Appearance",
            subtitle = "Sleek low-light FinTech styling",
            checked = uiState.isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode() },
          )
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(extColors.cardBorder))
          SettingsSwitchRow(
            icon = Icons.Default.Fingerprint,
            title = "Biometric Lock",
            subtitle = "Require fingerprint/PIN on launch",
            checked = biometricEnabled,
            onCheckedChange = { biometricEnabled = it },
          )
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(extColors.cardBorder))
          SettingsSwitchRow(
            icon = Icons.Default.Notifications,
            title = "Instant Expense Alerts",
            subtitle = "Notify when group members add bills",
            checked = notifyExpenses,
            onCheckedChange = { notifyExpenses = it },
          )
        }
      }
    }

    // Help & Onboarding Revisit
    item {
      SettingsSectionTitle(title = "HELP & ONBOARDING")
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, extColors.cardBorder, RoundedCornerShape(18.dp)),
      ) {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onShowOnboarding)
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Column {
                Text("Replay Onboarding Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Review features and tips", style = MaterialTheme.typography.bodySmall, color = extColors.textMuted)
              }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = extColors.textMuted)
          }
        }
      }
    }

    // Version Tag
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "SplitFin v1.0.0",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = "Split smarter. Settle easier.",
          style = MaterialTheme.typography.bodySmall,
          color = extColors.textMuted,
        )
      }
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}

@Composable
fun SettingsSectionTitle(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.labelSmall,
    color = LocalFinTechColors.current.textMuted,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.sp,
    modifier = Modifier.padding(start = 4.dp),
  )
}

@Composable
fun SettingsSwitchRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  val extColors = LocalFinTechColors.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f),
    ) {
      Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = extColors.textMuted)
      }
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}
