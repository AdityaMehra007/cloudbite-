package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      CloudBiteTheme(darkTheme = true) {
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        // Handle Back Button
        if (uiState.currentScreen != AppScreen.STOREFRONT) {
          BackHandler {
            viewModel.navigateTo(AppScreen.STOREFRONT)
          }
        }

        // Show toast messages as snackbars
        LaunchedEffect(uiState.toastMessage) {
          uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = ObsidianCharcoal,
          snackbarHost = {
            SnackbarHost(
              hostState = snackbarHostState,
              modifier = Modifier.navigationBarsPadding()
            ) { data ->
              Snackbar(
                snackbarData = data,
                containerColor = CharcoalElevated,
                contentColor = PorcelainIvory,
                actionColor = MutedChampagne
              )
            }
          },
          bottomBar = {
            NavigationBar(
              containerColor = CharcoalSurface,
              contentColor = PorcelainIvory,
              tonalElevation = 8.dp,
              modifier = Modifier.testTag("main_navigation_bar")
            ) {
              val screens = listOf(
                Triple(AppScreen.STOREFRONT, "Discover", Icons.Default.RestaurantMenu),
                Triple(AppScreen.ORDER_TRACKING, "Tracking", Icons.Default.Schedule),
                Triple(AppScreen.KITCHEN_DISPLAY, "Kitchen KDS", Icons.Default.SoupKitchen),
                Triple(AppScreen.OWNER_CONSOLE, "Console", Icons.Default.Analytics),
                Triple(AppScreen.AI_ASSISTANT, "AI Intel", Icons.Default.AutoAwesome)
              )

              screens.forEach { (screen, label, icon) ->
                val isSelected = uiState.currentScreen == screen
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { viewModel.navigateTo(screen) },
                  icon = {
                    Icon(
                      imageVector = icon,
                      contentDescription = label,
                      modifier = Modifier.size(20.dp)
                    )
                  },
                  label = {
                    Text(
                      text = label,
                      style = MaterialTheme.typography.labelSmall
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ObsidianCharcoal,
                    selectedTextColor = MutedChampagne,
                    unselectedIconColor = MutedLightText,
                    unselectedTextColor = MutedSecondaryText,
                    indicatorColor = MutedChampagne
                  ),
                  modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
                )
              }
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (uiState.currentScreen) {
              AppScreen.STOREFRONT -> {
                StorefrontScreen(
                  viewModel = viewModel,
                  onOpenProductDetail = { viewModel.openProductDetail(it) },
                  onOpenCart = { viewModel.toggleCart(true) },
                  onOpenSearch = { viewModel.toggleSearch(true) }
                )
              }
              AppScreen.ORDER_TRACKING -> {
                OrderTrackingScreen(viewModel = viewModel)
              }
              AppScreen.KITCHEN_DISPLAY -> {
                KitchenDisplayScreen(viewModel = viewModel)
              }
              AppScreen.OWNER_CONSOLE -> {
                OwnerConsoleScreen(viewModel = viewModel)
              }
              AppScreen.AI_ASSISTANT -> {
                AiAssistantScreen(viewModel = viewModel)
              }
            }

            // Product Detail Bottom Sheet
            uiState.selectedProductForDetail?.let { product ->
              ProductDetailSheet(
                product = product,
                onDismiss = { viewModel.closeProductDetail() },
                onAddToCart = { qty, modifiers, notes ->
                  viewModel.addToCart(product, qty, modifiers, notes)
                }
              )
            }

            // Cart & Checkout Bottom Sheet
            if (uiState.isCartOpen) {
              CartCheckoutSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.toggleCart(false) }
              )
            }

            // Search Modal Dialog
            if (uiState.isSearchOpen) {
              SearchDialog(
                viewModel = viewModel,
                onSelectProduct = { prod ->
                  viewModel.toggleSearch(false)
                  viewModel.openProductDetail(prod)
                },
                onDismiss = { viewModel.toggleSearch(false) }
              )
            }
          }
        }
      }
    }
  }
}
