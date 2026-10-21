package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlayfairFontFamily = FontFamily(
  Font(R.font.playfair_display, FontWeight.Normal)
)

val ManropeFontFamily = FontFamily(
  Font(R.font.manrope, FontWeight.Normal)
)

val CloudBiteTypography = Typography(
  displayLarge = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp
  ),
  displayMedium = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.25).sp
  ),
  displaySmall = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp
  ),
  headlineLarge = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp
  ),
  headlineMedium = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp
  ),
  headlineSmall = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 24.sp
  ),
  titleLarge = TextStyle(
    fontFamily = PlayfairFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.15.sp
  ),
  titleMedium = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
  ),
  titleSmall = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.1.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.25.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 19.sp,
    letterSpacing = 0.2.sp
  ),
  bodySmall = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
  ),
  labelLarge = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
  ),
  labelMedium = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  ),
  labelSmall = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.5.sp
  )
)
