package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.data.model.CartItem
import com.example.data.model.PaymentMethod
import com.example.ui.MainViewModel
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutSheet(
  viewModel: MainViewModel,
  onDismiss: () -> Unit
) {
  val cartItems by viewModel.cartItems.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val selectedLoc by viewModel.selectedLocation.collectAsState()

  var customerName by remember { mutableStateOf("Vikramaditya Roy") }
  var customerPhone by remember { mutableStateOf("+91 98450 12894") }
  var deliveryAddress by remember { mutableStateOf("Villa 14, Defense Colony, Indiranagar") }
  var kitchenNotes by remember { mutableStateOf("VIP Packaging with seal") }
  var couponInput by remember { mutableStateOf("") }
  var selectedPayment by remember { mutableStateOf(PaymentMethod.BLACK_LABEL_CARD) }

  val subtotal = viewModel.getCartSubtotal()
  val discount = viewModel.getCartDiscount()
  val packaging = viewModel.getPackagingFee()
  val delivery = viewModel.getDeliveryFee()
  val gst = viewModel.getGstTax()
  val grandTotal = viewModel.getCartGrandTotal()

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
        .padding(bottom = 36.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "YOUR ORDER",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Curated Selections",
            style = MaterialTheme.typography.titleLarge,
            color = PorcelainIvory
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = PorcelainIvory)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (cartItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🍷", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Your banquet table is empty",
              style = MaterialTheme.typography.titleMedium,
              color = PorcelainIvory
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Explore our artisanal menus to add extraordinary dishes.",
              style = MaterialTheme.typography.bodySmall,
              color = MutedLightText
            )
            Spacer(modifier = Modifier.height(20.dp))
            LuxuryButton(text = "Explore Menu", onClick = onDismiss)
          }
        }
      } else {
        // Items list
        cartItems.forEach { item ->
          CartItemRow(
            item = item,
            onIncrease = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
            onDecrease = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
            onRemove = { viewModel.removeCartItem(item.id) }
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        // Coupon Privilege Code Section
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = CharcoalElevated,
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "PATRON PRIVILEGE CODE",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = couponInput,
                onValueChange = { couponInput = it },
                placeholder = { Text("e.g. BLACKLABEL, FIRSTBITE", color = MutedSecondaryText, fontSize = 12.sp) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("coupon_input"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = PorcelainIvory,
                  unfocusedTextColor = PorcelainIvory,
                  focusedBorderColor = MutedChampagne,
                  unfocusedBorderColor = WarmBorderDark
                ),
                singleLine = true
              )
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = {
                  if (couponInput.isNotBlank()) {
                    viewModel.applyCoupon(couponInput)
                    couponInput = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = CharcoalSurface,
                  contentColor = MutedChampagne
                ),
                border = BorderStroke(1.dp, MutedChampagne),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("apply_coupon_button")
              ) {
                Text("Apply", style = MaterialTheme.typography.labelMedium)
              }
            }

            if (appliedCoupon != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "✓ Code '$appliedCoupon' active (-₹${String.format("%,.0f", discount)})",
                  style = MaterialTheme.typography.bodySmall,
                  color = SageGreen,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Remove",
                  style = MaterialTheme.typography.labelSmall,
                  color = RefinedTerracotta,
                  modifier = Modifier.clickable { viewModel.removeCoupon() }
                )
              }
            }
          }
        }

        // Delivery Details
        Spacer(modifier = Modifier.height(18.dp))
        Text(
          text = "DELIVERY DESTINATION",
          style = MaterialTheme.typography.labelSmall,
          color = MutedChampagne,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = customerName,
          onValueChange = { customerName = it },
          label = { Text("Patron Name", color = MutedLightText) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PorcelainIvory,
            unfocusedTextColor = PorcelainIvory,
            focusedBorderColor = MutedChampagne,
            unfocusedBorderColor = WarmBorderDark
          ),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = customerPhone,
          onValueChange = { customerPhone = it },
          label = { Text("Contact Phone", color = MutedLightText) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PorcelainIvory,
            unfocusedTextColor = PorcelainIvory,
            focusedBorderColor = MutedChampagne,
            unfocusedBorderColor = WarmBorderDark
          ),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = deliveryAddress,
          onValueChange = { deliveryAddress = it },
          label = { Text("Delivery Address (${selectedLoc.name})", color = MutedLightText) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PorcelainIvory,
            unfocusedTextColor = PorcelainIvory,
            focusedBorderColor = MutedChampagne,
            unfocusedBorderColor = WarmBorderDark
          ),
          maxLines = 2
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = kitchenNotes,
          onValueChange = { kitchenNotes = it },
          label = { Text("Delivery & Packing Instructions", color = MutedLightText) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PorcelainIvory,
            unfocusedTextColor = PorcelainIvory,
            focusedBorderColor = MutedChampagne,
            unfocusedBorderColor = WarmBorderDark
          ),
          singleLine = true
        )

        // Payment Method
        Spacer(modifier = Modifier.height(18.dp))
        Text(
          text = "PAYMENT METHOD",
          style = MaterialTheme.typography.labelSmall,
          color = MutedChampagne,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        PaymentMethod.values().forEach { method ->
          val isSelected = selectedPayment == method
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable { selectedPayment = method }
              .testTag("payment_${method.name}"),
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) CharcoalElevated else Color.Transparent,
            border = BorderStroke(
              1.dp,
              if (isSelected) MutedChampagne else WarmBorderDark
            )
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { selectedPayment = method },
                colors = RadioButtonDefaults.colors(
                  selectedColor = MutedChampagne,
                  unselectedColor = MutedSecondaryText
                )
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(method.title, style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
                Text(method.subtitle, style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
              }
            }
          }
        }

        // Bill Breakdown
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          color = CharcoalElevated,
          border = BorderStroke(1.dp, WarmBorderDark)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "BILL SUMMARY",
              style = MaterialTheme.typography.labelSmall,
              color = MutedChampagne,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            BillRow("Item Subtotal", "₹${String.format("%,.0f", subtotal)}")
            if (discount > 0) {
              BillRow("Patron Privilege Discount", "-₹${String.format("%,.0f", discount)}", color = SageGreen)
            }
            BillRow("Gourmet Bento Packaging", "₹${String.format("%,.0f", packaging)}")
            BillRow(
              "Artisanal Dispatch (${selectedLoc.zone})",
              if (delivery == 0.0) "COMPLIMENTARY" else "₹${String.format("%,.0f", delivery)}",
              color = if (delivery == 0.0) SageGreen else PorcelainIvory
            )
            BillRow("GST (5% Hospitality)", "₹${String.format("%,.2f", gst)}")
            Divider(color = WarmBorderDark, modifier = Modifier.padding(vertical = 10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Total Payable",
                style = MaterialTheme.typography.titleMedium,
                color = PorcelainIvory,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "₹${String.format("%,.0f", grandTotal)}",
                style = MaterialTheme.typography.titleLarge,
                color = MutedChampagne,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Place Order Action
        Spacer(modifier = Modifier.height(24.dp))
        Button(
          onClick = {
            viewModel.placeOrder(
              customerName = customerName,
              customerPhone = customerPhone,
              address = deliveryAddress,
              paymentMethod = selectedPayment,
              kitchenNotes = kitchenNotes
            )
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("place_order_button"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = RefinedTerracotta,
            contentColor = PaperWhite
          )
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Dispatch Order • ₹${String.format("%,.0f", grandTotal)}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun CartItemRow(
  item: CartItem,
  onIncrease: () -> Unit,
  onDecrease: () -> Unit,
  onRemove: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    color = CharcoalElevated,
    border = BorderStroke(1.dp, WarmBorderDark)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.product.name,
          style = MaterialTheme.typography.titleSmall,
          color = PorcelainIvory
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
            text = "Note: ${item.specialInstructions}",
            style = MaterialTheme.typography.bodySmall,
            color = MutedLightText,
            fontSize = 10.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "₹${String.format("%,.0f", item.totalPrice)}",
          style = MaterialTheme.typography.titleSmall,
          color = MutedChampagne,
          fontWeight = FontWeight.Bold
        )
      }

      // Quantity Controls
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .background(CharcoalSurface, RoundedCornerShape(6.dp))
          .border(1.dp, WarmBorderDark, RoundedCornerShape(6.dp))
          .padding(horizontal = 4.dp, vertical = 2.dp)
      ) {
        IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
          Text("−", color = PorcelainIvory, fontWeight = FontWeight.Bold)
        }
        Text(
          text = "${item.quantity}",
          style = MaterialTheme.typography.titleSmall,
          color = PorcelainIvory,
          modifier = Modifier.padding(horizontal = 6.dp)
        )
        IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
          Text("+", color = PorcelainIvory, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.width(6.dp))
      IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
        Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = MutedSecondaryText, modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
fun BillRow(
  label: String,
  value: String,
  color: Color = PorcelainIvory
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = label, style = MaterialTheme.typography.bodySmall, color = MutedLightText)
    Text(text = value, style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.SemiBold)
  }
}
