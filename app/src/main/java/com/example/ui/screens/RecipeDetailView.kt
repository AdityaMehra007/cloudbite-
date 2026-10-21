package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.data.local.Recipe
import com.example.ui.theme.*
import com.example.util.MeasurementUnit
import com.example.util.UnitConverter
import java.io.File
import java.io.FileOutputStream

/**
 * Edit/View Recipe detail view that lets users view and modify a recipe's details,
 * including its ingredients list and real-time financial metrics.
 * Includes a helper function to convert ingredient measurements between units
 * like grams, milliliters, and ounces within the edit view.
 */
@Composable
fun RecipeDetailView(
  recipe: Recipe,
  profitMarginThreshold: Double = DEFAULT_PROFIT_MARGIN_THRESHOLD,
  onSave: (Recipe) -> Unit,
  onDelete: (Recipe) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onDismiss() }

  // Editable local state
  var name by remember(recipe) { mutableStateOf(recipe.name) }
  var costText by remember(recipe) { mutableStateOf(recipe.cost.toString()) }
  var priceText by remember(recipe) { mutableStateOf(recipe.price.toString()) }
  var ingredientsList by remember(recipe) { mutableStateOf(recipe.ingredients.toMutableList()) }
  var newIngredientText by remember { mutableStateOf("") }
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  var hasUnsavedChanges by remember { mutableStateOf(false) }

  // Ingredient measurement unit converter state
  var converterValueText by remember { mutableStateOf("100") }
  var converterFromUnit by remember { mutableStateOf(MeasurementUnit.GRAMS) }
  var converterToUnit by remember { mutableStateOf(MeasurementUnit.OUNCES) }
  var isConverterExpanded by remember { mutableStateOf(true) }
  var isFromUnitDropdownExpanded by remember { mutableStateOf(false) }
  var isToUnitDropdownExpanded by remember { mutableStateOf(false) }

  val converterInputValue = converterValueText.toDoubleOrNull() ?: 0.0
  val convertedResultValue = UnitConverter.convertMeasurement(
    value = converterInputValue,
    fromUnit = converterFromUnit,
    toUnit = converterToUnit
  )

  // Derived financial metrics computed live from input text
  val cost = costText.toDoubleOrNull() ?: 0.0
  val price = priceText.toDoubleOrNull() ?: 0.0
  val potentialProfitMargin = price - cost
  val grossMarginPercent = if (price > 0) (potentialProfitMargin / price) * 100 else 0.0
  val foodCostRatio = if (price > 0) (cost / price) * 100 else 0.0
  val isLowMargin = potentialProfitMargin < profitMarginThreshold

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianCharcoal)
      .testTag("recipe_detail_view")
  ) {
    // Top Bar
    Surface(
      color = CharcoalSurface,
      border = BorderStroke(1.dp, WarmBorderDark),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("recipe_detail_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = PorcelainIvory
            )
          }
          Column {
            Text(
              text = "EDIT / VIEW RECIPE",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "ID: #${recipe.id}",
              style = MaterialTheme.typography.labelSmall,
              color = MutedSecondaryText,
              fontSize = 11.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { showDeleteConfirmDialog = true },
            modifier = Modifier.testTag("recipe_detail_delete_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete Recipe",
              tint = Color(0xFFEF9A9A)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          Button(
            onClick = {
              val updated = recipe.copy(
                name = name.trim().ifBlank { recipe.name },
                ingredients = ingredientsList.toList(),
                cost = cost,
                price = price
              )
              onSave(updated)
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = RefinedTerracotta,
              contentColor = PaperWhite
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            modifier = Modifier.testTag("recipe_detail_save_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Changes", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Scrollable Form & Metrics Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Live Financial Economics Card
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isLowMargin) Color(0xFF221616) else CharcoalSurface,
        border = BorderStroke(
          width = if (isLowMargin) 1.5.dp else 1.dp,
          color = if (isLowMargin) Color(0xFFE53935) else WarmBorderDark
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "UNIT ECONOMICS & FINANCIAL AUDIT",
                style = MaterialTheme.typography.labelSmall,
                color = if (isLowMargin) Color(0xFFEF9A9A) else MutedChampagne,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Live Calculated Margins",
                style = MaterialTheme.typography.titleMedium,
                color = PorcelainIvory
              )
            }

            // Margin Badge
            Surface(
              color = if (isLowMargin) Color(0xFF3E1C1C) else if (grossMarginPercent >= 60.0) SageGreenContainer else Color(0xFF2A231C),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(
                1.dp,
                if (isLowMargin) Color(0xFFEF5350) else if (grossMarginPercent >= 60.0) SageGreen else AntiqueBrass
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (isLowMargin) {
                  Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                  text = "${String.format("%.1f", grossMarginPercent)}% Margin",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isLowMargin) Color(0xFFFF8A80) else if (grossMarginPercent >= 60.0) SageGreen else GoldAccent,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Breakdown Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Recipe Cost", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
              Text("₹${String.format("%,.0f", cost)}", style = MaterialTheme.typography.titleLarge, color = PorcelainIvory)
              Text("${String.format("%.0f", foodCostRatio)}% of price", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 10.sp)
            }
            Column {
              Text("Menu Price", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
              Text("₹${String.format("%,.0f", price)}", style = MaterialTheme.typography.titleLarge, color = PorcelainIvory)
              Text("Patron listing", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Profit Margin (Price − Cost)", style = MaterialTheme.typography.bodySmall, color = if (isLowMargin) Color(0xFFEF5350) else MutedLightText, fontSize = 11.sp)
              Text(
                text = "₹${String.format("%,.0f", potentialProfitMargin)}",
                style = MaterialTheme.typography.titleLarge,
                color = if (isLowMargin) Color(0xFFEF5350) else GoldAccent,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isLowMargin) "⚠️ Under ₹${String.format("%,.0f", profitMarginThreshold)} target" else "Target met",
                style = MaterialTheme.typography.labelSmall,
                color = if (isLowMargin) Color(0xFFEF5350) else SageGreen,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // 2. Recipe Name Input
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CharcoalSurface,
        border = BorderStroke(1.dp, WarmBorderDark)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "RECIPE IDENTIFICATION",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              hasUnsavedChanges = true
            },
            label = { Text("Dish / Recipe Name", color = MutedLightText) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("recipe_detail_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PorcelainIvory,
              unfocusedTextColor = PorcelainIvory,
              focusedBorderColor = MutedChampagne,
              unfocusedBorderColor = WarmBorderDark
            )
          )
        }
      }

      // 3. Financial Metrics Pricing Inputs
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CharcoalSurface,
        border = BorderStroke(1.dp, WarmBorderDark)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "PRICING & UNIT COST PARAMETERS",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedTextField(
              value = costText,
              onValueChange = {
                costText = it
                hasUnsavedChanges = true
              },
              label = { Text("Cost (₹)", color = MutedLightText) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("recipe_detail_cost_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PorcelainIvory,
                unfocusedTextColor = PorcelainIvory,
                focusedBorderColor = MutedChampagne,
                unfocusedBorderColor = WarmBorderDark
              )
            )

            OutlinedTextField(
              value = priceText,
              onValueChange = {
                priceText = it
                hasUnsavedChanges = true
              },
              label = { Text("Target Price (₹)", color = MutedLightText) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("recipe_detail_price_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PorcelainIvory,
                unfocusedTextColor = PorcelainIvory,
                focusedBorderColor = MutedChampagne,
                unfocusedBorderColor = WarmBorderDark
              )
            )
          }
        }
      }

      // 4. Ingredients Management
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CharcoalSurface,
        border = BorderStroke(1.dp, WarmBorderDark)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "INGREDIENTS LIST (${ingredientsList.size})",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Tap 'x' to remove",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSecondaryText,
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Existing Ingredients as removable chips
          if (ingredientsList.isEmpty()) {
            Text(
              text = "No ingredients specified. Add one below.",
              style = MaterialTheme.typography.bodySmall,
              color = MutedLightText,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              ingredientsList.forEachIndexed { index, ingredient ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CharcoalElevated,
                  border = BorderStroke(1.dp, WarmBorderDark),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "${index + 1}. $ingredient",
                      style = MaterialTheme.typography.bodyMedium,
                      color = PorcelainIvory
                    )
                    IconButton(
                      onClick = {
                        ingredientsList.removeAt(index)
                        hasUnsavedChanges = true
                      },
                      modifier = Modifier
                        .size(24.dp)
                        .testTag("remove_ingredient_$index")
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove $ingredient",
                        tint = Color(0xFFEF9A9A),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Add New Ingredient Input Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = newIngredientText,
              onValueChange = { newIngredientText = it },
              placeholder = { Text("e.g. Kashmiri Saffron", color = MutedSecondaryText, fontSize = 12.sp) },
              label = { Text("Add Ingredient", color = MutedLightText) },
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("add_ingredient_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PorcelainIvory,
                unfocusedTextColor = PorcelainIvory,
                focusedBorderColor = MutedChampagne,
                unfocusedBorderColor = WarmBorderDark
              )
            )

            Button(
              onClick = {
                val trimmed = newIngredientText.trim()
                if (trimmed.isNotBlank()) {
                  ingredientsList.add(trimmed)
                  newIngredientText = ""
                  hasUnsavedChanges = true
                }
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = CharcoalElevated,
                contentColor = MutedChampagne
              ),
              border = BorderStroke(1.dp, MutedChampagne.copy(alpha = 0.5f)),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
              modifier = Modifier.testTag("add_ingredient_button")
            ) {
              Text("+ Add", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 5. Culinary Measurement Converter Card
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("measurement_converter_card"),
        shape = RoundedCornerShape(10.dp),
        color = CharcoalSurface,
        border = BorderStroke(1.dp, WarmBorderDark)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "CULINARY MEASUREMENT CONVERTER",
                style = MaterialTheme.typography.labelSmall,
                color = MutedChampagne,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Grams (g) • Milliliters (ml) • Ounces (oz)",
                style = MaterialTheme.typography.bodySmall,
                color = MutedLightText,
                fontSize = 11.sp
              )
            }
            TextButton(
              onClick = { isConverterExpanded = !isConverterExpanded },
              modifier = Modifier.testTag("toggle_converter_button")
            ) {
              Text(
                text = if (isConverterExpanded) "Hide" else "Show Converter",
                color = MutedChampagne,
                style = MaterialTheme.typography.labelSmall
              )
            }
          }

          if (isConverterExpanded) {
            Spacer(modifier = Modifier.height(12.dp))

            // Inputs Row: Quantity, From Unit, Swap Button, To Unit
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Quantity Input
              OutlinedTextField(
                value = converterValueText,
                onValueChange = { converterValueText = it },
                label = { Text("Amount", color = MutedLightText, fontSize = 10.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("converter_input_value"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = PorcelainIvory,
                  unfocusedTextColor = PorcelainIvory,
                  focusedBorderColor = MutedChampagne,
                  unfocusedBorderColor = WarmBorderDark
                )
              )

              // From Unit Dropdown Selector
              Box(modifier = Modifier.weight(1.3f)) {
                OutlinedButton(
                  onClick = { isFromUnitDropdownExpanded = true },
                  shape = RoundedCornerShape(8.dp),
                  border = BorderStroke(1.dp, WarmBorderDark),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = PorcelainIvory),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                  modifier = Modifier.fillMaxWidth().testTag("converter_from_unit_button")
                ) {
                  Text(converterFromUnit.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }

                DropdownMenu(
                  expanded = isFromUnitDropdownExpanded,
                  onDismissRequest = { isFromUnitDropdownExpanded = false },
                  modifier = Modifier
                    .background(CharcoalSurface)
                    .border(1.dp, WarmBorderDark)
                ) {
                  MeasurementUnit.values().forEach { unit ->
                    DropdownMenuItem(
                      text = { Text(unit.displayName, color = PorcelainIvory) },
                      onClick = {
                        converterFromUnit = unit
                        isFromUnitDropdownExpanded = false
                      }
                    )
                  }
                }
              }

              // Swap Button
              IconButton(
                onClick = {
                  val temp = converterFromUnit
                  converterFromUnit = converterToUnit
                  converterToUnit = temp
                },
                modifier = Modifier
                  .size(36.dp)
                  .testTag("converter_swap_button")
              ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Units", tint = MutedChampagne)
              }

              // To Unit Dropdown Selector
              Box(modifier = Modifier.weight(1.3f)) {
                OutlinedButton(
                  onClick = { isToUnitDropdownExpanded = true },
                  shape = RoundedCornerShape(8.dp),
                  border = BorderStroke(1.dp, WarmBorderDark),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = PorcelainIvory),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                  modifier = Modifier.fillMaxWidth().testTag("converter_to_unit_button")
                ) {
                  Text(converterToUnit.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }

                DropdownMenu(
                  expanded = isToUnitDropdownExpanded,
                  onDismissRequest = { isToUnitDropdownExpanded = false },
                  modifier = Modifier
                    .background(CharcoalSurface)
                    .border(1.dp, WarmBorderDark)
                ) {
                  MeasurementUnit.values().forEach { unit ->
                    DropdownMenuItem(
                      text = { Text(unit.displayName, color = PorcelainIvory) },
                      onClick = {
                        converterToUnit = unit
                        isToUnitDropdownExpanded = false
                      }
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Preset Conversion Chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              FilterChip(
                selected = converterFromUnit == MeasurementUnit.GRAMS && converterToUnit == MeasurementUnit.OUNCES,
                onClick = {
                  converterFromUnit = MeasurementUnit.GRAMS
                  converterToUnit = MeasurementUnit.OUNCES
                },
                label = { Text("Grams → Ounces", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = CharcoalElevated,
                  selectedLabelColor = MutedChampagne,
                  containerColor = Color.Transparent,
                  labelColor = MutedLightText
                )
              )
              FilterChip(
                selected = converterFromUnit == MeasurementUnit.MILLILITERS && converterToUnit == MeasurementUnit.FLUID_OUNCES,
                onClick = {
                  converterFromUnit = MeasurementUnit.MILLILITERS
                  converterToUnit = MeasurementUnit.FLUID_OUNCES
                },
                label = { Text("Milliliters → Fl Oz", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = CharcoalElevated,
                  selectedLabelColor = MutedChampagne,
                  containerColor = Color.Transparent,
                  labelColor = MutedLightText
                )
              )
              FilterChip(
                selected = converterFromUnit == MeasurementUnit.OUNCES && converterToUnit == MeasurementUnit.GRAMS,
                onClick = {
                  converterFromUnit = MeasurementUnit.OUNCES
                  converterToUnit = MeasurementUnit.GRAMS
                },
                label = { Text("Ounces → Grams", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = CharcoalElevated,
                  selectedLabelColor = MutedChampagne,
                  containerColor = Color.Transparent,
                  labelColor = MutedLightText
                )
              )
              FilterChip(
                selected = converterFromUnit == MeasurementUnit.GRAMS && converterToUnit == MeasurementUnit.MILLILITERS,
                onClick = {
                  converterFromUnit = MeasurementUnit.GRAMS
                  converterToUnit = MeasurementUnit.MILLILITERS
                },
                label = { Text("Grams ↔ Milliliters", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = CharcoalElevated,
                  selectedLabelColor = MutedChampagne,
                  containerColor = Color.Transparent,
                  labelColor = MutedLightText
                )
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conversion Result Box with Quick Insert Action
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              color = CharcoalElevated,
              border = BorderStroke(1.dp, WarmBorderDark)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "CONVERTED RESULT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedLightText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${UnitConverter.formatMeasurement(converterInputValue, converterFromUnit)} = ${UnitConverter.formatMeasurement(convertedResultValue, converterToUnit)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("converter_result_text")
                  )
                }

                Button(
                  onClick = {
                    val formatted = "${UnitConverter.formatMeasurement(converterInputValue, converterFromUnit)} (${UnitConverter.formatMeasurement(convertedResultValue, converterToUnit)})"
                    newIngredientText = if (newIngredientText.isBlank()) formatted else "$newIngredientText $formatted"
                  },
                  shape = RoundedCornerShape(6.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = CharcoalSurface,
                    contentColor = MutedChampagne
                  ),
                  border = BorderStroke(1.dp, MutedChampagne.copy(alpha = 0.5f)),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("converter_append_to_ingredient_button")
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Use in Input", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, WarmBorderDark),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedLightText),
          modifier = Modifier
            .weight(1f)
            .testTag("recipe_detail_cancel_button")
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            val updated = recipe.copy(
              name = name.trim().ifBlank { recipe.name },
              ingredients = ingredientsList.toList(),
              cost = cost,
              price = price
            )
            onSave(updated)
          },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = RefinedTerracotta,
            contentColor = PaperWhite
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("recipe_detail_bottom_save_button")
        ) {
          Text("Save to Room DB", fontWeight = FontWeight.Bold)
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (showDeleteConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmDialog = false },
      containerColor = CharcoalSurface,
      title = { Text("Delete Recipe?", color = PorcelainIvory) },
      text = {
        Text(
          "Are you sure you want to permanently remove '${recipe.name}' from the Room database?",
          color = MutedLightText
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirmDialog = false
            onDelete(recipe)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = PaperWhite
          ),
          modifier = Modifier.testTag("recipe_detail_confirm_delete_button")
        ) {
          Text("Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmDialog = false }) {
          Text("Cancel", color = MutedLightText)
        }
      }
    )
  }
}
