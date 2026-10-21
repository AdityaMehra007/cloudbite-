package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.Recipe
import com.example.data.local.RecipeDao
import com.example.data.model.*
import com.example.data.repository.CloudBiteRepository
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val label: String) {
  STOREFRONT("Storefront"),
  ORDER_TRACKING("Live Tracking"),
  KITCHEN_DISPLAY("Kitchen KDS"),
  OWNER_CONSOLE("Owner Console"),
  AI_ASSISTANT("AI Intelligence")
}

data class UiState(
  val currentScreen: AppScreen = AppScreen.STOREFRONT,
  val selectedCategoryId: String = "all",
  val selectedBrandId: String? = null,
  val searchQuery: String = "",
  val dietaryFilter: DietaryType? = null,
  val selectedProductForDetail: Product? = null,
  val isCartOpen: Boolean = false,
  val isSearchOpen: Boolean = false,
  val activeTrackedOrderId: String? = null,
  val toastMessage: String? = null,
  val aiInputQuery: String = "",
  val isAiThinking: Boolean = false
)

class MainViewModel @JvmOverloads constructor(
  application: Application,
  private val repository: CloudBiteRepository = CloudBiteRepository(),
  private val recipeRepository: RecipeRepository = RecipeRepository(AppDatabase.getDatabase(application).recipeDao())
) : AndroidViewModel(application) {

  val roomRecipes: StateFlow<List<Recipe>> = recipeRepository.allRecipes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      recipeRepository.seedInitialRecipesIfEmpty()
    }
  }

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  val locations = repository.locations
  val selectedLocation = repository.selectedLocation
  val brands = repository.brands
  val categories = repository.categories
  val ingredients = repository.ingredients
  val orders = repository.orders
  val cartItems = repository.cartItems
  val appliedCoupon = repository.appliedCoupon

  // Filtered Products
  val filteredProducts: StateFlow<List<Product>> = combine(
    repository.products,
    _uiState
  ) { products, state ->
    products.filter { prod ->
      val matchesCategory = state.selectedCategoryId == "all" || prod.categoryId == state.selectedCategoryId
      val matchesBrand = state.selectedBrandId == null || prod.brandId == state.selectedBrandId
      val matchesDietary = state.dietaryFilter == null || prod.dietaryType == state.dietaryFilter
      val matchesSearch = if (state.searchQuery.isBlank()) true else {
        prod.name.contains(state.searchQuery, ignoreCase = true) ||
          prod.description.contains(state.searchQuery, ignoreCase = true) ||
          prod.brandName.contains(state.searchQuery, ignoreCase = true) ||
          prod.categoryName.contains(state.searchQuery, ignoreCase = true)
      }
      matchesCategory && matchesBrand && matchesDietary && matchesSearch
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.products.value)

  // AI Chat Conversation history
  private val _aiMessages = MutableStateFlow(
    listOf(
      "AI Assistant" to "Good day, Chef & Operator. I am your CloudBite Black Label Intelligence Partner. I have direct access to your real-time recipe margins, live inventory thresholds, KDS ticket velocity, and Bengaluru multi-hub delivery performance. How may I assist your operations today?"
    )
  )
  val aiMessages: StateFlow<List<Pair<String, String>>> = _aiMessages.asStateFlow()

  fun navigateTo(screen: AppScreen) {
    _uiState.value = _uiState.value.copy(currentScreen = screen)
  }

  fun setCategory(categoryId: String) {
    _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
  }

  fun selectBrand(brandId: String?) {
    _uiState.value = _uiState.value.copy(selectedBrandId = brandId)
  }

  fun setSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun setDietaryFilter(dietary: DietaryType?) {
    _uiState.value = _uiState.value.copy(dietaryFilter = dietary)
  }

  fun openProductDetail(product: Product) {
    _uiState.value = _uiState.value.copy(selectedProductForDetail = product)
  }

  fun closeProductDetail() {
    _uiState.value = _uiState.value.copy(selectedProductForDetail = null)
  }

  fun toggleCart(open: Boolean) {
    _uiState.value = _uiState.value.copy(isCartOpen = open)
  }

  fun toggleSearch(open: Boolean) {
    _uiState.value = _uiState.value.copy(isSearchOpen = open)
  }

  fun setLocation(location: BengaluruLocation) {
    repository.setLocation(location)
    showToast("Delivery hub switched to ${location.name}")
  }

  // Cart actions
  fun addToCart(product: Product, quantity: Int = 1, modifiers: Map<String, ModifierOption> = emptyMap(), notes: String = "") {
    repository.addToCart(product, quantity, modifiers, notes)
    showToast("Added ${product.name} to cart")
  }

  fun updateCartQuantity(cartItemId: String, qty: Int) {
    repository.updateCartItemQuantity(cartItemId, qty)
  }

  fun removeCartItem(cartItemId: String) {
    repository.removeCartItem(cartItemId)
  }

  fun applyCoupon(code: String) {
    val success = repository.applyCoupon(code)
    if (success) {
      showToast("Privilege code '$code' applied successfully!")
    } else {
      showToast("Invalid code. Try 'BLACKLABEL' or 'FIRSTBITE'.")
    }
  }

  fun removeCoupon() {
    repository.removeCoupon()
    showToast("Coupon removed")
  }

  fun placeOrder(
    customerName: String,
    customerPhone: String,
    address: String,
    paymentMethod: PaymentMethod,
    kitchenNotes: String
  ) {
    val createdOrder = repository.placeOrder(customerName, customerPhone, address, paymentMethod, kitchenNotes)
    _uiState.value = _uiState.value.copy(
      isCartOpen = false,
      activeTrackedOrderId = createdOrder.id,
      currentScreen = AppScreen.ORDER_TRACKING
    )
    showToast("Order #${createdOrder.id} dispatched to kitchen!")
  }

  fun trackOrder(orderId: String) {
    _uiState.value = _uiState.value.copy(
      activeTrackedOrderId = orderId,
      currentScreen = AppScreen.ORDER_TRACKING
    )
  }

  // Kitchen operations
  fun advanceOrderStatus(orderId: String) {
    val order = orders.value.firstOrNull { it.id == orderId } ?: return
    val nextStatus = when (order.status) {
      OrderStatus.PLACED -> OrderStatus.ACCEPTED
      OrderStatus.ACCEPTED -> OrderStatus.PREPARING
      OrderStatus.PREPARING -> OrderStatus.READY
      OrderStatus.READY -> OrderStatus.OUT_FOR_DELIVERY
      OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
      else -> order.status
    }
    repository.updateOrderStatus(orderId, nextStatus)
    showToast("Order #${order.id} status updated to ${nextStatus.displayName}")
  }

  fun toggleProductStock(productId: String) {
    repository.toggleProductStock(productId)
    showToast("Item availability toggled")
  }

  fun restockIngredient(ingredientId: String, amount: Double) {
    repository.restockIngredient(ingredientId, amount)
    showToast("Restocked ingredient inventory")
  }

  fun getRecipeCosting(): List<RecipeItem> = repository.getRecipeCosting()

  fun addRoomRecipe(
    name: String,
    ingredients: List<String>,
    cost: Double,
    price: Double,
    imageUri: String? = null
  ) {
    viewModelScope.launch {
      recipeRepository.insert(
        Recipe(
          name = name,
          ingredients = ingredients,
          cost = cost,
          price = price,
          imageUri = imageUri
        )
      )
      showToast("Persisted recipe '$name' to Room database")
    }
  }

  fun updateRoomRecipe(recipe: Recipe) {
    viewModelScope.launch {
      recipeRepository.update(recipe)
      showToast("Updated recipe '${recipe.name}' in Room database")
    }
  }

  fun deleteRoomRecipe(recipe: Recipe) {
    viewModelScope.launch {
      recipeRepository.delete(recipe)
      showToast("Removed recipe '${recipe.name}' from Room database")
    }
  }

  fun getCartSubtotal() = repository.getCartSubtotal()
  fun getCartDiscount() = repository.getCartDiscount()
  fun getPackagingFee() = repository.getPackagingFee()
  fun getDeliveryFee() = repository.getDeliveryFee()
  fun getGstTax() = repository.getGstTax()
  fun getCartGrandTotal() = repository.getCartGrandTotal()

  // AI interaction
  fun setAiQuery(query: String) {
    _uiState.value = _uiState.value.copy(aiInputQuery = query)
  }

  fun sendAiQuery(promptOverride: String? = null) {
    val text = (promptOverride ?: _uiState.value.aiInputQuery).trim()
    if (text.isBlank()) return

    val currentHistory = _aiMessages.value.toMutableList()
    currentHistory.add("Operator" to text)
    _aiMessages.value = currentHistory
    _uiState.value = _uiState.value.copy(aiInputQuery = "", isAiThinking = true)

    viewModelScope.launch {
      // Simulate intelligent fast synthesis
      val response = repository.generateAiResponse(text)
      currentHistory.add("AI Assistant" to response)
      _aiMessages.value = currentHistory
      _uiState.value = _uiState.value.copy(isAiThinking = false)
    }
  }

  fun showToast(msg: String) {
    _uiState.value = _uiState.value.copy(toastMessage = msg)
  }

  fun clearToast() {
    _uiState.value = _uiState.value.copy(toastMessage = null)
  }
}
