package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.Recipe
import com.example.ui.RecipeViewModel
import com.example.ui.theme.*

// Threshold constants for luxury margin intelligence
const val DEFAULT_PROFIT_MARGIN_THRESHOLD = 200.0 // Minimum acceptable profit margin in INR
const val DEFAULT_MARGIN_PERCENT_THRESHOLD = 60.0 // Minimum acceptable percentage margin

/**
 * Supported sorting options for recipes.
 */
enum class RecipeSortOption(val displayName: String) {
  NAME_ASC("Name (A–Z)"),
  NAME_DESC("Name (Z–A)"),
  COST_ASC("Cost (Low → High)"),
  COST_DESC("Cost (High → Low)"),
  MARGIN_DESC("Profit Margin (High → Low)"),
  MARGIN_ASC("Profit Margin (Low → High)")
}

/**
 * Composable list view that observes RecipeViewModel's StateFlow
 * and displays all saved recipes in a scrollable list with delete buttons for each item.
 * Calculates and displays potential profit margin (price minus cost) for each recipe,
 * highlighting items with a margin below threshold in red.
 * Includes interactive menu and buttons to sort the displayed list by name, cost, or profit margin.
 */
@Composable
fun RecipeListView(
  modifier: Modifier = Modifier,
  recipeViewModel: RecipeViewModel = viewModel(),
  profitMarginThreshold: Double = DEFAULT_PROFIT_MARGIN_THRESHOLD,
  onAddRecipeClick: (() -> Unit)? = null
) {
  val recipes by recipeViewModel.recipes.collectAsStateWithLifecycle()
  var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }
  var filterLowMarginOnly by remember { mutableStateOf(false) }
  var currentSort by remember { mutableStateOf(RecipeSortOption.NAME_ASC) }
  var isSortMenuExpanded by remember { mutableStateOf(false) }
  var selectedRecipeForDetail by remember { mutableStateOf<Recipe?>(null) }

  val lowMarginCount = remember(recipes, profitMarginThreshold) {
    recipes.count { (it.price - it.cost) < profitMarginThreshold }
  }

  // If a recipe is selected for viewing/editing, show the Edit/View Recipe detail view
  if (selectedRecipeForDetail != null) {
    RecipeDetailView(
      recipe = selectedRecipeForDetail!!,
      profitMarginThreshold = profitMarginThreshold,
      onSave = { updated ->
        recipeViewModel.updateRecipe(updated)
        selectedRecipeForDetail = null
      },
      onDelete = { toDelete ->
        recipeViewModel.removeRecipe(toDelete)
        selectedRecipeForDetail = null
      },
      onDismiss = { selectedRecipeForDetail = null },
      modifier = modifier
    )
    return
  }

  // Filter and sort recipes based on selected options
  val sortedRecipes = remember(recipes, filterLowMarginOnly, profitMarginThreshold, currentSort) {
    val filtered = if (filterLowMarginOnly) {
      recipes.filter { (it.price - it.cost) < profitMarginThreshold }
    } else {
      recipes
    }

    when (currentSort) {
      RecipeSortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
      RecipeSortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
      RecipeSortOption.COST_ASC -> filtered.sortedBy { it.cost }
      RecipeSortOption.COST_DESC -> filtered.sortedByDescending { it.cost }
      RecipeSortOption.MARGIN_DESC -> filtered.sortedByDescending { it.price - it.cost }
      RecipeSortOption.MARGIN_ASC -> filtered.sortedBy { it.price - it.cost }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianCharcoal)
      .testTag("recipe_list_view")
  ) {
    // Header Bar
    Surface(
      color = CharcoalSurface,
      border = BorderStroke(1.dp, WarmBorderDark),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(MutedChampagne)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "FINANCIAL RECIPE COSTING",
                style = MaterialTheme.typography.labelSmall,
                color = MutedChampagne,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Saved Recipes (${recipes.size})",
              style = MaterialTheme.typography.titleMedium,
              color = PorcelainIvory
            )
          }

          if (onAddRecipeClick != null) {
            Button(
              onClick = onAddRecipeClick,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = RefinedTerracotta,
                contentColor = PaperWhite
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier.testTag("recipe_list_add_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Recipe", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Threshold Telemetry & Quick Filter Strip
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Target Profit: ",
              style = MaterialTheme.typography.labelSmall,
              color = MutedLightText
            )
            Text(
              text = "≥ ₹${String.format("%,.0f", profitMarginThreshold)}",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              fontWeight = FontWeight.Bold
            )
          }

          if (lowMarginCount > 0) {
            FilterChip(
              selected = filterLowMarginOnly,
              onClick = { filterLowMarginOnly = !filterLowMarginOnly },
              label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "$lowMarginCount Below Target",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF8A80),
                    fontWeight = FontWeight.Bold
                  )
                }
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF3E1C1C),
                selectedLabelColor = Color(0xFFFF8A80),
                containerColor = Color(0xFF2E1515),
                labelColor = Color(0xFFEF9A9A)
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = filterLowMarginOnly,
                borderColor = Color(0xFFE53935).copy(alpha = 0.5f),
                selectedBorderColor = Color(0xFFE53935)
              ),
              modifier = Modifier.testTag("filter_low_margin_chip")
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sorting Controls: Dropdown Menu & Quick Sort Buttons (Name, Cost, Profit Margin)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Sort Dropdown Menu button
          Box {
            OutlinedButton(
              onClick = { isSortMenuExpanded = true },
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, MutedChampagne),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MutedChampagne
              ),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.testTag("recipe_sort_menu_button")
            ) {
              Icon(Icons.Default.Sort, contentDescription = "Sort Menu", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Sort: ${currentSort.displayName}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
            }

            DropdownMenu(
              expanded = isSortMenuExpanded,
              onDismissRequest = { isSortMenuExpanded = false },
              modifier = Modifier
                .background(CharcoalSurface)
                .border(1.dp, WarmBorderDark)
                .testTag("recipe_sort_dropdown_menu")
            ) {
              DropdownMenuItem(
                text = { Text("Name (A → Z)", color = PorcelainIvory) },
                onClick = {
                  currentSort = RecipeSortOption.NAME_ASC
                  isSortMenuExpanded = false
                },
                leadingIcon = { Icon(Icons.Default.SortByAlpha, contentDescription = null, tint = MutedChampagne) },
                modifier = Modifier.testTag("sort_by_name_asc")
              )
              DropdownMenuItem(
                text = { Text("Name (Z → A)", color = PorcelainIvory) },
                onClick = {
                  currentSort = RecipeSortOption.NAME_DESC
                  isSortMenuExpanded = false
                },
                leadingIcon = { Icon(Icons.Default.SortByAlpha, contentDescription = null, tint = MutedChampagne) },
                modifier = Modifier.testTag("sort_by_name_desc")
              )
              HorizontalDivider(color = WarmBorderDark)
              DropdownMenuItem(
                text = { Text("Cost (Lowest first)", color = PorcelainIvory) },
                onClick = {
                  currentSort = RecipeSortOption.COST_ASC
                  isSortMenuExpanded = false
                },
                leadingIcon = { Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = MutedChampagne) },
                modifier = Modifier.testTag("sort_by_cost_asc")
              )
              DropdownMenuItem(
                text = { Text("Cost (Highest first)", color = PorcelainIvory) },
                onClick = {
                  currentSort = RecipeSortOption.COST_DESC
                  isSortMenuExpanded = false
                },
                leadingIcon = { Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = MutedChampagne) },
                modifier = Modifier.testTag("sort_by_cost_desc")
              )
              HorizontalDivider(color = WarmBorderDark)
              DropdownMenuItem(
                text = { Text("Profit Margin (Highest first)", color = PorcelainIvory) },
                onClick = {
                  currentSort = RecipeSortOption.MARGIN_DESC
                  isSortMenuExpanded = false
                },
                leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SageGreen) },
                modifier = Modifier.testTag("sort_by_margin_desc")
              )
              DropdownMenuItem(
                text = { Text("Profit Margin (Lowest first)", color = PorcelainIvory) },
                onClick = {
                  currentSort = RecipeSortOption.MARGIN_ASC
                  isSortMenuExpanded = false
                },
                leadingIcon = { Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFEF5350)) },
                modifier = Modifier.testTag("sort_by_margin_asc")
              )
            }
          }

          // Button / Chip: Sort by Name
          FilterChip(
            selected = currentSort == RecipeSortOption.NAME_ASC || currentSort == RecipeSortOption.NAME_DESC,
            onClick = {
              currentSort = if (currentSort == RecipeSortOption.NAME_ASC) {
                RecipeSortOption.NAME_DESC
              } else {
                RecipeSortOption.NAME_ASC
              }
            },
            label = {
              Text(
                text = "Name ${if (currentSort == RecipeSortOption.NAME_ASC) "A-Z ↑" else if (currentSort == RecipeSortOption.NAME_DESC) "Z-A ↓" else ""}".trim(),
                style = MaterialTheme.typography.labelSmall
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CharcoalElevated,
              selectedLabelColor = MutedChampagne,
              containerColor = Color.Transparent,
              labelColor = MutedLightText
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = currentSort == RecipeSortOption.NAME_ASC || currentSort == RecipeSortOption.NAME_DESC,
              borderColor = WarmBorderDark,
              selectedBorderColor = MutedChampagne
            ),
            modifier = Modifier.testTag("sort_button_name")
          )

          // Button / Chip: Sort by Cost
          FilterChip(
            selected = currentSort == RecipeSortOption.COST_ASC || currentSort == RecipeSortOption.COST_DESC,
            onClick = {
              currentSort = if (currentSort == RecipeSortOption.COST_ASC) {
                RecipeSortOption.COST_DESC
              } else {
                RecipeSortOption.COST_ASC
              }
            },
            label = {
              Text(
                text = "Cost ${if (currentSort == RecipeSortOption.COST_ASC) "Low ↑" else if (currentSort == RecipeSortOption.COST_DESC) "High ↓" else ""}".trim(),
                style = MaterialTheme.typography.labelSmall
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CharcoalElevated,
              selectedLabelColor = MutedChampagne,
              containerColor = Color.Transparent,
              labelColor = MutedLightText
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = currentSort == RecipeSortOption.COST_ASC || currentSort == RecipeSortOption.COST_DESC,
              borderColor = WarmBorderDark,
              selectedBorderColor = MutedChampagne
            ),
            modifier = Modifier.testTag("sort_button_cost")
          )

          // Button / Chip: Sort by Profit Margin
          FilterChip(
            selected = currentSort == RecipeSortOption.MARGIN_DESC || currentSort == RecipeSortOption.MARGIN_ASC,
            onClick = {
              currentSort = if (currentSort == RecipeSortOption.MARGIN_DESC) {
                RecipeSortOption.MARGIN_ASC
              } else {
                RecipeSortOption.MARGIN_DESC
              }
            },
            label = {
              Text(
                text = "Profit Margin ${if (currentSort == RecipeSortOption.MARGIN_DESC) "High ↓" else if (currentSort == RecipeSortOption.MARGIN_ASC) "Low ↑" else ""}".trim(),
                style = MaterialTheme.typography.labelSmall
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CharcoalElevated,
              selectedLabelColor = MutedChampagne,
              containerColor = Color.Transparent,
              labelColor = MutedLightText
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = currentSort == RecipeSortOption.MARGIN_DESC || currentSort == RecipeSortOption.MARGIN_ASC,
              borderColor = WarmBorderDark,
              selectedBorderColor = MutedChampagne
            ),
            modifier = Modifier.testTag("sort_button_profit_margin")
          )
        }
      }
    }

    if (sortedRecipes.isEmpty()) {
      // Empty state
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(CharcoalElevated),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = MutedChampagne,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = if (filterLowMarginOnly) "No low-margin recipes found" else "No saved recipes found",
            style = MaterialTheme.typography.titleMedium,
            color = PorcelainIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (filterLowMarginOnly) "All saved recipes meet or exceed the ₹${String.format("%,.0f", profitMarginThreshold)} threshold."
            else "Saved recipes with ingredient costs and profit margins will appear here.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedLightText
          )
          if (filterLowMarginOnly) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { filterLowMarginOnly = false }) {
              Text("Show All Recipes", color = MutedChampagne)
            }
          } else if (onAddRecipeClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = onAddRecipeClick,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = RefinedTerracotta,
                contentColor = PaperWhite
              )
            ) {
              Text("Create First Recipe", style = MaterialTheme.typography.labelMedium)
            }
          }
        }
      }
    } else {
      // Scrollable LazyColumn of Recipes with Profit Margin and Low-Margin Red Highlight
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("recipe_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(
          items = sortedRecipes,
          key = { it.id }
        ) { recipe ->
          RecipeListItemCard(
            recipe = recipe,
            profitThreshold = profitMarginThreshold,
            onClick = { selectedRecipeForDetail = recipe },
            onDeleteClick = { recipeToDelete = recipe }
          )
        }
      }
    }
  }

  // Confirmation Delete Dialog
  recipeToDelete?.let { recipe ->
    AlertDialog(
      onDismissRequest = { recipeToDelete = null },
      containerColor = CharcoalSurface,
      title = {
        Text("Remove Recipe?", style = MaterialTheme.typography.titleMedium, color = PorcelainIvory)
      },
      text = {
        Text(
          text = "Are you sure you want to delete '${recipe.name}' from your saved recipe database?",
          style = MaterialTheme.typography.bodyMedium,
          color = MutedLightText
        )
      },
      confirmButton = {
        Button(
          onClick = {
            recipeViewModel.removeRecipe(recipe)
            recipeToDelete = null
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = PaperWhite
          ),
          modifier = Modifier.testTag("confirm_delete_button")
        ) {
          Text("Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { recipeToDelete = null }) {
          Text("Cancel", color = MutedLightText)
        }
      }
    )
  }
}

