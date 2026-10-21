package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DietaryType
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun SectionHeader(
  eyebrow: String,
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(MutedChampagne)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = eyebrow.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MutedChampagne,
        letterSpacing = 1.8.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(12.dp))
      Box(
        modifier = Modifier
          .height(1.dp)
          .weight(1f)
          .background(WarmBorderDark)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.headlineLarge,
      color = PorcelainIvory
    )
    if (subtitle != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MutedLightText
      )
    }
  }
}

@Composable
fun DietaryBadge(
  dietaryType: DietaryType,
  modifier: Modifier = Modifier
) {
  val (color, label) = when (dietaryType) {
    DietaryType.VEGETARIAN -> Pair(Color(0xFF4CAF50), "VEG")
    DietaryType.NON_VEGETARIAN -> Pair(Color(0xFFE53935), "NON-VEG")
    DietaryType.VEGAN -> Pair(Color(0xFF81C784), "VEGAN")
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(4.dp),
    color = CharcoalElevated,
    border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun PriceText(
  price: Double,
  compareAtPrice: Double? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.Bottom
  ) {
    Text(
      text = "₹${String.format("%,.0f", price)}",
      style = MaterialTheme.typography.titleMedium,
      color = MutedChampagne,
      fontWeight = FontWeight.Bold
    )
    if (compareAtPrice != null && compareAtPrice > price) {
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "₹${String.format("%,.0f", compareAtPrice)}",
        style = MaterialTheme.typography.bodySmall,
        color = MutedSecondaryText,
        textDecoration = TextDecoration.LineThrough
      )
    }
  }
}

