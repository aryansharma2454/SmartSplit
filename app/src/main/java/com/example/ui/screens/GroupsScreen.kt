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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Group
import com.example.model.User
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GroupCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun GroupsScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  onSelectGroup: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  var showCreateGroupDialog by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "Your Groups",
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Keep every shared expense organized.",
              style = MaterialTheme.typography.bodyMedium,
              color = extColors.textMuted,
            )
          }

          Button(
            onClick = { showCreateGroupDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("create_group_header_btn"),
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(4.dp))
            Text("Create", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      if (uiState.groups.isEmpty()) {
        item {
          EmptyStateView(
            title = "No groups yet",
            description = "Create your first group and start splitting expenses effortlessly.",
            buttonText = "Create Group",
            onButtonClick = { showCreateGroupDialog = true },
          )
        }
      } else {
        items(uiState.groups) { group ->
          val members = uiState.users.filter { group.memberIds.contains(it.id) }
          val groupDebts = viewModel.calculatePairwiseDebts(groupId = group.id)
          var groupBal = 0.0
          for (d in groupDebts) {
            if (d.toUserId == uiState.currentUser.id) groupBal += d.amount
            if (d.fromUserId == uiState.currentUser.id) groupBal -= d.amount
          }

          GroupCard(
            group = group,
            members = members,
            userBalanceInGroup = groupBal,
            currency = uiState.currencySymbol,
            onClick = { onSelectGroup(group.id) },
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(90.dp))
      }
    }

    // Floating button to also create group easily
    FloatingActionButton(
      onClick = { showCreateGroupDialog = true },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 90.dp)
        .testTag("create_group_fab"),
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create Group")
    }
  }

  if (showCreateGroupDialog) {
    CreateGroupDialog(
      allUsers = uiState.users,
      currentUser = uiState.currentUser,
      onDismiss = { showCreateGroupDialog = false },
      onCreate = { name, emoji, memberIds, desc ->
        viewModel.createGroup(name, emoji, memberIds, desc)
        showCreateGroupDialog = false
      },
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGroupDialog(
  allUsers: List<User>,
  currentUser: User,
  onDismiss: () -> Unit,
  onCreate: (name: String, emoji: String, memberIds: List<String>, desc: String) -> Unit,
) {
  var groupName by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedEmoji by remember { mutableStateOf("🏝️") }
  val selectedMembers = remember { mutableStateListOf(currentUser.id, "u2", "u3") }

  val popularEmojis = listOf("🏝️", "🏠", "🍕", "🚗", "✈️", "☕", "🎉", "🎮")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .testTag("create_group_dialog"),
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Create Group",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        // Emoji picker
        Text(text = "Choose Icon", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          popularEmojis.forEach { emoji ->
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selectedEmoji == emoji) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                .border(
                  width = if (selectedEmoji == emoji) 2.dp else 0.dp,
                  color = if (selectedEmoji == emoji) MaterialTheme.colorScheme.primary else Color.Transparent,
                  shape = RoundedCornerShape(10.dp),
                )
                .clickable { selectedEmoji = emoji },
              contentAlignment = Alignment.Center,
            ) {
              Text(text = emoji, fontSize = 20.sp)
            }
          }
        }

        OutlinedTextField(
          value = groupName,
          onValueChange = { groupName = it },
          label = { Text("Group Name (e.g. Goa Trip)") },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("group_name_input"),
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description (Optional)") },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth(),
        )

        Text(
          text = "Select Members (${selectedMembers.size})",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          allUsers.forEach { user ->
            val isSelected = selectedMembers.contains(user.id)
            val isCurrent = user.id == currentUser.id

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
              ),
              modifier = Modifier
                .clickable(enabled = !isCurrent) {
                  if (isSelected) selectedMembers.remove(user.id) else selectedMembers.add(user.id)
                }
                .testTag("member_chip_${user.id}"),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                UserAvatar(user = user, size = 20.dp, fontSize = 9)
                Text(
                  text = if (isCurrent) "You (${user.name.split(" ").first()})" else user.name.split(" ").first(),
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            if (groupName.isNotBlank()) {
              onCreate(groupName.trim(), selectedEmoji, selectedMembers.toList(), description.trim())
            }
          },
          enabled = groupName.isNotBlank(),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("submit_create_group_btn"),
        ) {
          Text("Create Group", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }
}
