package com.example.util

/**
 * Supported measurement units for culinary recipe ingredients.
 */
enum class MeasurementUnit(
  val symbol: String,
  val displayName: String,
  val type: UnitType
) {
  GRAMS("g", "Grams (g)", UnitType.WEIGHT),
  MILLILITERS("ml", "Milliliters (ml)", UnitType.VOLUME),
  OUNCES("oz", "Ounces (oz)", UnitType.WEIGHT),
  FLUID_OUNCES("fl oz", "Fluid Ounces (fl oz)", UnitType.VOLUME),
  KILOGRAMS("kg", "Kilograms (kg)", UnitType.WEIGHT),
  LITERS("L", "Liters (L)", UnitType.VOLUME),
  POUNDS("lb", "Pounds (lb)", UnitType.WEIGHT),
  TABLESPOONS("tbsp", "Tablespoons (tbsp)", UnitType.VOLUME),
  TEASPOONS("tsp", "Teaspoons (tsp)", UnitType.VOLUME)
}

enum class UnitType {
  WEIGHT,
  VOLUME
}

/**
 * Culinary unit converter providing precise bidirectional conversion
 * between grams, milliliters, ounces, and other common culinary units.
 */
object UnitConverter {

  /**
   * Helper function to convert an ingredient measurement between units
   * like grams, milliliters, and ounces.
   *
   * @param value The numerical quantity to convert
   * @param fromUnit The source measurement unit
   * @param toUnit The target measurement unit
   * @param densityGramsPerMl Culinary density ratio for weight<->volume conversions
   *                           (default 1.0 for water, broths, and standard liquids)
   * @return The converted measurement value
   */
  fun convertMeasurement(
    value: Double,
    fromUnit: MeasurementUnit,
    toUnit: MeasurementUnit,
    densityGramsPerMl: Double = 1.0
  ): Double {
    if (value <= 0.0) return 0.0
    if (fromUnit == toUnit) return value

    // 1. Normalize fromUnit into base grams (weight) using standard culinary factors
    val baseGrams: Double = when (fromUnit) {
      MeasurementUnit.GRAMS -> value
      MeasurementUnit.KILOGRAMS -> value * 1000.0
      MeasurementUnit.OUNCES -> value * 28.349523125
      MeasurementUnit.POUNDS -> value * 453.59237
      MeasurementUnit.MILLILITERS -> value * densityGramsPerMl
      MeasurementUnit.LITERS -> value * 1000.0 * densityGramsPerMl
      MeasurementUnit.FLUID_OUNCES -> value * 29.5735295625 * densityGramsPerMl
      MeasurementUnit.TABLESPOONS -> value * 14.78676478 * densityGramsPerMl
      MeasurementUnit.TEASPOONS -> value * 4.92892159 * densityGramsPerMl
    }

    // 2. Convert base grams into the requested target toUnit
    return when (toUnit) {
      MeasurementUnit.GRAMS -> baseGrams
      MeasurementUnit.KILOGRAMS -> baseGrams / 1000.0
      MeasurementUnit.OUNCES -> baseGrams / 28.349523125
      MeasurementUnit.POUNDS -> baseGrams / 453.59237
      MeasurementUnit.MILLILITERS -> baseGrams / densityGramsPerMl
      MeasurementUnit.LITERS -> (baseGrams / densityGramsPerMl) / 1000.0
      MeasurementUnit.FLUID_OUNCES -> (baseGrams / densityGramsPerMl) / 29.5735295625
      MeasurementUnit.TABLESPOONS -> (baseGrams / densityGramsPerMl) / 14.78676478
      MeasurementUnit.TEASPOONS -> (baseGrams / densityGramsPerMl) / 4.92892159
    }
  }

  /**
   * Formats a converted measurement value into a clean, human-readable string.
   */
  fun formatMeasurement(value: Double, unit: MeasurementUnit): String {
    return if (value >= 100.0 || value % 1.0 == 0.0) {
      "${String.format("%.1f", value)} ${unit.symbol}"
    } else {
      "${String.format("%.2f", value)} ${unit.symbol}"
    }
  }
}
