package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModifierOption
import com.example.data.model.Product
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
  product: Product,
  onDismiss: () -> Unit,
  onAddToCart: (quantity: Int, modifiers: Map<String, ModifierOption>, notes: String) -> Unit
) {
  var quantity by remember { mutableStateOf(1) }
  var specialInstructions by remember { mutableStateOf("") }
  val selectedModifiers = remember {
    mutableStateMapOf<String, ModifierOption>().apply {
      // Pre-select first option for required groups
      product.modifierGroups.filter { it.required }.forEach { group ->
        if (group.options.isNotEmpty()) {
          put(group.id, group.options.first())
        }
      }
    }
  }

  val baseUnit = product.price + selectedModifiers.values.sumOf { it.priceDelta }
  val calculatedTotal = baseUnit * quantity

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = CharcoalSurface,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, bottom = 4.dp)
          .width(40.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(WarmBorderDark)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header with close button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        DietaryBadge(dietaryType = product.dietaryType)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = PorcelainIvory)
        }
      }

      // Large Dish Visual
      DishCanvasIllustration(
        productId = product.id,
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = product.brandName.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = AntiqueBrass,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = product.name,
        style = MaterialTheme.typography.headlineMedium,
        color = PorcelainIvory
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = product.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MutedLightText
      )

      // Portion & Nutrition bar
      Spacer(modifier = Modifier.height(14.dp))
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CharcoalElevated,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, WarmBorderDark)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PORTION", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 9.sp)
            Text(product.portion, style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CALORIES", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 9.sp)
            Text("${product.calories} kcal", style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PROTEIN", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 9.sp)
            Text("${product.proteinGrams}g", style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PREP TIME", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 9.sp)
            Text("${product.prepTimeMinutes} mins", style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
          }
        }
      }

      // Tasting Notes & Ingredients
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "CHEF'S TASTING NOTES",
        style = MaterialTheme.typography.labelSmall,
        color = MutedChampagne,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = product.tastingNotes,
        style = MaterialTheme.typography.bodySmall,
        color = MutedLightText,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "PROVENANCE & KEY INGREDIENTS",
        style = MaterialTheme.typography.labelSmall,
        color = MutedChampagne,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = product.ingredientsList.joinToString(" • "),
        style = MaterialTheme.typography.bodySmall,
        color = MutedLightText
      )

      if (product.allergens.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "ALLERGEN NOTICE: ${product.allergens}",
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFFEF9A9A),
          fontSize = 10.sp
        )
      }

      // Modifiers
      if (product.modifierGroups.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        product.modifierGroups.forEach { group ->
          Text(
            text = "${group.name.uppercase()} ${if (group.required) "(REQUIRED)" else "(OPTIONAL)"}",
            style = MaterialTheme.typography.labelMedium,
            color = MutedChampagne,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          group.options.forEach { option ->
            val isSelected = selectedModifiers[group.id]?.id == option.id
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clickable {
                  if (isSelected && !group.required) {
                    selectedModifiers.remove(group.id)
                  } else {
                    selectedModifiers[group.id] = option
                  }
                },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) CharcoalElevated else Color.Transparent,
              border = BorderStroke(
                1.dp,
                if (isSelected) MutedChampagne else WarmBorderDark
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  RadioButton(
                    selected = isSelected,
                    onClick = {
                      if (isSelected && !group.required) {
                        selectedModifiers.remove(group.id)
                      } else {
                        selectedModifiers[group.id] = option
                      }
                    },
                    colors = RadioButtonDefaults.colors(
                      selectedColor = MutedChampagne,
                      unselectedColor = MutedSecondaryText
                    )
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = option.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PorcelainIvory
                  )
                }
                if (option.priceDelta > 0) {
                  Text(
                    text = "+₹${String.format("%,.0f", option.priceDelta)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedChampagne,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }
      }

      // Special Chef Instructions
      Spacer(modifier = Modifier.height(10.dp))
      OutlinedTextField(
        value = specialInstructions,
        onValueChange = { specialInstructions = it },
        label = { Text("Special Preparation Notes for Chef", color = MutedLightText) },
        placeholder = { Text("e.g. extra crispy crust, separate sauces...", color = MutedSecondaryText) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = PorcelainIvory,
          unfocusedTextColor = PorcelainIvory,
          focusedBorderColor = MutedChampagne,
          unfocusedBorderColor = WarmBorderDark
        ),
        maxLines = 2
      )

      // Quantity and Add to Cart Bottom Bar
      Spacer(modifier = Modifier.height(24.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Quantity Stepper
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CharcoalElevated,
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
          ) {
            IconButton(
              onClick = { if (quantity > 1) quantity-- },
              modifier = Modifier.size(36.dp)
            ) {
              Text("−", color = PorcelainIvory, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(
              text = "$quantity",
              style = MaterialTheme.typography.titleMedium,
              color = PorcelainIvory,
              modifier = Modifier.padding(horizontal = 10.dp),
              fontWeight = FontWeight.Bold
            )
            IconButton(
              onClick = { quantity++ },
              modifier = Modifier.size(36.dp)
            ) {
              Text("+", color = PorcelainIvory, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
          onClick = {
            onAddToCart(quantity, selectedModifiers.toMap(), specialInstructions)
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("modal_add_to_cart_button"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = RefinedTerracotta,
            contentColor = PaperWhite
          )
        ) {
          Text(
            text = "Add to Order • ₹${String.format("%,.0f", calculatedTotal)}",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
