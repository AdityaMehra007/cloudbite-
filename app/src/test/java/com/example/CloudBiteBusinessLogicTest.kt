package com.example

import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.repository.CloudBiteRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CloudBiteBusinessLogicTest {

  private lateinit var repository: CloudBiteRepository

  @Before
  fun setUp() {
    repository = CloudBiteRepository()
  }

  @Test
  fun testSeedBrandsAndProductsLoaded() {
    val brands = repository.brands
    assertEquals(5, brands.size)

    val products = repository.products.value
    assertTrue("Products should contain signature dishes", products.size >= 8)

    val biryani = products.first { it.id == "prod_biryani" }
    assertEquals("Royal Dum Awadhi Biryani", biryani.name)
    assertTrue("Biryani should have modifier options", biryani.modifierGroups.isNotEmpty())
  }

  @Test
  fun testCartPricingAndPrivilegeCouponCalculation() {
    val products = repository.products.value
    val pasta = products.first { it.id == "prod_mushroom_pasta" }

    // Add 2 pastas
    repository.addToCart(pasta, quantity = 2)
    assertEquals(1380.0, repository.getCartSubtotal(), 0.01)

    // Test BLACKLABEL coupon (15% discount)
    val valid = repository.applyCoupon("BLACKLABEL")
    assertTrue(valid)
    val discount = repository.getCartDiscount()
    assertEquals(207.0, discount, 0.01) // 15% of 1380 = 207

    // Test Packaging & Delivery calculation
    assertEquals(40.0, repository.getPackagingFee(), 0.01)
    assertEquals(49.0, repository.getDeliveryFee(), 0.01)

    val gst = repository.getGstTax()
    assertEquals((1380.0 - 207.0) * 0.05, gst, 0.01)

    val total = repository.getCartGrandTotal()
    assertEquals((1380.0 - 207.0 + 40.0 + 49.0 + gst), total, 0.01)
  }

  @Test
  fun testOrderPlacementAndInventoryDeduction() {
    val initialBasmati = repository.ingredients.value.first { it.id == "ing_basmati" }.currentStock
    val biryani = repository.products.value.first { it.id == "prod_biryani" }

    repository.addToCart(biryani, quantity = 2)
    val initialOrdersCount = repository.orders.value.size

    val order = repository.placeOrder(
      customerName = "Test Patron",
      customerPhone = "+91 99999 88888",
      deliveryAddress = "Indiranagar Hub 1",
      paymentMethod = PaymentMethod.UPI_GPAY
    )

    assertEquals(initialOrdersCount + 1, repository.orders.value.size)
    assertEquals(OrderStatus.PLACED, order.status)
    assertEquals(0, repository.cartItems.value.size)

    // Verify theoretical inventory deduction (0.25kg * 2 = 0.5kg basmati deducted)
    val updatedBasmati = repository.ingredients.value.first { it.id == "ing_basmati" }.currentStock
    assertEquals(initialBasmati - 0.50, updatedBasmati, 0.01)
  }

  @Test
  fun testKdsOrderStatusProgression() {
    val orders = repository.orders.value
    val firstOrder = orders.first()

    repository.updateOrderStatus(firstOrder.id, OrderStatus.ACCEPTED)
    var updated = repository.orders.value.first { it.id == firstOrder.id }
    assertEquals(OrderStatus.ACCEPTED, updated.status)

    repository.updateOrderStatus(firstOrder.id, OrderStatus.PREPARING)
    updated = repository.orders.value.first { it.id == firstOrder.id }
    assertEquals(OrderStatus.PREPARING, updated.status)

    repository.updateOrderStatus(firstOrder.id, OrderStatus.READY)
    updated = repository.orders.value.first { it.id == firstOrder.id }
    assertEquals(OrderStatus.READY, updated.status)
  }

  @Test
  fun testRecipeCostingUnitEconomics() {
    val costing = repository.getRecipeCosting()
    assertTrue(costing.isNotEmpty())

    costing.forEach { item ->
      assertTrue("Selling price must exceed zero", item.sellingPrice > 0)
      assertTrue("Food cost must be positive", item.foodCost > 0)
      assertTrue("Total unit cost should be food + packaging", item.totalUnitCost == item.foodCost + item.packagingCost)
      assertTrue("Gross margin should be between 40% and 90%", item.grossMarginPercent in 40.0..90.0)
    }
  }

  @Test
  fun testAiAssistantQuerySynthesis() {
    val marginInsight = repository.generateAiResponse("What are our current recipe margins?")
    assertTrue(marginInsight.contains("Margin"))
    assertTrue(marginInsight.contains("Food Cost"))

    val stockInsight = repository.generateAiResponse("Check ingredient inventory stock levels")
    assertTrue(stockInsight.contains("Inventory"))

    val forecastInsight = repository.generateAiResponse("Forecast weekend demand in Bengaluru")
    assertTrue(forecastInsight.contains("Forecast"))
    assertTrue(forecastInsight.contains("Bengaluru"))
  }

  @Test
  fun testRoomRecipeEntityCostingCalculations() {
    val recipe = com.example.data.local.Recipe(
      name = "Smoked Duck Brioche",
      ingredients = listOf("Duck Breast", "Brioche Bun", "Fig Chutney", "Microgreens"),
      cost = 180.0,
      price = 560.0
    )

    assertEquals("Smoked Duck Brioche", recipe.name)
    assertEquals(4, recipe.ingredients.size)
    assertEquals(180.0, recipe.cost, 0.01)
    assertEquals(560.0, recipe.price, 0.01)

    // Gross profit = 560 - 180 = 380
    assertEquals(380.0, recipe.grossProfit, 0.01)

    // Gross margin % = (380 / 560) * 100 = 67.857%
    assertEquals(67.857, recipe.grossMarginPercent, 0.01)
  }

  @Test
  fun testRoomRecipeConverters() {
    val converters = com.example.data.local.RecipeConverters()
    val ingredients = listOf("Basmati Rice", "Saffron", "A2 Ghee")

    val stringRepresentation = converters.fromIngredientsList(ingredients)
    val convertedBack = converters.toIngredientsList(stringRepresentation)

    assertEquals(ingredients, convertedBack)

    val emptyBack = converters.toIngredientsList("")
    assertTrue(emptyBack.isEmpty())
  }
}
