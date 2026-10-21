package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.LuxuryButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val orders by viewModel.orders.collectAsState()

  // Track either the explicitly selected order or the latest order
  val activeOrder = orders.firstOrNull { it.id == uiState.activeTrackedOrderId }
    ?: orders.firstOrNull()

  Scaffold(
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianCharcoal)
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo(AppScreen.STOREFRONT) },
          modifier = Modifier.testTag("back_to_storefront")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PorcelainIvory)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "LIVE ORDER CONCIERGE",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = activeOrder?.id ?: "No Active Order",
            style = MaterialTheme.typography.titleLarge,
            color = PorcelainIvory
          )
        }
      }
    },
    containerColor = ObsidianCharcoal,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    if (activeOrder == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("📦", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text("No orders placed yet", style = MaterialTheme.typography.titleMedium, color = PorcelainIvory)
          Spacer(modifier = Modifier.height(16.dp))
          LuxuryButton(text = "Go to Storefront", onClick = { viewModel.navigateTo(AppScreen.STOREFRONT) })
        }
      }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .padding(bottom = 80.dp)
      ) {
        // Status Hero Card
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = CharcoalSurface,
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              StatusPill(status = activeOrder.status)
              Text(
                text = "ETA: ~${activeOrder.estimatedDeliveryMinutes} MINS",
                style = MaterialTheme.typography.labelMedium,
                color = MutedChampagne,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = when (activeOrder.status) {
                OrderStatus.PLACED -> "Order confirmed by concierge."
                OrderStatus.ACCEPTED -> "Ticket accepted by master chef."
                OrderStatus.PREPARING -> "Artisanal preparation underway in cloud kitchen."
                OrderStatus.READY -> "Quality inspected & packed in luxury thermal container."
                OrderStatus.OUT_FOR_DELIVERY -> "En route via dedicated Black Label courier."
                OrderStatus.DELIVERED -> "Handcrafted delivery completed. Bon appétit!"
                OrderStatus.CANCELLED -> "Order has been cancelled."
              },
              style = MaterialTheme.typography.headlineSmall,
              color = PorcelainIvory
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Delivering to ${activeOrder.deliveryAddress} (${activeOrder.deliveryArea})",
              style = MaterialTheme.typography.bodySmall,
              color = MutedLightText
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Stepper Timeline
        Text(
          text = "FULFILLMENT TIMELINE",
          style = MaterialTheme.typography.labelSmall,
          color = MutedChampagne,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = CharcoalElevated,
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            val steps = listOf(
              Triple("Order Confirmed", "Concierge logged request", 0),
              Triple("Kitchen Accepted", "Master chef queued ticket", 1),
              Triple("Artisan Preparation", "Slow cooked & hand-crafted", 2),
              Triple("Sealed & Inspected", "Gold seal & temperature check", 3),
              Triple("Out for Delivery", activeOrder.courierName, 4),
              Triple("Delivered", "Delivered to patron", 5)
            )

            steps.forEachIndexed { index, (title, desc, stepIdx) ->
              val isCompleted = activeOrder.status.stepIndex >= stepIdx
              val isCurrent = activeOrder.status.stepIndex == stepIdx

              Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(
                        if (isCompleted) MutedChampagne else CharcoalSurface
                      )
                      .border(
                        1.dp,
                        if (isCompleted || isCurrent) MutedChampagne else WarmBorderDark,
                        CircleShape
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isCompleted) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = ObsidianCharcoal,
                        modifier = Modifier.size(14.dp)
                      )
                    }
                  }

                  if (index < steps.size - 1) {
                    Box(
                      modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(
                          if (activeOrder.status.stepIndex > stepIdx) MutedChampagne else WarmBorderDark
                        )
                    )
                  }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)) {
                  Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isCompleted || isCurrent) PorcelainIvory else MutedSecondaryText,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                  )
                  Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted || isCurrent) MutedLightText else MutedSecondaryText,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }

        // Courier Contact Card
        Spacer(modifier = Modifier.height(18.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          color = CharcoalElevated,
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(CharcoalSurface)
                .border(1.dp, AntiqueBrass, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = MutedChampagne)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("Dedicated Dispatch Partner", style = MaterialTheme.typography.labelSmall, color = AntiqueBrass, fontSize = 9.sp)
              Text(activeOrder.courierName, style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
              Text("Insulated thermal carrier • Sanitized seal", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
            }
          }
        }

        // Ordered Items Summary
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "ORDER SUMMARY (${activeOrder.items.size} ITEMS)",
          style = MaterialTheme.typography.labelSmall,
          color = MutedChampagne,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          color = CharcoalElevated,
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            activeOrder.items.forEach { item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "${item.quantity}x ${item.product.name}",
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
                }
                Text(
                  text = "₹${String.format("%,.0f", item.totalPrice)}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = PorcelainIvory,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Divider(color = WarmBorderDark, modifier = Modifier.padding(vertical = 8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Total Paid (${activeOrder.paymentMethod.title})", style = MaterialTheme.typography.bodyMedium, color = MutedLightText)
              Text("₹${String.format("%,.0f", activeOrder.totalAmount)}", style = MaterialTheme.typography.titleMedium, color = MutedChampagne, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Actions
        Spacer(modifier = Modifier.height(24.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          LuxuryButton(
            text = "Order Again",
            onClick = { viewModel.navigateTo(AppScreen.STOREFRONT) },
            modifier = Modifier.weight(1f)
          )
          LuxuryButton(
            text = "KDS Kitchen View",
            onClick = { viewModel.navigateTo(AppScreen.KITCHEN_DISPLAY) },
            isSecondary = true,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}
