package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalFinTechColors

data class OnboardingSlide(
  val title: String,
  val description: String,
  val emoji: String,
)

@Composable
fun OnboardingScreen(
  onFinish: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val extColors = LocalFinTechColors.current
  var currentSlide by remember { mutableIntStateOf(0) }

  val slides = listOf(
    OnboardingSlide(
      title = "Split expenses instantly",
      description = "Add bills, choose who participated, and SplitFin calculates fair shares immediately with zero math stress.",
      emoji = "💳",
    ),
    OnboardingSlide(
      title = "Settle debts with clarity",
      description = "No awkward money talks. See clear bilateral transfers, simplified routes, and one-tap settlements.",
      emoji = "🤝",
    ),
    OnboardingSlide(
      title = "Smart receipt scanning",
      description = "Snap a restaurant or grocery receipt and let our AI scanner extract items and totals directly into your bill.",
      emoji = "🧾",
    ),
  )

  val activeSlide = slides[currentSlide]

  Surface(
    modifier = modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background,
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      // Top Skip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        if (currentSlide < slides.size - 1) {
          TextButton(onClick = onFinish) {
            Text("Skip", style = MaterialTheme.typography.labelLarge, color = extColors.textMuted)
          }
        } else {
          Spacer(modifier = Modifier.height(48.dp))
        }
      }

      // Center Slide content
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp),
      ) {
        Box(
          modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center,
        ) {
          Text(text = activeSlide.emoji, fontSize = 56.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
          text = activeSlide.title,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = activeSlide.description,
          style = MaterialTheme.typography.bodyLarge,
          color = extColors.textMuted,
          textAlign = TextAlign.Center,
          lineHeight = 24.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Slide Indicators
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          slides.indices.forEach { index ->
            val isSelected = currentSlide == index
            Box(
              modifier = Modifier
                .height(8.dp)
                .clip(CircleShape)
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                )
                .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp),
            )
          }
        }
      }

      // Bottom Button
      Button(
        onClick = {
          if (currentSlide < slides.size - 1) {
            currentSlide++
          } else {
            onFinish()
          }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("onboarding_next_btn"),
      ) {
        Text(
          text = if (currentSlide == slides.size - 1) "Get Started" else "Next",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
        )
      }
    }
  }
}