@Composable
fun StatusPill(
  status: OrderStatus,
  modifier: Modifier = Modifier
) {
  val (bg, fg) = when (status) {
    OrderStatus.PLACED -> Pair(CharcoalElevated, MutedChampagne)
    OrderStatus.ACCEPTED -> Pair(Color(0xFF2E2B23), GoldAccent)
    OrderStatus.PREPARING -> Pair(Color(0xFF382A1E), RefinedTerracotta)
    OrderStatus.READY -> Pair(Color(0xFF1E3324), SageGreen)
    OrderStatus.OUT_FOR_DELIVERY -> Pair(Color(0xFF1E2835), Color(0xFF64B5F6))
    OrderStatus.DELIVERED -> Pair(Color(0xFF1E3324), Color(0xFF81C784))
    OrderStatus.CANCELLED -> Pair(Color(0xFF331E1E), Color(0xFFE57373))
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = bg,
    border = BorderStroke(1.dp, fg.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(fg)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = status.displayName,
        style = MaterialTheme.typography.labelSmall,
        color = fg,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun LuxuryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isSecondary: Boolean = false
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp)
      .testTag("luxury_button"),
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isSecondary) CharcoalElevated else RefinedTerracotta,
      contentColor = if (isSecondary) MutedChampagne else PaperWhite,
      disabledContainerColor = CharcoalElevated.copy(alpha = 0.5f),
      disabledContentColor = MutedSecondaryText
    ),
    border = if (isSecondary) BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.6f)) else null,
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun DishCanvasIllustration(
  productId: String,
  modifier: Modifier = Modifier
) {
  // Artistic procedural luxury culinary rendering
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(
        Brush.radialGradient(
          colors = listOf(CharcoalElevated, ObsidianCharcoal)
        )
      )
      .border(1.dp, WarmBorderDark, RoundedCornerShape(12.dp))
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = h / 2f

      // Rim of fine ceramic platter
      drawCircle(
        color = Color(0xFF282522),
        radius = (w.coerceAtMost(h) * 0.44f),
        center = Offset(cx, cy)
      )
      drawCircle(
        color = AntiqueBrass.copy(alpha = 0.25f),
        radius = (w.coerceAtMost(h) * 0.43f),
        center = Offset(cx, cy),
        style = Stroke(width = 2.dp.toPx())
      )

      // Inner dish center
      drawCircle(
        color = Color(0xFF1E1C1A),
        radius = (w.coerceAtMost(h) * 0.38f),
        center = Offset(cx, cy)
      )

      when {
        productId.contains("biryani") -> {
          // Warm saffron golden rice texture & spices
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFE5A93C), Color(0xFFB5701B)),
              center = Offset(cx, cy),
              radius = w * 0.35f
            ),
            radius = w * 0.32f,
            center = Offset(cx, cy)
          )
          // Roasted shallot & herb accents
          drawCircle(color = Color(0xFF5A2A18), radius = 6.dp.toPx(), center = Offset(cx - 15, cy - 10))
          drawCircle(color = Color(0xFF5A2A18), radius = 8.dp.toPx(), center = Offset(cx + 20, cy + 12))
          drawCircle(color = Color(0xFF437537), radius = 4.dp.toPx(), center = Offset(cx + 5, cy - 20))
          drawCircle(color = Color(0xFFF7F3EC), radius = 5.dp.toPx(), center = Offset(cx - 10, cy + 18))
        }
        productId.contains("pasta") -> {
          // Creamy truffle ribbons
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFDCC8A8), Color(0xFF9E8A68)),
              center = Offset(cx, cy),
              radius = w * 0.34f
            ),
            radius = w * 0.30f,
            center = Offset(cx, cy)
          )
          // Wild dark morel accents
          drawArc(
            color = Color(0xFF332A24),
            startAngle = 30f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - 25, cy - 25),
            size = Size(50f, 50f),
            style = Stroke(width = 12f)
          )
          drawCircle(color = Color(0xFF221A15), radius = 8.dp.toPx(), center = Offset(cx + 10, cy - 10))
          drawCircle(color = Color(0xFF4A7C59), radius = 4.dp.toPx(), center = Offset(cx - 15, cy + 15))
        }
        productId.contains("burger") -> {
          // Golden toasted brioche & smash patties
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFD99036), Color(0xFF7A4110)),
              center = Offset(cx, cy),
              radius = w * 0.35f
            ),
            radius = w * 0.32f,
            center = Offset(cx, cy)
          )
          drawCircle(color = Color(0xFFE5B020), radius = 10.dp.toPx(), center = Offset(cx + 18, cy + 5))
          drawCircle(color = Color(0xFF3B1E08), radius = 12.dp.toPx(), center = Offset(cx - 8, cy - 5))
        }
        productId.contains("avocado") || productId.contains("bowl") -> {
          // Vibrant greens & quinoa grains
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFF7A9A60), Color(0xFF436030)),
              center = Offset(cx, cy),
              radius = w * 0.35f
            ),
            radius = w * 0.32f,
            center = Offset(cx, cy)
          )
          drawCircle(color = Color(0xFFB4C873), radius = 12.dp.toPx(), center = Offset(cx - 18, cy - 10))
          drawCircle(color = Color(0xFFE56B55), radius = 8.dp.toPx(), center = Offset(cx + 16, cy + 14))
          drawCircle(color = Color(0xFFDCC8A8), radius = 7.dp.toPx(), center = Offset(cx + 4, cy - 18))
        }
        productId.contains("mousse") || productId.contains("chocolate") -> {
          // Deep Valrhona dark gloss & gold flake
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFF3D2314), Color(0xFF1A0E07)),
              center = Offset(cx, cy),
              radius = w * 0.35f
            ),
            radius = w * 0.30f,
            center = Offset(cx, cy)
          )
          // Gold leaf sparkle
          drawCircle(color = GoldAccent, radius = 4.dp.toPx(), center = Offset(cx, cy - 5))
          drawCircle(color = Color(0xFFFAF6EE), radius = 5.dp.toPx(), center = Offset(cx + 12, cy + 12))
        }
        else -> {
          // Signature cloche insignia
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(MutedChampagne.copy(alpha = 0.5f), Color(0xFF282522)),
              center = Offset(cx, cy),
              radius = w * 0.30f
            ),
            radius = w * 0.28f,
            center = Offset(cx, cy)
          )
          drawCircle(color = GoldAccent, radius = 5.dp.toPx(), center = Offset(cx, cy))
        }
      }
    }
  }
}
