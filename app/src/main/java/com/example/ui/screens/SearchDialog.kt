package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.MainViewModel
import com.example.ui.components.DietaryBadge
import com.example.ui.components.PriceText
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
  viewModel: MainViewModel,
  onSelectProduct: (Product) -> Unit,
  onDismiss: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val products by viewModel.filteredProducts.collectAsState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = CharcoalSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "CURATION DISCOVERY",
          style = MaterialTheme.typography.labelSmall,
          color = MutedChampagne,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = PorcelainIvory)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      OutlinedTextField(
        value = uiState.searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MutedChampagne) },
        placeholder = { Text("Search biryani, truffle pasta, wagyu burger, matcha...", color = MutedSecondaryText, fontSize = 13.sp) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_text_field"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = PorcelainIvory,
          unfocusedTextColor = PorcelainIvory,
          focusedBorderColor = MutedChampagne,
          unfocusedBorderColor = WarmBorderDark
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "Found ${products.size} matching items",
        style = MaterialTheme.typography.labelSmall,
        color = MutedLightText
      )
      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        modifier = Modifier.heightIn(max = 400.dp)
      ) {
        items(products) { prod ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable {
                onSelectProduct(prod)
                onDismiss()
              },
            shape = RoundedCornerShape(8.dp),
            color = CharcoalElevated,
            border = BorderStroke(1.dp, WarmBorderDark)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  DietaryBadge(dietaryType = prod.dietaryType)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(prod.name, style = MaterialTheme.typography.titleSmall, color = PorcelainIvory)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("${prod.brandName} • ${prod.portion}", style = MaterialTheme.typography.bodySmall, color = MutedLightText, fontSize = 11.sp)
              }
              PriceText(price = prod.price)
            }
          }
        }
      }
    }
  }
}
