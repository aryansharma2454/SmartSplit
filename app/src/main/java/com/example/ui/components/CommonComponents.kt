package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Expense
import com.example.model.Group
import com.example.model.User
import com.example.ui.theme.LocalFinTechColors

@Composable
fun UserAvatar(
  user: User,
  size: Dp = 40.dp,
  fontSize: Int = 13,
  modifier: Modifier = Modifier,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(user.avatarColorHex)),
  ) {
    Text(
      text = user.avatarInitials,
      color = Color.White,
      fontSize = fontSize.sp,
      fontWeight = FontWeight.Bold,
    )
  }
}

@Composable
fun AvatarStack(
  users: List<User>,
  maxCount: Int = 4,
  avatarSize: Dp = 28.dp,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val displayList = users.take(maxCount)
    displayList.forEachIndexed { index, user ->
      Box(
        modifier = Modifier
          .size(avatarSize)
          .padding(start = if (index == 0) 0.dp else 4.dp)
          .clip(CircleShape)
          .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
          .background(Color(user.avatarColorHex)),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = user.avatarInitials,
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
        )
      }
    }
    if (users.size > maxCount) {
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "+${users.size - maxCount}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun FinancialBadge(
  amount: Double,
  currency: String = "₹",
  isReceiving: Boolean?,
  prefixText: String? = null,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current

  val (bgColor, textColor, icon) = when {
    isReceiving == true -> Triple(
      extColors.moneyReceiveBg,
      extColors.moneyReceive,
      Icons.Default.ArrowUpward,
    )
    isReceiving == false -> Triple(
      extColors.moneyOweBg,
      extColors.moneyOwe,
      Icons.Default.ArrowDownward,
    )
    else -> Triple(
      extColors.moneySettledBg,
      extColors.moneySettled,
      Icons.Default.CheckCircle,
    )
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(12.dp),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(12.dp),
      )
      val formattedAmount = String.format("%,.2f", amount)
      val sign = if (isReceiving == true) "+" else if (isReceiving == false) "-" else ""
      Text(
        text = "${prefixText?.let { "$it " } ?: ""}$sign$currency$formattedAmount",
        color = textColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun GroupCard(
  group: Group,
  members: List<User>,
  userBalanceInGroup: Double,
  currency: String = "₹",
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = extColors.cardBackground,
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .border(1.dp, extColors.cardBorder, RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .testTag("group_card_${group.id}"),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
          ) {
            Text(text = group.emoji, fontSize = 22.sp)
          }
          Column {
            Text(
              text = group.name,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "${group.memberIds.size} members",
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted,
            )
          }
        }

        AvatarStack(users = members)
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
      ) {
        Column {
          Text(
            text = "Total spent",
            style = MaterialTheme.typography.labelSmall,
            color = extColors.textMuted,
          )
          Text(
            text = "$currency${String.format("%,.0f", group.totalSpent)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }

        // User balance indicator
        when {
          userBalanceInGroup > 0.01 -> {
            FinancialBadge(
              amount = userBalanceInGroup,
              currency = currency,
              isReceiving = true,
              prefixText = "You receive",
            )
          }
          userBalanceInGroup < -0.01 -> {
            FinancialBadge(
              amount = kotlin.math.abs(userBalanceInGroup),
              currency = currency,
              isReceiving = false,
              prefixText = "You owe",
            )
          }
          else -> {
            Surface(
              color = extColors.moneySettledBg,
              shape = RoundedCornerShape(12.dp),
            ) {
              Text(
                text = "Settled up",
                color = extColors.moneySettled,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ExpenseCard(
  expense: Expense,
  payerName: String,
  groupName: String,
  userShareAmount: Double,
  isUserPayer: Boolean,
  currency: String = "₹",
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = extColors.cardBackground,
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("expense_card_${expense.id}"),
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
          Text(text = expense.category.emoji, fontSize = 20.sp)
        }

        Column {
          Text(
            text = expense.description,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$groupName • Paid by $payerName",
            style = MaterialTheme.typography.bodySmall,
            color = extColors.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          if (expense.receiptMerchant != null) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(top = 2.dp),
            ) {
              Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = "Receipt",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp),
              )
              Text(
                text = "Scanned Receipt",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
              )
            }
          }
        }
      }

      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = "$currency${String.format("%,.2f", expense.totalAmount)}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )

        // What Aryan owes or receives for this single expense
        if (isUserPayer) {
          val othersOwe = expense.totalAmount - userShareAmount
          Text(
            text = "You receive $currency${String.format("%,.2f", othersOwe)}",
            style = MaterialTheme.typography.labelSmall,
            color = extColors.moneyReceive,
            fontWeight = FontWeight.SemiBold,
          )
        } else {
          Text(
            text = "You owe $currency${String.format("%,.2f", userShareAmount)}",
            style = MaterialTheme.typography.labelSmall,
            color = extColors.moneyOwe,
            fontWeight = FontWeight.SemiBold,
          )
        }
      }
    }
  }
}

@Composable
fun ShimmerCard(
  height: Dp = 100.dp,
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition(label = "shimmer")
  val translateAnim by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "shimmer_translate",
  )

  val shimmerColors = listOf(
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
  )

  val brush = Brush.linearGradient(
    colors = shimmerColors,
    start = Offset.Zero,
    end = Offset(x = translateAnim, y = translateAnim),
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(18.dp))
      .background(brush),
  )
}

@Composable
fun EmptyStateView(
  title: String,
  description: String,
  buttonText: String,
  emoji: String = "👥",
  onButtonClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center,
    ) {
      Text(text = emoji, fontSize = 36.sp)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      color = LocalFinTechColors.current.textMuted,
      textAlign = TextAlign.Center,
      lineHeight = 20.sp,
    )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = onButtonClick,
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
      ),
      modifier = Modifier
        .height(48.dp)
        .testTag("empty_state_action_button"),
    ) {
      Text(
        text = buttonText,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun DebtTransferFlowCard(
  fromUser: User,
  toUser: User,
  amount: Double,
  currency: String = "₹",
  onSettleClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = extColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f),
      ) {
        UserAvatar(user = fromUser, size = 34.dp)
        Text(
          text = if (fromUser.isCurrentUser) "You" else fromUser.name.split(" ").first(),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "owes",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp),
        )

        UserAvatar(user = toUser, size = 34.dp)
        Text(
          text = if (toUser.isCurrentUser) "You" else toUser.name.split(" ").first(),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(
          text = "$currency${String.format("%,.2f", amount)}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )

        OutlinedButton(
          onClick = onSettleClick,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.height(36.dp),
        ) {
          Text("Settle", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
