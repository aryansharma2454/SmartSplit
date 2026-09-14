package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.ReceiptExtraction
import com.example.model.ReceiptItem
import com.example.ui.theme.LocalFinTechColors
import com.example.viewmodel.ExpenseUiState
import com.example.viewmodel.ExpenseViewModel

@Composable
fun ReceiptScanScreen(
  viewModel: ExpenseViewModel,
  uiState: ExpenseUiState,
  onClose: () -> Unit,
  onUseExtractedExpense: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  var isFlashOn by remember { mutableStateOf(false) }
  var isScanned by remember { mutableStateOf(false) }

  // Extracted data state
  var merchantName by remember { mutableStateOf("Domino's Pizza") }
  var totalAmount by remember { mutableStateOf("1249.00") }
  var receiptDate by remember { mutableStateOf("15 Sep 2026") }
  var itemsList by remember {
    mutableStateOf(
      listOf(
        ReceiptItem("Peppy Paneer Pizza (Large)", 799.0),
        ReceiptItem("Cheese Garlic Bread & Dip", 250.0),
        ReceiptItem("CGST + SGST (18%)", 200.0),
      ),
    )
  }

  // Scanning laser animation
  val infiniteTransition = rememberInfiniteTransition(label = "laser")
  val scanProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "laser_anim",
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A)),
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(
        onClick = onClose,
        modifier = Modifier.testTag("scan_receipt_close_btn"),
      ) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
      }

      Text(
        text = "Scan Receipt",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White,
      )

      IconButton(
        onClick = { isFlashOn = !isFlashOn },
        modifier = Modifier.testTag("scan_receipt_flash_btn"),
      ) {
        Icon(
          imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
          contentDescription = "Flash",
          tint = if (isFlashOn) Color(0xFFFBBF24) else Color.White,
        )
      }
    }

    if (!isScanned) {
      // Live Camera / Viewfinder Box
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
      ) {
        // Darkened background with camera reticle
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E293B))
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
        ) {
          // Subtle laser scan beam
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(3.dp)
              .padding(horizontal = 12.dp)
              .align(Alignment.TopCenter)
              .padding(top = (scanProgress * 360).dp)
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    Color.Transparent,
                    MaterialTheme.colorScheme.secondary,
                    Color.Transparent,
                  ),
                ),
              ),
          )

          // Center simulated receipt graphic
          Column(
            modifier = Modifier
              .align(Alignment.Center)
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.4f),
              modifier = Modifier.size(72.dp),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Position receipt within frame",
              style = MaterialTheme.typography.titleMedium,
              color = Color.White,
              fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Ensure total amount and merchant are clearly visible",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.7f),
              textAlign = TextAlign.Center,
            )
          }

          // Corner Reticles
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(12.dp)
              .size(24.dp)
              .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 8.dp)),
          )
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(12.dp)
              .size(24.dp)
              .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(topEnd = 8.dp)),
          )
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(12.dp)
              .size(24.dp)
              .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 8.dp)),
          )
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(12.dp)
              .size(24.dp)
              .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomEnd = 8.dp)),
          )
        }
      }

      // Bottom Camera Controls
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 36.dp, start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          onClick = { isScanned = true },
          modifier = Modifier
            .size(48.dp)
            .background(Color.White.copy(alpha = 0.15f), CircleShape),
        ) {
          Icon(Icons.Default.Image, contentDescription = "Gallery", tint = Color.White)
        }

        // Shutter Button
        Box(
          modifier = Modifier
            .size(76.dp)
            .border(4.dp, Color.White, CircleShape)
            .padding(6.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { isScanned = true }
            .testTag("scan_shutter_btn"),
          contentAlignment = Alignment.Center,
        ) {
          Icon(Icons.Default.CameraAlt, contentDescription = "Capture", tint = Color.White, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.size(48.dp))
      }
    } else {
      // Extracted Information Review Card (Section 16 requirement)
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
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
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = extColors.moneyReceive,
                modifier = Modifier.size(22.dp),
              )
              Text(
                text = "Receipt Detected ✓",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = extColors.moneyReceive,
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.clickable { isScanned = false },
            ) {
              Text(
                text = "Retake",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              )
            }
          }
        }

        item {
          OutlinedTextField(
            value = merchantName,
            onValueChange = { merchantName = it },
            label = { Text("Merchant") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
          )
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            OutlinedTextField(
              value = totalAmount,
              onValueChange = { totalAmount = it },
              label = { Text("Total Amount (${uiState.currencySymbol})") },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.weight(1f),
            )

            OutlinedTextField(
              value = receiptDate,
              onValueChange = { receiptDate = it },
              label = { Text("Date") },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.weight(1f),
            )
          }
        }

        item {
          Text(
            text = "Extracted Items",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
        }

        items(itemsList) { item ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(text = item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
              Text(
                text = "${uiState.currencySymbol}${viewModel.formatAmount(item.amount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              val extraction = ReceiptExtraction(
                merchant = merchantName,
                total = totalAmount.toDoubleOrNull() ?: 1249.0,
                date = receiptDate,
                items = itemsList,
              )
              viewModel.setScannedReceipt(extraction)
              onUseExtractedExpense()
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("review_expense_btn"),
          ) {
            Text(
              text = "Review & Add Expense",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
            )
          }
        }
      }
    }
  }
}
