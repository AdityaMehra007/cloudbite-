package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.LuxuryButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@Composable
fun KitchenDisplayScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()
  val products by viewModel.filteredProducts.collectAsState()
  val selectedLoc by viewModel.selectedLocation.collectAsState()

  var selectedTab by remember { mutableStateOf("TICKETS") } // TICKETS or 86_ITEMS
  var activeStatusFilter by remember { mutableStateOf<OrderStatus?>(null) }

  val activeOrders = remember(orders, activeStatusFilter) {
    orders.filter { order ->
      if (activeStatusFilter != null) {
        order.status == activeStatusFilter
      } else {
        order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED
      }
    }
  }

  Scaffold(
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianCharcoal)
          .statusBarsPadding()
      ) {
        // High-contrast KDS header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E676)) // Live kitchen indicator
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "KDS • CENTRAL PRODUCTION LINE",
                style = MaterialTheme.typography.labelSmall,
                color = MutedChampagne,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${selectedLoc.name} (${activeOrders.size} Active)",
                style = MaterialTheme.typography.titleMedium,
                color = PorcelainIvory
              )
            }
          }

          // Tab Switcher
          Row(
            modifier = Modifier
              .background(CharcoalElevated, RoundedCornerShape(8.dp))
              .border(1.dp, WarmBorderDark, RoundedCornerShape(8.dp))
              .padding(2.dp)
          ) {
            TabButton("TICKETS", selectedTab == "TICKETS") { selectedTab = "TICKETS" }
            TabButton("86'd DISHES", selectedTab == "86_ITEMS") { selectedTab = "86_ITEMS" }
          }
        }

        // Operational Column Status Filter
        if (selectedTab == "TICKETS") {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            item {
              StatusFilterChip(
                label = "All Active (${orders.count { it.status != OrderStatus.DELIVERED }})",
                isSelected = activeStatusFilter == null,
                onClick = { activeStatusFilter = null }
              )
            }
            item {
              StatusFilterChip(
                label = "New Placed (${orders.count { it.status == OrderStatus.PLACED }})",
                isSelected = activeStatusFilter == OrderStatus.PLACED,
                onClick = { activeStatusFilter = OrderStatus.PLACED }
              )
            }
            item {
              StatusFilterChip(
                label = "Accepted (${orders.count { it.status == OrderStatus.ACCEPTED }})",
                isSelected = activeStatusFilter == OrderStatus.ACCEPTED,
                onClick = { activeStatusFilter = OrderStatus.ACCEPTED }
              )
            }
            item {
              StatusFilterChip(
                label = "Preparing (${orders.count { it.status == OrderStatus.PREPARING }})",
                isSelected = activeStatusFilter == OrderStatus.PREPARING,
                onClick = { activeStatusFilter = OrderStatus.PREPARING }
              )
            }
            item {
              StatusFilterChip(
                label = "Ready for Dispatch (${orders.count { it.status == OrderStatus.READY }})",
                isSelected = activeStatusFilter == OrderStatus.READY,
                onClick = { activeStatusFilter = OrderStatus.READY }
              )
            }
          }
        }
      }
    },
    containerColor = ObsidianCharcoal,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    if (selectedTab == "TICKETS") {
      if (activeOrders.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("👨‍🍳", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Production Board Clear",
              style = MaterialTheme.typography.titleMedium,
              color = PorcelainIvory
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "All kitchen tickets have been prepared and dispatched.",
              style = MaterialTheme.typography.bodySmall,
              color = MutedLightText
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
          items(activeOrders, key = { it.id }) { order ->
            KitchenTicketCard(
              order = order,
              onAdvanceStatus = { viewModel.advanceOrderStatus(order.id) },
              onTrackCustomerView = { viewModel.trackOrder(order.id) },
              modifier = Modifier.padding(vertical = 6.dp)
            )
          }
        }
      }
    } else {
      // 86'd Dishes / Out of Stock Control
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CharcoalElevated,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, WarmBorderDark)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "EMERGENCY 86'D DISH CONTROLS",
                style = MaterialTheme.typography.labelSmall,
                color = RefinedTerracotta,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Mark ingredients or dishes out of stock instantly across all digital menus when kitchen supplies run out.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedLightText
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        items(products) { product ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = CharcoalSurface,
            border = BorderStroke(1.dp, if (product.inStock) WarmBorderDark else RefinedTerracotta.copy(alpha = 0.6f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = product.name,
                  style = MaterialTheme.typography.titleSmall,
                  color = PorcelainIvory
                )
                Text(
                  text = "${product.brandName} • ₹${String.format("%,.0f", product.price)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MutedLightText
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (product.inStock) "ACTIVE" else "86'D",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (product.inStock) SageGreen else RefinedTerracotta,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                  checked = product.inStock,
                  onCheckedChange = { viewModel.toggleProductStock(product.id) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = SageGreen,
                    checkedTrackColor = SageGreenContainer,
                    uncheckedThumbColor = RefinedTerracotta,
                    uncheckedTrackColor = Color(0xFF331E1E)
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun KitchenTicketCard(
  order: Order,
  onAdvanceStatus: () -> Unit,
  onTrackCustomerView: () -> Unit,
  modifier: Modifier = Modifier
) {
  val elapsedMinutes = ((System.currentTimeMillis() - order.placedTimeMillis) / (60 * 1000)).coerceAtLeast(0)
  val isOverdue = elapsedMinutes > 20

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    color = CharcoalSurface,
    border = BorderStroke(
      width = if (isOverdue) 1.5.dp else 1.dp,
      color = if (isOverdue) RefinedTerracotta else WarmBorderDark
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Order ID, Elapsed Time, Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = order.id,
            style = MaterialTheme.typography.titleMedium,
            color = PorcelainIvory,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = if (isOverdue) RefinedTerracotta.copy(alpha = 0.2f) else CharcoalElevated,
            shape = RoundedCornerShape(4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.Timer,
                contentDescription = null,
                tint = if (isOverdue) RefinedTerracotta else MutedChampagne,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${elapsedMinutes}m elapsed",
                style = MaterialTheme.typography.labelSmall,
                color = if (isOverdue) RefinedTerracotta else MutedChampagne,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        StatusPill(status = order.status)
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Patron: ${order.customerName} (${order.deliveryArea})",
        style = MaterialTheme.typography.bodySmall,
        color = MutedLightText
      )

      if (order.kitchenNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          color = Color(0xFF2A231C),
          shape = RoundedCornerShape(4.dp),
          border = BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.5f))
        ) {
          Text(
            text = "★ CHEF NOTE: ${order.kitchenNotes}",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
          )
        }
      }

      Divider(color = WarmBorderDark, modifier = Modifier.padding(vertical = 10.dp))

      // Items list with quantities and modifications
      order.items.forEach { item ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
          verticalAlignment = Alignment.Top
        ) {
          Surface(
            color = CharcoalElevated,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "${item.quantity}x",
              style = MaterialTheme.typography.labelMedium,
              color = GoldAccent,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.product.name,
              style = MaterialTheme.typography.bodyMedium,
              color = PorcelainIvory,
              fontWeight = FontWeight.SemiBold
            )
            if (item.selectedModifiers.isNotEmpty()) {
              Text(
                text = item.selectedModifiers.values.joinToString(", ") { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MutedChampagne,
                fontSize = 11.sp
              )
            }
            if (item.specialInstructions.isNotBlank()) {
              Text(
                text = "• Note: ${item.specialInstructions}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEF9A9A),
                fontSize = 10.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Stage Advancement Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(onClick = onTrackCustomerView) {
          Text("Inspect Ticket", color = MutedLightText, style = MaterialTheme.typography.labelSmall)
        }

        val nextActionText = when (order.status) {
          OrderStatus.PLACED -> "Accept Order →"
          OrderStatus.ACCEPTED -> "Start Cooking →"
          OrderStatus.PREPARING -> "Mark as Ready →"
          OrderStatus.READY -> "Dispatch to Courier →"
          OrderStatus.OUT_FOR_DELIVERY -> "Confirm Delivery ✓"
          else -> "Completed"
        }

        Button(
          onClick = onAdvanceStatus,
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = when (order.status) {
              OrderStatus.PLACED -> AntiqueBrass
              OrderStatus.ACCEPTED -> Color(0xFFD48B30)
              OrderStatus.PREPARING -> SageGreen
              else -> MutedChampagne
            },
            contentColor = ObsidianCharcoal
          ),
          modifier = Modifier.testTag("kds_advance_${order.id}")
        ) {
          Text(
            text = nextActionText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun TabButton(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) MutedChampagne else Color.Transparent)
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) ObsidianCharcoal else PorcelainIvory,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun StatusFilterChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) MutedChampagne else CharcoalElevated,
    border = BorderStroke(1.dp, if (isSelected) MutedChampagne else WarmBorderDark),
    modifier = Modifier.clickable { onClick() }
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) ObsidianCharcoal else PorcelainIvory,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
    )
  }
}
