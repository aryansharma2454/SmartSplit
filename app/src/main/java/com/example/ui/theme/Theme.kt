package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedFinTechColors(
  val moneyReceive: Color,
  val moneyReceiveBg: Color,
  val moneyOwe: Color,
  val moneyOweBg: Color,
  val moneySettled: Color,
  val moneySettledBg: Color,
  val cardBackground: Color,
  val cardBorder: Color,
  val textMuted: Color,
)

val LocalFinTechColors = staticCompositionLocalOf {
  ExtendedFinTechColors(
    moneyReceive = MoneyReceiveGreen,
    moneyReceiveBg = MoneyReceiveGreenBg,
    moneyOwe = MoneyOweRed,
    moneyOweBg = MoneyOweRedBg,
    moneySettled = MoneySettledGray,
    moneySettledBg = MoneySettledBg,
    cardBackground = LightSurfaceCard,
    cardBorder = LightBorder,
    textMuted = LightTextSecondary,
  )
}

private val FinTechDarkColorScheme = darkColorScheme(
  primary = PrimaryIndigoLight,
  onPrimary = Color.White,
  primaryContainer = PrimaryIndigoContainerDark,
  onPrimaryContainer = Color(0xFFC7D2FE),
  secondary = EmeraldFinTechLight,
  onSecondary = Color.Black,
  secondaryContainer = EmeraldContainerDark,
  onSecondaryContainer = Color(0xFFA7F3D0),
  tertiary = AccentSky,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkBorder,
  error = MoneyOweRedLight,
  onError = Color.Black,
)

private val FinTechLightColorScheme = lightColorScheme(
  primary = PrimaryIndigo,
  onPrimary = Color.White,
  primaryContainer = PrimaryIndigoContainerLight,
  onPrimaryContainer = PrimaryIndigoDark,
  secondary = EmeraldFinTech,
  onSecondary = Color.White,
  secondaryContainer = EmeraldContainerLight,
  onSecondaryContainer = EmeraldFinTechDark,
  tertiary = AccentSky,
  background = LightBackground,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  outline = LightBorder,
  error = MoneyOweRed,
  onError = Color.White,
)

@Composable
fun SplitFinTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) FinTechDarkColorScheme else FinTechLightColorScheme

  val extendedColors = if (darkTheme) {
    ExtendedFinTechColors(
      moneyReceive = MoneyReceiveGreenLight,
      moneyReceiveBg = MoneyReceiveGreenBgDark,
      moneyOwe = MoneyOweRedLight,
      moneyOweBg = MoneyOweRedBgDark,
      moneySettled = MoneySettledGray,
      moneySettledBg = Color(0xFF1E293B),
      cardBackground = DarkSurfaceCard,
      cardBorder = DarkBorder,
      textMuted = DarkTextSecondary,
    )
  } else {
    ExtendedFinTechColors(
      moneyReceive = MoneyReceiveGreen,
      moneyReceiveBg = MoneyReceiveGreenBg,
      moneyOwe = MoneyOweRed,
      moneyOweBg = MoneyOweRedBg,
      moneySettled = MoneySettledGray,
      moneySettledBg = MoneySettledBg,
      cardBackground = LightSurfaceCard,
      cardBorder = LightBorder,
      textMuted = LightTextSecondary,
    )
  }

  CompositionLocalProvider(LocalFinTechColors provides extendedColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content,
    )
  }
}
