package com.example.data.local

import androidx.room.TypeConverter

class RecipeConverters {
  @TypeConverter
  fun fromIngredientsList(ingredients: List<String>?): String {
    if (ingredients.isNullOrEmpty()) return ""
    return ingredients.joinToString("|||")
  }

  @TypeConverter
  fun toIngredientsList(data: String?): List<String> {
    if (data.isNullOrBlank()) return emptyList()
    return data.split("|||")
  }
}
