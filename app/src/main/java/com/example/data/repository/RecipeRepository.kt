package com.example.data.repository

import com.example.data.local.Recipe
import com.example.data.local.RecipeDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository class abstracting access to RecipeDao.
 * Provides a clean API for the ViewModel to interact with the Room database,
 * including methods for inserting, deleting, and retrieving all recipes.
 */
class RecipeRepository(
  private val recipeDao: RecipeDao
) {

  /**
   * Flow of all persisted recipes ordered by name.
   */
  val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()

  /**
   * Retrieve a single recipe by its primary key ID.
   */
  suspend fun getRecipeById(id: Long): Recipe? {
    return recipeDao.getRecipeById(id)
  }

  /**
   * Insert a single recipe into the database.
   */
  suspend fun insert(recipe: Recipe): Long {
    return recipeDao.insertRecipe(recipe)
  }

  /**
   * Insert a batch of recipes into the database.
   */
  suspend fun insertAll(recipes: List<Recipe>) {
    recipeDao.insertRecipes(recipes)
  }

  /**
   * Update an existing recipe in the database.
   */
  suspend fun update(recipe: Recipe) {
    recipeDao.updateRecipe(recipe)
  }

  /**
   * Delete a recipe from the database.
   */
  suspend fun delete(recipe: Recipe) {
    recipeDao.deleteRecipe(recipe)
  }

  /**
   * Delete a recipe by its ID.
   */
  suspend fun deleteById(id: Long) {
    recipeDao.deleteRecipeById(id)
  }

  /**
   * Returns total count of recipes in the database.
   */
  suspend fun getCount(): Int {
    return recipeDao.getCount()
  }

  /**
   * Seeds initial signature recipes if database is empty.
   */
  suspend fun seedInitialRecipesIfEmpty() {
    if (recipeDao.getCount() == 0) {
      val initialRecipes = listOf(
        Recipe(
          name = "Royal Dum Awadhi Biryani",
          ingredients = listOf("Aged Basmati", "Chicken", "Kashmiri Saffron", "Coorg Ghee", "Brown Shallots"),
          cost = 168.0,
          price = 580.0
        ),
        Recipe(
          name = "Wild Foraged Truffle Tagliatelle",
          ingredients = listOf("Bronze Tagliatelle", "Wild Morels", "Black Porcini", "Normandy Butter", "White Truffle Oil", "24M Parmigiano"),
          cost = 210.0,
          price = 690.0
        ),
        Recipe(
          name = "The Black Label Wagyu-Style Burger",
          ingredients = listOf("Prime Patties", "Brioche", "English Cheddar", "Onion Jam", "Black Garlic Aioli"),
          cost = 175.0,
          price = 540.0
        ),
        Recipe(
          name = "Nourish Avocado & Quinoa Bowl",
          ingredients = listOf("Hass Avocado", "Tricolor Quinoa", "Edamame", "Roasted Squash", "Yuzu Tahini"),
          cost = 142.0,
          price = 490.0
        ),
        Recipe(
          name = "Artisan Paneer Tikka Grain Bowl",
          ingredients = listOf("Malai Paneer", "Brown Basmati", "Spinach", "Pickled Shallots", "Makhani Glaze"),
          cost = 135.0,
          price = 460.0
        ),
        Recipe(
          name = "Coorg Ghee Roast Masala Dosa",
          ingredients = listOf("Heirloom Rice Batter", "Coorg Ghee", "Spiced Potato", "Three Chutneys"),
          cost = 72.0,
          price = 280.0
        ),
        Recipe(
          name = "Valrhona Guanaja 70% Dark Mousse",
          ingredients = listOf("Valrhona 70% Guanaja", "Heavy Cream", "Cacao Nibs", "Maldon Salt", "Bourbon Vanilla"),
          cost = 105.0,
          price = 380.0
        ),
        Recipe(
          name = "Estate Single-Origin Nitro Cold Brew",
          ingredients = listOf("Single Origin Arabica", "Filtered Water", "Pure Nitrogen"),
          cost = 52.0,
          price = 260.0
        )
      )
      recipeDao.insertRecipes(initialRecipes)
    }
  }
}
