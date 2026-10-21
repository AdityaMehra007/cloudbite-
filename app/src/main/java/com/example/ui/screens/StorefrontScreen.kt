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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Brand
import com.example.data.model.Category
import com.example.data.model.DietaryType
import com.example.data.model.Product
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun StorefrontScreen(
  viewModel: MainViewModel,
  onOpenProductDetail: (Product) -> Unit,
  onOpenCart: () -> Unit,
  onOpenSearch: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val selectedLoc by viewModel.selectedLocation.collectAsState()
  val products by viewModel.filteredProducts.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  var showLocationDialog by remember { mutableStateOf(false) }

  val totalCartCount = cartItems.sumOf { it.quantity }

  Scaffold(
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianCharcoal)
          .statusBarsPadding()
      ) {
        // Discreet Announcement Strip
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(CharcoalElevated)
            .padding(vertical = 6.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Thoughtfully prepared. Beautifully delivered. • Bengaluru Edition",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            letterSpacing = 0.5.sp
          )
        }

        // Primary Luxury Top Navigation
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CLOUDBITE",
                style = MaterialTheme.typography.titleLarge,
                color = PorcelainIvory,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = AntiqueBrass.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "BLACK LABEL",
                  style = MaterialTheme.typography.labelSmall,
                  color = MutedChampagne,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Clickable Location Hub Selector
            Row(
              modifier = Modifier
                .clickable { showLocationDialog = true }
                .padding(top = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Delivery Hub",
                tint = RefinedTerracotta,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${selectedLoc.name} ▾",
                style = MaterialTheme.typography.labelSmall,
                color = MutedLightText
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onOpenSearch,
              modifier = Modifier.testTag("search_button")
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = PorcelainIvory
              )
            }

            // Cart Button with live count pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CharcoalElevated)
                .border(1.dp, WarmBorderDark, RoundedCornerShape(20.dp))
                .clickable { onOpenCart() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("cart_header_button"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.ShoppingBag,
                  contentDescription = "Cart",
                  tint = MutedChampagne,
                  modifier = Modifier.size(18.dp)
                )
                if (totalCartCount > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(RefinedTerracotta)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "$totalCartCount",
                      style = MaterialTheme.typography.labelSmall,
                      color = PaperWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    },
    containerColor = ObsidianCharcoal,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      // 1. Signature Hero Section
      item {
        EditorialHeroSection(
          onExploreClick = { viewModel.setCategory("all") },
          onKitchensClick = { viewModel.setCategory("all") }
        )
      }

      // 2. Virtual Kitchen Brands Showcase
      item {
        Column(modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)) {
          SectionHeader(
            eyebrow = "Culinary Brands",
            title = "Different kitchens. Extraordinary character.",
            subtitle = "Artisanal food houses operating within our Bengaluru cloud hubs",
            modifier = Modifier.padding(horizontal = 16.dp)
          )
          Spacer(modifier = Modifier.height(14.dp))
          BrandCarousel(
            brands = viewModel.brands,
            selectedBrandId = uiState.selectedBrandId,
            onSelectBrand = { viewModel.selectBrand(it) }
          )
        }
      }

      // 3. Category Discovery Strip
      item {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
          SectionHeader(
            eyebrow = "The Curations",
            title = "An appetite for the exceptional.",
            modifier = Modifier.padding(horizontal = 16.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          CategoryFilterRow(
            categories = viewModel.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            onSelectCategory = { viewModel.setCategory(it) }
          )
          Spacer(modifier = Modifier.height(10.dp))
          DietaryFilterRow(
            selectedDietary = uiState.dietaryFilter,
            onSelectDietary = { viewModel.setDietaryFilter(it) }
          )
        }
      }

      // 4. Products Catalog Section
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Signature Dishes (${products.size})",
            style = MaterialTheme.typography.titleMedium,
            color = PorcelainIvory
          )
          if (uiState.selectedBrandId != null || uiState.selectedCategoryId != "all" || uiState.dietaryFilter != null) {
            Text(
              text = "Clear filters",
              style = MaterialTheme.typography.labelSmall,
              color = RefinedTerracotta,
              modifier = Modifier.clickable {
                viewModel.selectBrand(null)
                viewModel.setCategory("all")
                viewModel.setDietaryFilter(null)
              }
            )
          }
        }
      }

      // Products List Items
      if (products.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(48.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🍽️", fontSize = 40.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No dishes match current filters",
                style = MaterialTheme.typography.titleMedium,
                color = PorcelainIvory
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Try clearing brand or dietary preferences.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedLightText
              )
            }
          }
        }
      } else {
        items(products, key = { it.id }) { product ->
          ProductCard(
            product = product,
            onCardClick = { onOpenProductDetail(product) },
            onAddToCart = { viewModel.addToCart(product) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      }

      // 5. Seasonal Feature Banner
      item {
        SeasonalFeatureBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))
      }

      // 6. Brand Storytelling
      item {
        BrandStorySection(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
      }
    }
  }

  // Location Selector Dialog
  if (showLocationDialog) {
    LocationSelectorDialog(
      locations = viewModel.locations,
      currentLocation = selectedLoc,
      onSelect = {
        viewModel.setLocation(it)
        showLocationDialog = false
      },
      onDismiss = { showLocationDialog = false }
    )
  }
}

