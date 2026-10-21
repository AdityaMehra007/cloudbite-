package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getAllRecipes(): Flow<List<Recipe>>

  @Query("SELECT * FROM recipes WHERE id = :id")
  suspend fun getRecipeById(id: Long): Recipe?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecipe(recipe: Recipe): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecipes(recipes: List<Recipe>)

  @Update
  suspend fun updateRecipe(recipe: Recipe)

  @Delete
  suspend fun deleteRecipe(recipe: Recipe)

  @Query("DELETE FROM recipes WHERE id = :id")
  suspend fun deleteRecipeById(id: Long)

  @Query("SELECT COUNT(*) FROM recipes")
  suspend fun getCount(): Int
}
