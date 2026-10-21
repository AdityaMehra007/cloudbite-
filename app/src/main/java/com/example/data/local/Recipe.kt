package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database @Entity class named 'Recipe' with fields for:
 * - id (primary key)
 * - name (String)
 * - ingredients (List<String>)
 * - cost (Double)
 * - price (Double)
 */
@Entity(tableName = "recipes")
data class Recipe(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val ingredients: List<String>,
  val cost: Double,
  val price: Double,
  val imageUri: String? = null
) {
  val grossProfit: Double
    get() = price - cost

  val grossMarginPercent: Double
    get() = if (price > 0) ((price - cost) / price) * 100 else 0.0
}

// Backwards compatibility alias
typealias RecipeEntity = Recipe
