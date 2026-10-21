package com.example.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.Recipe
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Recipe data and financial costing operations.
 * Uses RecipeRepository to expose a StateFlow of recipes for the UI to observe,
 * and provides functions for adding or removing recipes.
 */
class RecipeViewModel(
  private val recipeRepository: RecipeRepository
) : ViewModel() {

  /**
   * Secondary constructor enabling default Android ViewModelProvider instantiation.
   */
  constructor(application: Application) : this(
    RecipeRepository(AppDatabase.getDatabase(application).recipeDao())
  )

  /**
   * StateFlow of all recipes observed reactively by the UI layer.
   */
  val recipes: StateFlow<List<Recipe>> = recipeRepository.allRecipes
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  init {
    viewModelScope.launch {
      recipeRepository.seedInitialRecipesIfEmpty()
    }
  }

  /**
   * Adds a new Recipe object into the database.
   */
  fun addRecipe(recipe: Recipe) {
    viewModelScope.launch {
      recipeRepository.insert(recipe)
    }
  }

  /**
   * Helper function to add a recipe with specific fields.
   */
  fun addRecipe(
    name: String,
    ingredients: List<String>,
    cost: Double,
    price: Double,
    imageUri: String? = null
  ) {
    addRecipe(
      Recipe(
        name = name,
        ingredients = ingredients,
        cost = cost,
        price = price,
        imageUri = imageUri
      )
    )
  }

  /**
   * Updates an existing Recipe in the database.
   */
  fun updateRecipe(recipe: Recipe) {
    viewModelScope.launch {
      recipeRepository.update(recipe)
    }
  }

  /**
   * Removes an existing Recipe from the database.
   */
  fun removeRecipe(recipe: Recipe) {
    viewModelScope.launch {
      recipeRepository.delete(recipe)
    }
  }

  /**
   * Removes a recipe by its primary key ID.
   */
  fun removeRecipeById(id: Long) {
    viewModelScope.launch {
      recipeRepository.deleteById(id)
    }
  }

  companion object {
    fun provideFactory(
      recipeRepository: RecipeRepository
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(recipeRepository) as T
      }
    }
  }
}
