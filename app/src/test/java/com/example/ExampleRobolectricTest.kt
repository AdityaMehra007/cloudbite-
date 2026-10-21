package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CloudBite", appName)
  }

  @Test
  fun `test AppDatabase with Recipe entity and RecipeConverters`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val dao = db.recipeDao()

    val testRecipe = com.example.data.local.Recipe(
      name = "Heirloom Tomato Tart",
      ingredients = listOf("Puff Pastry", "San Marzano Tomatoes", "Ricotta", "Fresh Basil"),
      cost = 95.0,
      price = 340.0
    )

    val id = dao.insertRecipe(testRecipe)
    org.junit.Assert.assertTrue(id > 0)

    val fetched = dao.getRecipeById(id)
    org.junit.Assert.assertNotNull(fetched)
    assertEquals("Heirloom Tomato Tart", fetched?.name)
    assertEquals(4, fetched?.ingredients?.size)
    assertEquals("Puff Pastry", fetched?.ingredients?.get(0))
    assertEquals("Fresh Basil", fetched?.ingredients?.get(3))
    assertEquals(95.0, fetched?.cost ?: 0.0, 0.01)
    assertEquals(340.0, fetched?.price ?: 0.0, 0.01)

    // Test derived margin
    assertEquals(245.0, fetched?.grossProfit ?: 0.0, 0.01)

    db.close()
  }

  @Test
  fun `test RecipeRepository abstraction`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = com.example.data.repository.RecipeRepository(db.recipeDao())

    // Test seed initial recipes
    repository.seedInitialRecipesIfEmpty()
    val initialCount = repository.getCount()
    org.junit.Assert.assertTrue(initialCount >= 8)

    // Test insert via repository
    val customRecipe = com.example.data.local.Recipe(
      name = "Saffron Poached Pear",
      ingredients = listOf("Bosc Pear", "Kashmiri Saffron", "Vanilla Bean", "Star Anise"),
      cost = 60.0,
      price = 220.0
    )
    val id = repository.insert(customRecipe)
    org.junit.Assert.assertTrue(id > 0)

    val fetched = repository.getRecipeById(id)
    org.junit.Assert.assertNotNull(fetched)
    assertEquals("Saffron Poached Pear", fetched?.name)

    // Test delete via repository
    if (fetched != null) {
      repository.delete(fetched)
    }
    assertEquals(initialCount, repository.getCount())

    db.close()
  }

  @Test
  fun `test RecipeViewModel state and mutations`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = com.example.data.repository.RecipeRepository(db.recipeDao())
    val viewModel = com.example.ui.RecipeViewModel(repository)

    kotlinx.coroutines.delay(100)
    val initialList = viewModel.recipes.value
    org.junit.Assert.assertTrue(initialList.isNotEmpty())

    // Add recipe via ViewModel
    viewModel.addRecipe("Cardamom Panna Cotta", listOf("Cream", "Cardamom Pods", "Gelatin", "Pistachio"), 50.0, 180.0)
    kotlinx.coroutines.delay(100)

    val updatedList = viewModel.recipes.value
    val added = updatedList.firstOrNull { it.name == "Cardamom Panna Cotta" }
    org.junit.Assert.assertNotNull(added)
    assertEquals(50.0, added?.cost ?: 0.0, 0.01)
    assertEquals(180.0, added?.price ?: 0.0, 0.01)

    // Remove recipe via ViewModel
    if (added != null) {
      viewModel.removeRecipe(added)
      kotlinx.coroutines.delay(100)
      val afterRemoval = viewModel.recipes.value
      org.junit.Assert.assertNull(afterRemoval.firstOrNull { it.name == "Cardamom Panna Cotta" })
    }

    db.close()
  }

  @Test
  fun `test potential profit margin calculation and threshold`() {
    val lowMarginRecipe = com.example.data.local.Recipe(
      name = "Filter Coffee Biscuit",
      ingredients = listOf("Coffee Extract", "Flour", "Butter"),
      cost = 45.0,
      price = 120.0
    )
    val highMarginRecipe = com.example.data.local.Recipe(
      name = "Black Truffle Risotto",
      ingredients = listOf("Arborio Rice", "Truffle Butter", "Parmigiano"),
      cost = 180.0,
      price = 680.0
    )

    val lowProfitMargin = lowMarginRecipe.price - lowMarginRecipe.cost
    assertEquals(75.0, lowProfitMargin, 0.01)

    val highProfitMargin = highMarginRecipe.price - highMarginRecipe.cost
    assertEquals(500.0, highProfitMargin, 0.01)

    val threshold = 200.0
    org.junit.Assert.assertTrue(lowProfitMargin < threshold)
    org.junit.Assert.assertFalse(highProfitMargin < threshold)
  }

  @Test
  fun `test recipe sorting by name cost and profit margin`() {
    val r1 = com.example.data.local.Recipe(name = "Zesty Lemon Cake", ingredients = listOf("Lemon"), cost = 50.0, price = 200.0)
    val r2 = com.example.data.local.Recipe(name = "Artisan Sourdough", ingredients = listOf("Flour"), cost = 30.0, price = 150.0)
    val r3 = com.example.data.local.Recipe(name = "Porcini Tagliatelle", ingredients = listOf("Porcini"), cost = 120.0, price = 480.0)

    val list = listOf(r1, r2, r3)

    // Sort by name
    val byName = list.sortedBy { it.name.lowercase() }
    assertEquals("Artisan Sourdough", byName[0].name)
    assertEquals("Porcini Tagliatelle", byName[1].name)
    assertEquals("Zesty Lemon Cake", byName[2].name)

    // Sort by cost (ascending)
    val byCostAsc = list.sortedBy { it.cost }
    assertEquals("Artisan Sourdough", byCostAsc[0].name)
    assertEquals("Zesty Lemon Cake", byCostAsc[1].name)
    assertEquals("Porcini Tagliatelle", byCostAsc[2].name)

    // Sort by profit margin (price - cost, descending)
    val byMarginDesc = list.sortedByDescending { it.price - it.cost }
    assertEquals("Porcini Tagliatelle", byMarginDesc[0].name)
    assertEquals("Zesty Lemon Cake", byMarginDesc[1].name)
    assertEquals("Artisan Sourdough", byMarginDesc[2].name)
  }

  @Test
  fun `test recipe update modifications and financial metrics`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = com.example.data.repository.RecipeRepository(db.recipeDao())

    val initialRecipe = com.example.data.local.Recipe(
      name = "Wild Berry Pavlova",
      ingredients = listOf("Meringue", "Heavy Cream", "Blackberries"),
      cost = 70.0,
      price = 220.0
    )
    val id = repository.insert(initialRecipe)

    val fetched = repository.getRecipeById(id)
    org.junit.Assert.assertNotNull(fetched)

    // User modifies name, ingredients list, and financial pricing metrics
    val modifiedRecipe = fetched!!.copy(
      name = "Wild Berry & Pistachio Pavlova",
      ingredients = listOf("Meringue", "Vanilla Mascarpone", "Blackberries", "Sicilian Pistachio"),
      cost = 85.0,
      price = 280.0
    )
    repository.update(modifiedRecipe)

    val updatedFromDb = repository.getRecipeById(id)
    org.junit.Assert.assertNotNull(updatedFromDb)
    assertEquals("Wild Berry & Pistachio Pavlova", updatedFromDb?.name)
    assertEquals(4, updatedFromDb?.ingredients?.size)
    assertEquals("Sicilian Pistachio", updatedFromDb?.ingredients?.get(3))
    assertEquals(85.0, updatedFromDb?.cost ?: 0.0, 0.01)
    assertEquals(280.0, updatedFromDb?.price ?: 0.0, 0.01)

    // Verify recalculated potential profit margin: 280 - 85 = 195.0
    val potentialProfitMargin = updatedFromDb!!.price - updatedFromDb.cost
    assertEquals(195.0, potentialProfitMargin, 0.01)

    // Verify gross margin percentage: (195 / 280) * 100 = 69.64%
    assertEquals(69.642, updatedFromDb.grossMarginPercent, 0.01)

    db.close()
  }

  @Test
  fun `test ingredient unit measurement conversions`() {
    // 1. Grams to Ounces
    val oz = com.example.util.UnitConverter.convertMeasurement(
      value = 100.0,
      fromUnit = com.example.util.MeasurementUnit.GRAMS,
      toUnit = com.example.util.MeasurementUnit.OUNCES
    )
    assertEquals(3.52739, oz, 0.001)

    // 2. Ounces to Grams
    val grams = com.example.util.UnitConverter.convertMeasurement(
      value = 1.0,
      fromUnit = com.example.util.MeasurementUnit.OUNCES,
      toUnit = com.example.util.MeasurementUnit.GRAMS
    )
    assertEquals(28.3495, grams, 0.001)

    // 3. Milliliters to Fluid Ounces
    val flOz = com.example.util.UnitConverter.convertMeasurement(
      value = 500.0,
      fromUnit = com.example.util.MeasurementUnit.MILLILITERS,
      toUnit = com.example.util.MeasurementUnit.FLUID_OUNCES
    )
    assertEquals(16.907, flOz, 0.01)

    // 4. Same unit conversion
    val same = com.example.util.UnitConverter.convertMeasurement(
      value = 250.0,
      fromUnit = com.example.util.MeasurementUnit.GRAMS,
      toUnit = com.example.util.MeasurementUnit.GRAMS
    )
    assertEquals(250.0, same, 0.001)
  }
}