@Composable
fun EditorialHeroSection(
  onExploreClick: () -> Unit,
  onKitchensClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(
        Brush.verticalGradient(
          colors = listOf(CharcoalElevated, ObsidianCharcoal)
        )
      )
      .padding(horizontal = 16.dp, vertical = 24.dp)
  ) {
    Column {
      Surface(
        color = RefinedTerracotta.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, RefinedTerracotta.copy(alpha = 0.4f))
      ) {
        Text(
          text = "THE ART OF REFINED DINING",
          style = MaterialTheme.typography.labelSmall,
          color = RefinedTerracotta,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "A finer way to fall in love with food.",
        style = MaterialTheme.typography.displayMedium,
        color = PorcelainIvory
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Discover remarkable flavours, thoughtful preparation, and the kitchens bringing something special to your table.",
        style = MaterialTheme.typography.bodyLarge,
        color = MutedLightText,
        lineHeight = 22.sp
      )
      Spacer(modifier = Modifier.height(20.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LuxuryButton(
          text = "Explore Collection",
          onClick = onExploreClick,
          modifier = Modifier.testTag("explore_collection_button")
        )
        LuxuryButton(
          text = "Our Kitchens",
          onClick = onKitchensClick,
          isSecondary = true
        )
      }
    }
  }
}

@Composable
fun BrandCarousel(
  brands: List<Brand>,
  selectedBrandId: String?,
  onSelectBrand: (String?) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyRow(
    modifier = modifier.fillMaxWidth(),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      BrandPillCard(
        name = "All Brands",
        cuisine = "Curated Selection",
        badge = "Full Hub",
        isSelected = selectedBrandId == null,
        onClick = { onSelectBrand(null) }
      )
    }
    items(brands) { brand ->
      BrandPillCard(
        name = brand.name,
        cuisine = brand.cuisine,
        badge = brand.badge,
        isSelected = selectedBrandId == brand.id,
        onClick = { onSelectBrand(if (selectedBrandId == brand.id) null else brand.id) }
      )
    }
  }
}

@Composable
fun BrandPillCard(
  name: String,
  cuisine: String,
  badge: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .width(180.dp)
      .clickable { onClick() }
      .testTag("brand_pill_${name.replace(" ", "_")}"),
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) CharcoalElevated else Color(0xFF1B1917),
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) MutedChampagne else WarmBorderDark
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = badge.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          color = if (isSelected) GoldAccent else MutedSecondaryText,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold
        )
        if (isSelected) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(MutedChampagne)
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = name,
        style = MaterialTheme.typography.titleSmall,
        color = PorcelainIvory,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = cuisine,
        style = MaterialTheme.typography.bodySmall,
        color = MutedLightText,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun CategoryFilterRow(
  categories: List<Category>,
  selectedCategoryId: String,
  onSelectCategory: (String) -> Unit
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(categories) { category ->
      val isSelected = category.id == selectedCategoryId
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MutedChampagne else CharcoalElevated,
        border = BorderStroke(1.dp, if (isSelected) MutedChampagne else WarmBorderDark),
        modifier = Modifier.clickable { onSelectCategory(category.id) }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = category.iconEmoji, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) ObsidianCharcoal else PorcelainIvory,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        }
      }
    }
  }
}

@Composable
fun DietaryFilterRow(
  selectedDietary: DietaryType?,
  onSelectDietary: (DietaryType?) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    FilterChip(
      selected = selectedDietary == null,
      onClick = { onSelectDietary(null) },
      label = { Text("All Plates", style = MaterialTheme.typography.labelSmall) },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = CharcoalElevated,
        selectedLabelColor = MutedChampagne,
        containerColor = Color.Transparent,
        labelColor = MutedLightText
      ),
      border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selectedDietary == null,
        borderColor = WarmBorderDark,
        selectedBorderColor = MutedChampagne
      )
    )

    FilterChip(
      selected = selectedDietary == DietaryType.VEGETARIAN,
      onClick = {
        onSelectDietary(if (selectedDietary == DietaryType.VEGETARIAN) null else DietaryType.VEGETARIAN)
      },
      label = { Text("🌿 Veg Only", style = MaterialTheme.typography.labelSmall) },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = SageGreenContainer,
        selectedLabelColor = SageGreen,
        containerColor = Color.Transparent,
        labelColor = MutedLightText
      ),
      border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selectedDietary == DietaryType.VEGETARIAN,
        borderColor = WarmBorderDark,
        selectedBorderColor = SageGreen
      )
    )

    FilterChip(
      selected = selectedDietary == DietaryType.NON_VEGETARIAN,
      onClick = {
        onSelectDietary(if (selectedDietary == DietaryType.NON_VEGETARIAN) null else DietaryType.NON_VEGETARIAN)
      },
      label = { Text("🥩 Non-Veg", style = MaterialTheme.typography.labelSmall) },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = CharcoalElevated,
        selectedLabelColor = Color(0xFFEF9A9A),
        containerColor = Color.Transparent,
        labelColor = MutedLightText
      ),
      border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selectedDietary == DietaryType.NON_VEGETARIAN,
        borderColor = WarmBorderDark,
        selectedBorderColor = Color(0xFFEF9A9A)
      )
    )
  }
}

