package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Recipe
import com.example.data.model.Ingredient
import com.example.data.model.RecipeItem
import com.example.ui.MainViewModel
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*

@Composable
fun OwnerConsoleScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()
  val ingredients by viewModel.ingredients.collectAsState()
  val roomRecipes by viewModel.roomRecipes.collectAsState()

  var activeTab by remember { mutableStateOf("OVERVIEW") } // OVERVIEW, INVENTORY, RECIPES
  var showAddRecipeDialog by remember { mutableStateOf(false) }
  var selectedRecipeForDetail by remember { mutableStateOf<Recipe?>(null) }

  if (selectedRecipeForDetail != null) {
    RecipeDetailView(
      recipe = selectedRecipeForDetail!!,
      onSave = { updated ->
        viewModel.updateRoomRecipe(updated)
        selectedRecipeForDetail = null
      },
      onDelete = { toDelete ->
        viewModel.deleteRoomRecipe(toDelete)
        selectedRecipeForDetail = null
      },
      onDismiss = { selectedRecipeForDetail = null },
      modifier = modifier
    )
    return
  }

  val totalOrders = orders.size
  val grossOrderValue = orders.sumOf { it.totalAmount }
  val netSales = orders.sumOf { it.subtotal - it.discount }
  val aov = if (totalOrders > 0) grossOrderValue / totalOrders else 0.0
  val completionRate = if (totalOrders > 0) {
    (orders.count { it.status.stepIndex >= 3 }.toDouble() / totalOrders * 100)
  } else 100.0

  val lowStockCount = ingredients.count { it.isLowStock }

  Scaffold(
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianCharcoal)
          .statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "EXECUTIVE CONSOLE",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              letterSpacing = 1.2.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "CloudBite Hospitality Group",
              style = MaterialTheme.typography.titleLarge,
              color = PorcelainIvory
            )
          }

          // Tabs
          Row(
            modifier = Modifier
              .background(CharcoalElevated, RoundedCornerShape(8.dp))
              .border(1.dp, WarmBorderDark, RoundedCornerShape(8.dp))
              .padding(2.dp)
          ) {
            TabButton("FINANCES", activeTab == "OVERVIEW") { activeTab = "OVERVIEW" }
            TabButton("STOCK ($lowStockCount)", activeTab == "INVENTORY") { activeTab = "INVENTORY" }
            TabButton("RECIPES", activeTab == "RECIPES") { activeTab = "RECIPES" }
          }
        }
      }
    },
    containerColor = ObsidianCharcoal,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      when (activeTab) {
        "OVERVIEW" -> {
          // KPI Grid
          item {
            Text(
              text = "TODAY'S FINANCIAL TELEMETRY",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              KpiCard(
                title = "GROSS REVENUE (GOV)",
                value = "₹${String.format("%,.0f", grossOrderValue)}",
                subtitle = "Total volume placed",
                modifier = Modifier.weight(1f)
              )
              KpiCard(
                title = "NET SALES",
                value = "₹${String.format("%,.0f", netSales)}",
                subtitle = "After ₹${String.format("%,.0f", orders.sumOf { it.discount })} discounts",
                modifier = Modifier.weight(1f)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              KpiCard(
                title = "AVERAGE TICKET (AOV)",
                value = "₹${String.format("%,.0f", aov)}",
                subtitle = "High patron spend",
                modifier = Modifier.weight(1f)
              )
              KpiCard(
                title = "COMPLETION RATE",
                value = "${String.format("%.1f", completionRate)}%",
                subtitle = "KDS fulfillment",
                modifier = Modifier.weight(1f)
              )
            }
            Spacer(modifier = Modifier.height(18.dp))
          }

          // Food Cost & Contribution Margin Summary
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              color = CharcoalElevated,
              border = BorderStroke(1.dp, WarmBorderDark)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "UNIT ECONOMICS & MARGIN MODEL",
                  style = MaterialTheme.typography.labelSmall,
                  color = MutedChampagne,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text("Blended Food Cost", style = MaterialTheme.typography.bodySmall, color = MutedLightText)
                    Text("28.4%", style = MaterialTheme.typography.titleMedium, color = SageGreen, fontWeight = FontWeight.Bold)
                  }
                  Column {
                    Text("Packaging & Sealing", style = MaterialTheme.typography.bodySmall, color = MutedLightText)
                    Text("₹30.00 / order", style = MaterialTheme.typography.titleMedium, color = PorcelainIvory, fontWeight = FontWeight.Bold)
                  }
                  Column {
                    Text("Gross Margin", style = MaterialTheme.typography.bodySmall, color = MutedLightText)
                    Text("71.6%", style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(18.dp))
          }

          // Virtual Brands Performance
          item {
            Text(
              text = "BRAND REVENUE CONTRIBUTION",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          items(viewModel.brands) { brand ->
            val brandOrders = orders.filter { order -> order.items.any { it.product.brandId == brand.id } }
            val brandRevenue = brandOrders.sumOf { order ->
              order.items.filter { it.product.brandId == brand.id }.sumOf { it.totalPrice }
            }
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              shape = RoundedCornerShape(8.dp),
              color = CharcoalSurface,
              border = BorderStroke(1.dp, WarmBorderDark)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(brand.name, style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
                  Text("${brandOrders.size} orders logged today", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
                }
                Text("₹${String.format("%,.0f", brandRevenue)}", style = MaterialTheme.typography.titleSmall, color = MutedChampagne, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        "INVENTORY" -> {
          // Low stock alert banner
          item {
            if (lowStockCount > 0) {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2E1C1A),
                border = BorderStroke(1.dp, RefinedTerracotta)
              ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Warning, contentDescription = null, tint = RefinedTerracotta)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("$lowStockCount Ingredients Below Safety Buffer", style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
                    Text("Theoretical consumption from orders has reduced stocks near threshold.", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
                  }
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
            }
          }

          items(ingredients) { ing ->
            IngredientRow(
              ingredient = ing,
              onRestock = { viewModel.restockIngredient(ing.id, 20.0) }
            )
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        "RECIPES" -> {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ROOM DATABASE • RECIPE COSTING",
                  style = MaterialTheme.typography.labelSmall,
                  color = MutedChampagne,
                  letterSpacing = 1.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${roomRecipes.size} Room Persisted Recipes",
                  style = MaterialTheme.typography.titleSmall,
                  color = PorcelainIvory
                )
              }

              Button(
                onClick = { showAddRecipeDialog = true },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = RefinedTerracotta,
                  contentColor = PaperWhite
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_recipe_button")
              ) {
                Text("+ New Recipe", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          items(roomRecipes, key = { it.id }) { recipe ->
            RoomRecipeCard(
              recipe = recipe,
              onClick = { selectedRecipeForDetail = recipe },
              onDelete = { viewModel.deleteRoomRecipe(recipe) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }
  }

  if (showAddRecipeDialog) {
    AddRecipeDialog(
      onDismiss = { showAddRecipeDialog = false },
      onSave = { name, ings, cost, price ->
        viewModel.addRoomRecipe(name, ings, cost, price)
        showAddRecipeDialog = false
      }
    )
  }
}

@Composable
fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = CharcoalSurface,
    border = BorderStroke(1.dp, WarmBorderDark)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(title, style = MaterialTheme.typography.labelSmall, color = MutedLightText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(6.dp))
      Text(value, style = MaterialTheme.typography.headlineMedium, color = PorcelainIvory, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MutedSecondaryText, fontSize = 10.sp)
    }
  }
}

@Composable
fun IngredientRow(
  ingredient: Ingredient,
  onRestock: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    color = CharcoalSurface,
    border = BorderStroke(1.dp, if (ingredient.isLowStock) RefinedTerracotta else WarmBorderDark)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(ingredient.name, style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
          if (ingredient.isLowStock) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(color = RefinedTerracotta, shape = RoundedCornerShape(4.dp)) {
              Text("LOW", style = MaterialTheme.typography.labelSmall, color = PaperWhite, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
          }
        }
        Text(
          text = "Stock: ${String.format("%.1f", ingredient.currentStock)} ${ingredient.unit} (Min: ${ingredient.minThreshold} ${ingredient.unit})",
          style = MaterialTheme.typography.bodySmall,
          color = if (ingredient.isLowStock) RefinedTerracotta else MutedLightText
        )
        Text(
          text = "Supplier: ${ingredient.supplier} • ₹${String.format("%,.0f", ingredient.costPerUnit)}/${ingredient.unit}",
          style = MaterialTheme.typography.bodySmall,
          color = MutedSecondaryText,
          fontSize = 11.sp
        )
      }

      Button(
        onClick = onRestock,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CharcoalElevated, contentColor = MutedChampagne),
        border = BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Text("+ Restock", style = MaterialTheme.typography.labelSmall)
      }
    }
  }
}

@Composable
fun RoomRecipeCard(
  recipe: Recipe,
  profitThreshold: Double = 200.0,
  onClick: () -> Unit = {},
  onDelete: () -> Unit
) {
  val potentialProfitMargin = recipe.price - recipe.cost
  val isBelowThreshold = potentialProfitMargin < profitThreshold

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("owner_recipe_card_${recipe.id}"),
    shape = RoundedCornerShape(10.dp),
    color = if (isBelowThreshold) Color(0xFF221616) else CharcoalSurface,
    border = BorderStroke(
      width = if (isBelowThreshold) 1.5.dp else 1.dp,
      color = if (isBelowThreshold) Color(0xFFE53935) else WarmBorderDark
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(recipe.name, style = MaterialTheme.typography.titleMedium, color = PorcelainIvory)
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
          Text("Room Database ID: #${recipe.id}", style = MaterialTheme.typography.labelSmall, color = MutedSecondaryText, fontSize = 9.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = if (isBelowThreshold) Color(0xFF3E1C1C) else if (recipe.grossMarginPercent >= 60.0) SageGreenContainer else Color(0xFF2A231C),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(
              1.dp,
              if (isBelowThreshold) Color(0xFFEF5350)
              else if (recipe.grossMarginPercent >= 60.0) SageGreen else AntiqueBrass
            )
          ) {
            Text(
              text = "${String.format("%.1f", recipe.grossMarginPercent)}% Margin",
              style = MaterialTheme.typography.labelSmall,
              color = if (isBelowThreshold) Color(0xFFFF8A80) else if (recipe.grossMarginPercent >= 60.0) SageGreen else GoldAccent,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MutedSecondaryText, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Ingredients List Chips
      if (recipe.ingredients.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "INGREDIENTS LIST (${recipe.ingredients.size}):",
          style = MaterialTheme.typography.labelSmall,
          color = if (isBelowThreshold) Color(0xFFEF9A9A) else MutedChampagne,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          recipe.ingredients.forEach { ing ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CharcoalElevated,
              border = BorderStroke(1.dp, if (isBelowThreshold) Color(0xFF5A2525) else WarmBorderDark)
            ) {
              Text(
                text = ing,
                style = MaterialTheme.typography.labelSmall,
                color = MutedLightText,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Divider(color = if (isBelowThreshold) Color(0xFF5A2525) else WarmBorderDark)
      Spacer(modifier = Modifier.height(8.dp))

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
          Text(
            text = "₹${String.format("%,.0f", potentialProfitMargin)}",
            style = MaterialTheme.typography.titleMedium,
            color = if (isBelowThreshold) Color(0xFFEF5350) else GoldAccent,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun AddRecipeDialog(
  onDismiss: () -> Unit,
  onSave: (name: String, ingredients: List<String>, cost: Double, price: Double) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var ingredientsInput by remember { mutableStateOf("") }
  var costInput by remember { mutableStateOf("") }
  var priceInput by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalSurface,
    title = {
      Column {
        Text("CREATE ROOM RECIPE", style = MaterialTheme.typography.labelSmall, color = MutedChampagne, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
        Text("Financial Recipe Costing", style = MaterialTheme.typography.titleLarge, color = PorcelainIvory)
      }
    },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
          text = "Persists a new Recipe entity with fields: name, ingredients (list), cost, and price into the Room SQLite database.",
          style = MaterialTheme.typography.bodySmall,
          color = MutedLightText
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Recipe / Dish Name", color = MutedLightText) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("recipe_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PorcelainIvory,
            unfocusedTextColor = PorcelainIvory,
            focusedBorderColor = MutedChampagne,
            unfocusedBorderColor = WarmBorderDark
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = ingredientsInput,
          onValueChange = { ingredientsInput = it },
          label = { Text("Ingredients (comma separated)", color = MutedLightText) },
          placeholder = { Text("e.g. Saffron, Rice, Ghee, Shallots", color = MutedSecondaryText, fontSize = 12.sp) },
          modifier = Modifier.fillMaxWidth().testTag("recipe_ingredients_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PorcelainIvory,
            unfocusedTextColor = PorcelainIvory,
            focusedBorderColor = MutedChampagne,
            unfocusedBorderColor = WarmBorderDark
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = costInput,
            onValueChange = { costInput = it },
            label = { Text("Cost (₹)", color = MutedLightText) },
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("recipe_cost_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PorcelainIvory,
              unfocusedTextColor = PorcelainIvory,
              focusedBorderColor = MutedChampagne,
              unfocusedBorderColor = WarmBorderDark
            )
          )
          OutlinedTextField(
            value = priceInput,
            onValueChange = { priceInput = it },
            label = { Text("Price (₹)", color = MutedLightText) },
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("recipe_price_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PorcelainIvory,
              unfocusedTextColor = PorcelainIvory,
              focusedBorderColor = MutedChampagne,
              unfocusedBorderColor = WarmBorderDark
            )
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val cost = costInput.toDoubleOrNull() ?: 0.0
          val price = priceInput.toDoubleOrNull() ?: 0.0
          val ings = ingredientsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
          if (name.isNotBlank() && price > 0) {
            onSave(name, ings, cost, price)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RefinedTerracotta, contentColor = PaperWhite),
        modifier = Modifier.testTag("save_recipe_button")
      ) {
        Text("Save to Room DB", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = MutedLightText)
      }
    }
  )
}