/**
 * Individual Recipe card item calculating and displaying the potential profit margin
 * (price minus cost) and highlighting in red if below the profit threshold.
 */
@Composable
fun RecipeListItemCard(
  recipe: Recipe,
  profitThreshold: Double = DEFAULT_PROFIT_MARGIN_THRESHOLD,
  onClick: () -> Unit = {},
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Calculate potential profit margin (price minus cost)
  val potentialProfitMargin = recipe.price - recipe.cost
  val profitMarginPercent = if (recipe.price > 0) (potentialProfitMargin / recipe.price) * 100 else 0.0

  // Determine if recipe margin is below acceptable threshold
  val isBelowThreshold = potentialProfitMargin < profitThreshold

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("recipe_item_${recipe.id}")
      .clickable { onClick() },
    shape = RoundedCornerShape(10.dp),
    color = if (isBelowThreshold) Color(0xFF221616) else CharcoalSurface,
    border = BorderStroke(
      width = if (isBelowThreshold) 1.5.dp else 1.dp,
      color = if (isBelowThreshold) Color(0xFFE53935) else WarmBorderDark
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Title Row with Margin Badge and Delete Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = recipe.name,
              style = MaterialTheme.typography.titleMedium,
              color = PorcelainIvory,
              fontWeight = FontWeight.Bold
            )
            if (isBelowThreshold) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFFE53935),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "LOW MARGIN",
                  style = MaterialTheme.typography.labelSmall,
                  color = PaperWhite,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
          Text(
            text = "ID: #${recipe.id}",
            style = MaterialTheme.typography.labelSmall,
            color = MutedSecondaryText,
            fontSize = 9.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Margin Indicator Pill (Red if below threshold, Green/Gold if healthy)
          Surface(
            color = if (isBelowThreshold) Color(0xFF3E1C1C) else if (profitMarginPercent >= 60.0) SageGreenContainer else Color(0xFF2A231C),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(
              1.dp,
              if (isBelowThreshold) Color(0xFFEF5350)
              else if (profitMarginPercent >= 60.0) SageGreen.copy(alpha = 0.6f)
              else AntiqueBrass.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("margin_badge_${recipe.id}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isBelowThreshold) {
                Icon(
                  imageVector = Icons.Default.WarningAmber,
                  contentDescription = "Low Margin Alert",
                  tint = Color(0xFFEF5350),
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = "${String.format("%.1f", profitMarginPercent)}% Margin",
                style = MaterialTheme.typography.labelSmall,
                color = if (isBelowThreshold) Color(0xFFFF8A80) else if (profitMarginPercent >= 60.0) SageGreen else GoldAccent,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Delete Action Button
          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(CharcoalElevated)
              .testTag("delete_recipe_${recipe.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete ${recipe.name}",
              tint = Color(0xFFEF9A9A),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Ingredients List Chips
      if (recipe.ingredients.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "INGREDIENTS (${recipe.ingredients.size}):",
          style = MaterialTheme.typography.labelSmall,
          color = if (isBelowThreshold) Color(0xFFEF9A9A) else MutedChampagne,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          recipe.ingredients.forEach { ingredient ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CharcoalElevated,
              border = BorderStroke(1.dp, if (isBelowThreshold) Color(0xFF5A2525) else WarmBorderDark)
            ) {
              Text(
                text = ingredient,
                style = MaterialTheme.typography.labelSmall,
                color = MutedLightText,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(
        color = if (isBelowThreshold) Color(0xFF5A2525) else WarmBorderDark,
        thickness = 1.dp
      )
      Spacer(modifier = Modifier.height(8.dp))

      // Financial Economics Row with Potential Profit Margin (Price minus Cost)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Recipe Cost", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 10.sp)
          Text("₹${String.format("%,.0f", recipe.cost)}", style = MaterialTheme.typography.titleSmall, color = MutedLightText)
        }
        Column {
          Text("Selling Price", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 10.sp)
          Text("₹${String.format("%,.0f", recipe.price)}", style = MaterialTheme.typography.titleSmall, color = PorcelainIvory, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Profit Margin (Price − Cost)",
            style = MaterialTheme.typography.bodySmall,
            color = if (isBelowThreshold) Color(0xFFEF5350) else MutedLightText,
            fontSize = 10.sp,
            fontWeight = if (isBelowThreshold) FontWeight.Bold else FontWeight.Normal
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${String.format("%,.0f", potentialProfitMargin)}",
              style = MaterialTheme.typography.titleMedium,
              color = if (isBelowThreshold) Color(0xFFEF5350) else GoldAccent,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.testTag("profit_margin_value_${recipe.id}")
            )
            if (isBelowThreshold) {
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "(< ₹${String.format("%,.0f", profitThreshold)})",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFEF5350),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