@Composable
fun ProductCard(
  product: Product,
  onCardClick: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(12.dp),
    color = CharcoalSurface,
    border = BorderStroke(1.dp, WarmBorderDark)
  ) {
    Column {
      // Dish Visual & Badges
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
      ) {
        DishCanvasIllustration(
          productId = product.id,
          modifier = Modifier.fillMaxSize()
        )

        // Top Badges
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          DietaryBadge(dietaryType = product.dietaryType)

          if (product.chefSignature) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = ObsidianCharcoal.copy(alpha = 0.85f),
              border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "★ CHEF SIGNATURE",
                  style = MaterialTheme.typography.labelSmall,
                  color = GoldAccent,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // Content & Action
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = product.brandName.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          color = AntiqueBrass,
          letterSpacing = 1.sp,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = product.name,
          style = MaterialTheme.typography.titleMedium,
          color = PorcelainIvory
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = product.description,
          style = MaterialTheme.typography.bodySmall,
          color = MutedLightText,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${product.portion} • ${product.prepTimeMinutes} mins",
            style = MaterialTheme.typography.labelSmall,
            color = MutedSecondaryText,
            fontSize = 10.sp
          )
          Text(
            text = "${product.calories} kcal • ${product.proteinGrams}g protein",
            style = MaterialTheme.typography.labelSmall,
            color = MutedSecondaryText,
            fontSize = 10.sp
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          PriceText(price = product.price, compareAtPrice = product.compareAtPrice)

          Button(
            onClick = onAddToCart,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = RefinedTerracotta,
              contentColor = PaperWhite
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier
              .defaultMinSize(minHeight = 36.dp)
              .testTag("add_button_${product.id}")
          ) {
            Text(
              text = if (product.modifierGroups.isNotEmpty()) "Customize +" else "Add +",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun SeasonalFeatureBanner(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = CharcoalElevated,
    border = BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "SEASONAL FEATURE",
        style = MaterialTheme.typography.labelSmall,
        color = MutedChampagne,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "An occasion, even on an ordinary Tuesday.",
        style = MaterialTheme.typography.headlineMedium,
        color = PorcelainIvory
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Order any two chef signature entrees and enjoy a complimentary bottle of our 24-hour estate nitro cold brew, temperature-packaged with bespoke crystal glassware seal.",
        style = MaterialTheme.typography.bodyMedium,
        color = MutedLightText
      )
    }
  }
}

@Composable
fun BrandStorySection(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(CharcoalElevated),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "❦", color = MutedChampagne, fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.height(10.dp))
    Text(
      text = "Good food is never just food.",
      style = MaterialTheme.typography.headlineMedium,
      color = PorcelainIvory
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "It's the care behind every ingredient, the craft behind every recipe, and the little details that turn an ordinary meal into something worth remembering.",
      style = MaterialTheme.typography.bodyMedium,
      color = MutedLightText,
      modifier = Modifier.padding(horizontal = 16.dp),
      lineHeight = 22.sp
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
      text = "CLOUDBITE BLACK LABEL • BENGALURU CULINARY CONSORTIUM",
      style = MaterialTheme.typography.labelSmall,
      color = MutedSecondaryText,
      letterSpacing = 1.5.sp,
      fontSize = 9.sp
    )
  }
}

@Composable
fun LocationSelectorDialog(
  locations: List<com.example.data.model.BengaluruLocation>,
  currentLocation: com.example.data.model.BengaluruLocation,
  onSelect: (com.example.data.model.BengaluruLocation) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalSurface,
    titleContentColor = PorcelainIvory,
    title = {
      Text(
        text = "Select Bengaluru Delivery Hub",
        style = MaterialTheme.typography.titleLarge
      )
    },
    text = {
      Column {
        Text(
          text = "Select your neighborhood to route from our nearest dedicated cloud kitchen hub for fastest artisanal dispatch.",
          style = MaterialTheme.typography.bodySmall,
          color = MutedLightText
        )
        Spacer(modifier = Modifier.height(12.dp))
        locations.forEach { loc ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable { onSelect(loc) },
            shape = RoundedCornerShape(8.dp),
            color = if (loc.id == currentLocation.id) CharcoalElevated else Color.Transparent,
            border = BorderStroke(
              1.dp,
              if (loc.id == currentLocation.id) MutedChampagne else WarmBorderDark
            )
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = loc.name,
                  style = MaterialTheme.typography.titleSmall,
                  color = PorcelainIvory
                )
                Text(
                  text = "${loc.zone} • ~${loc.avgDeliveryMins} mins dispatch",
                  style = MaterialTheme.typography.bodySmall,
                  color = MutedLightText,
                  fontSize = 11.sp
                )
              }
              if (loc.id == currentLocation.id) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = MutedChampagne,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Done", color = MutedChampagne)
      }
    }
  )
}
