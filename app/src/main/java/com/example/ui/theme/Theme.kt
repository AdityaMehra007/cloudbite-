package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = MutedChampagne,
  onPrimary = ObsidianCharcoal,
  primaryContainer = CharcoalElevated,
  onPrimaryContainer = MutedChampagne,
  secondary = AntiqueBrass,
  onSecondary = PaperWhite,
  secondaryContainer = CharcoalElevated,
  onSecondaryContainer = WarmBorder,
  tertiary = RefinedTerracotta,
  onTertiary = PaperWhite,
  background = ObsidianCharcoal,
  onBackground = PorcelainIvory,
  surface = CharcoalSurface,
  onSurface = PorcelainIvory,
  surfaceVariant = CharcoalElevated,
  onSurfaceVariant = MutedLightText,
  outline = WarmBorderDark,
  outlineVariant = WarmBorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = DeepEspresso,
  onPrimary = PorcelainIvory,
  primaryContainer = WarmCream,
  onPrimaryContainer = DeepEspresso,
  secondary = AntiqueBrass,
  onSecondary = PaperWhite,
  secondaryContainer = PorcelainIvory,
  onSecondaryContainer = DeepEspresso,
  tertiary = RefinedTerracotta,
  onTertiary = PaperWhite,
  background = PorcelainIvory,
  onBackground = DeepEspresso,
  surface = PaperWhite,
  onSurface = DeepEspresso,
  surfaceVariant = WarmCream,
  onSurfaceVariant = MutedSecondaryText,
  outline = WarmBorder,
  outlineVariant = WarmBorder
)

@Composable
fun CloudBiteTheme(
  darkTheme: Boolean = true, // Default to Black Label luxury dark aesthetic
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = CloudBiteTypography,
    content = content
  )
}
