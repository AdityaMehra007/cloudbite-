package com.example.data.model

enum class DietaryType {
  VEGETARIAN,
  NON_VEGETARIAN,
  VEGAN
}

data class Brand(
  val id: String,
  val name: String,
  val tagline: String,
  val description: String,
  val cuisine: String,
  val accentColorHex: Long,
  val rating: Float,
  val reviewCount: Int,
  val badge: String
)

data class Category(
  val id: String,
  val name: String,
  val iconEmoji: String,
  val description: String
)

data class ModifierOption(
  val id: String,
  val name: String,
  val priceDelta: Double
)

data class ModifierGroup(
  val id: String,
  val name: String,
  val required: Boolean,
  val options: List<ModifierOption>
)

data class Product(
  val id: String,
  val name: String,
  val slug: String,
  val description: String,
  val brandId: String,
  val brandName: String,
  val categoryId: String,
  val categoryName: String,
  val price: Double,
  val compareAtPrice: Double? = null,
  val dietaryType: DietaryType,
  val portion: String,
  val prepTimeMinutes: Int,
  val isFeatured: Boolean = false,
  val chefSignature: Boolean = false,
  val inStock: Boolean = true,
  val calories: Int,
  val proteinGrams: Int,
  val ingredientsList: List<String>,
  val allergens: String,
  val tastingNotes: String,
  val modifierGroups: List<ModifierGroup> = emptyList(),
  val recipeCost: Double = 0.0,
  val imageResPlaceholder: String = ""
)

data class CartItem(
  val id: String,
  val product: Product,
  val quantity: Int,
  val selectedModifiers: Map<String, ModifierOption> = emptyMap(),
  val specialInstructions: String = ""
) {
  val unitPrice: Double
    get() = product.price + selectedModifiers.values.sumOf { it.priceDelta }

  val totalPrice: Double
    get() = unitPrice * quantity
}

enum class OrderStatus(val displayName: String, val stepIndex: Int) {
  PLACED("Order Placed", 0),
  ACCEPTED("Kitchen Accepted", 1),
  PREPARING("Chef Preparing", 2),
  READY("Ready for Dispatch", 3),
  OUT_FOR_DELIVERY("Out for Delivery", 4),
  DELIVERED("Delivered", 5),
  CANCELLED("Cancelled", -1)
}

enum class PaymentMethod(val title: String, val subtitle: String) {
  UPI_GPAY("Google Pay UPI", "Instant & secure via UPI"),
  UPI_PHONEPE("PhonePe UPI", "Fast checkout"),
  BLACK_LABEL_CARD("Black Label Card", "Visa / Mastercard / Amex"),
  CASH_ON_DELIVERY("Pay on Delivery", "Cash or UPI on arrival")
}

data class Order(
  val id: String,
  val customerName: String,
  val customerPhone: String,
  val deliveryAddress: String,
  val deliveryArea: String,
  val items: List<CartItem>,
  val subtotal: Double,
  val discount: Double,
  val couponCode: String?,
  val gstTax: Double,
  val packagingFee: Double,
  val deliveryFee: Double,
  val totalAmount: Double,
  val status: OrderStatus,
  val placedTimeMillis: Long,
  val estimatedDeliveryMinutes: Int,
  val courierName: String,
  val paymentMethod: PaymentMethod,
  val isPaid: Boolean,
  val kitchenNotes: String = ""
)

data class Ingredient(
  val id: String,
  val name: String,
  val unit: String,
  val currentStock: Double,
  val minThreshold: Double,
  val costPerUnit: Double,
  val supplier: String,
  val category: String
) {
  val isLowStock: Boolean
    get() = currentStock <= minThreshold
}

data class RecipeIngredient(
  val ingredientId: String,
  val ingredientName: String,
  val quantity: Double,
  val unit: String,
  val cost: Double
)

data class RecipeItem(
  val productId: String,
  val productName: String,
  val brandName: String,
  val ingredients: List<RecipeIngredient>,
  val foodCost: Double,
  val packagingCost: Double,
  val sellingPrice: Double
) {
  val totalUnitCost: Double
    get() = foodCost + packagingCost

  val grossProfit: Double
    get() = sellingPrice - totalUnitCost

  val grossMarginPercent: Double
    get() = if (sellingPrice > 0) (grossProfit / sellingPrice) * 100 else 0.0
}

data class AiBusinessInsight(
  val id: String,
  val title: String,
  val category: String,
  val summary: String,
  val metricImpact: String,
  val confidenceScore: Float,
  val suggestedAction: String
)

data class BengaluruLocation(
  val id: String,
  val name: String,
  val zone: String,
  val isHub: Boolean = false,
  val avgDeliveryMins: Int
)
