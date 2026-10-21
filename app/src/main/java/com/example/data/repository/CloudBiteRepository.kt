package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CloudBiteRepository {

  // Seed Locations
  val locations = listOf(
    BengaluruLocation("loc_ind", "Indiranagar, 100ft Rd", "East Hub", isHub = true, avgDeliveryMins = 28),
    BengaluruLocation("loc_kor", "Koramangala 4th Block", "Central Hub", isHub = true, avgDeliveryMins = 32),
    BengaluruLocation("loc_lav", "Lavelle Road & UB City", "CBD Hub", avgDeliveryMins = 25),
    BengaluruLocation("loc_hsr", "HSR Layout Sector 3", "South Hub", avgDeliveryMins = 30),
    BengaluruLocation("loc_jay", "Jayanagar 4th T Block", "South-West Hub", avgDeliveryMins = 35)
  )

  private val _selectedLocation = MutableStateFlow(locations[0])
  val selectedLocation: StateFlow<BengaluruLocation> = _selectedLocation.asStateFlow()

  // Seed Brands
  val brands = listOf(
    Brand(
      id = "brand_bowl",
      name = "Bowl Theory",
      tagline = "Contemporary Artisanal Bowls",
      description = "Nutrient-dense grain bowls, vibrant heritage greens, and house-emulsified dressings designed by holistic chefs.",
      cuisine = "Artisan Bowls & Clean Living",
      accentColorHex = 0xFF78816B,
      rating = 4.9f,
      reviewCount = 1420,
      badge = "Clean & Crafted"
    ),
    Brand(
      id = "brand_tiffin",
      name = "Bengaluru Tiffin Co.",
      tagline = "Heritage Flavours Reimagined",
      description = "Slow-fermented heirloom batts, golden ghee-roasted dosas, and heritage spices from Coorg & Malabar estates.",
      cuisine = "Modern South Indian",
      accentColorHex = 0xFFA88B5C,
      rating = 4.95f,
      reviewCount = 2860,
      badge = "Heritage Icon"
    ),
    Brand(
      id = "brand_firegrill",
      name = "Firegrill Kitchen",
      tagline = "Smoked Craft & Bold Flavour",
      description = "Handcrafted smash patties, smoked brioche, wood-fired skewers, and truffle-dusted crisp sides.",
      cuisine = "Gourmet Burgers & Grills",
      accentColorHex = 0xFFB95D45,
      rating = 4.88f,
      reviewCount = 1890,
      badge = "Wood-Fired"
    ),
    Brand(
      id = "brand_pasta",
      name = "Maison Pasta Co.",
      tagline = "Italian Craft, Zero Shortcuts",
      description = "Extruded daily bronze-die pasta, 24-month aged Parmigiano Reggiano, and rich slow-simmered sauces.",
      cuisine = "Artisanal Italian",
      accentColorHex = 0xFFC5AD82,
      rating = 4.92f,
      reviewCount = 980,
      badge = "Fresh Daily"
    ),
    Brand(
      id = "brand_noir",
      name = "Atelier Noir Pâtisserie",
      tagline = "Dark Cacao & Single Estate Brews",
      description = "Single-origin Valrhona chocolate entremets, delicate French viennoiserie, and cold-drip Chikmagalur coffee.",
      cuisine = "Fine Desserts & Coffee",
      accentColorHex = 0xFF251D19,
      rating = 4.97f,
      reviewCount = 1120,
      badge = "Chef's Atelier"
    )
  )

  // Seed Categories
  val categories = listOf(
    Category("all", "All Curations", "✨", "Complete luxury collection"),
    Category("cat_heritage", "Indian Heritage", "🍛", "Slow-cooked biryanis & curries"),
    Category("cat_bowls", "Artisan Bowls", "🥗", "Superfood grains & fresh greens"),
    Category("cat_grill", "Fire & Grill", "🍔", "Smoked craft burgers & skewers"),
    Category("cat_pasta", "Crafted Pasta", "🍝", "Handmade pasta & truffles"),
    Category("cat_tiffin", "South Indian", "🥞", "Crisp ghee dosas & tiffin"),
    Category("cat_dessert", "Sweet Indulgence", "🍫", "Fine chocolate & pastry"),
    Category("cat_coffee", "Coffee & Tonics", "☕", "Cold brews & botanical teas")
  )

  // Seed Products
  private val _products = MutableStateFlow(
    listOf(
      Product(
        id = "prod_biryani",
        name = "Royal Dum Awadhi Biryani",
        slug = "royal-dum-awadhi-biryani",
        description = "Fragrant long-grain aged basmati rice, tender farm chicken marinated for 18 hours in saffron yoghurt, caramelized shallots, and fragrant mace.",
        brandId = "brand_tiffin",
        brandName = "Bengaluru Tiffin Co.",
        categoryId = "cat_heritage",
        categoryName = "Indian Heritage",
        price = 580.0,
        compareAtPrice = 650.0,
        dietaryType = DietaryType.NON_VEGETARIAN,
        portion = "Serves 1-2 • 550g",
        prepTimeMinutes = 25,
        isFeatured = true,
        chefSignature = true,
        inStock = true,
        calories = 680,
        proteinGrams = 42,
        ingredientsList = listOf("Aged Dehradun Basmati", "Tender Chicken", "Kashmiri Saffron", "Coorg Ghee", "Brown Shallots", "Star Anise"),
        allergens = "Contains Dairy (Ghee, Curd). Halal Certified.",
        tastingNotes = "Rich aromatic floral notes of saffron and rose water balanced with slow-roasted spices and meltingly tender meat.",
        recipeCost = 168.0,
        modifierGroups = listOf(
          ModifierGroup(
            id = "mod_spice_biryani",
            name = "Spice Profile",
            required = true,
            options = listOf(
              ModifierOption("sp_mild", "Awadhi Royal Mild", 0.0),
              ModifierOption("sp_med", "Classic Lucknowi Balance", 0.0),
              ModifierOption("sp_bold", "Bengaluru Spice Infusion", 0.0)
            )
          ),
          ModifierGroup(
            id = "mod_raita",
            name = "Accompaniment Upgrade",
            required = false,
            options = listOf(
              ModifierOption("raita_burani", "Burani Garlic Raita", 45.0),
              ModifierOption("raita_pomegranate", "Pomegranate Mint Raita", 55.0),
              ModifierOption("salan_mirchi", "Mirchi Ka Salan (Side)", 65.0)
            )
          )
        )
      ),
      Product(
        id = "prod_mushroom_pasta",
        name = "Wild Foraged Truffle Tagliatelle",
        slug = "wild-foraged-truffle-tagliatelle",
        description = "Hand-cut bronze-extruded egg tagliatelle, wild morels and porcini mushrooms tossed in Normandy butter, white truffle emulsion, and aged Parmigiano.",
        brandId = "brand_pasta",
        brandName = "Maison Pasta Co.",
        categoryId = "cat_pasta",
        categoryName = "Crafted Pasta",
        price = 690.0,
        compareAtPrice = 750.0,
        dietaryType = DietaryType.VEGETARIAN,
        portion = "Serves 1 • 380g",
        prepTimeMinutes = 20,
        isFeatured = true,
        chefSignature = true,
        inStock = true,
        calories = 540,
        proteinGrams = 18,
        ingredientsList = listOf("Fresh Bronze Tagliatelle", "Wild Morels", "Black Porcini", "Normandy Butter", "White Truffle Oil", "24-Month Parmigiano"),
        allergens = "Contains Wheat, Dairy, Eggs.",
        tastingNotes = "Deep earthy umami from slow-seared forest mushrooms wrapped in silky truffle-infused glaze.",
        recipeCost = 210.0,
        modifierGroups = listOf(
          ModifierGroup(
            id = "mod_cheese_taglia",
            name = "Extra Cheese & Truffle",
            required = false,
            options = listOf(
              ModifierOption("ext_parm", "Extra Aged Parmigiano Shavings", 60.0),
              ModifierOption("ext_truffle", "Double Black Truffle Emulsion", 95.0)
            )
          )
        )
      ),
      Product(
        id = "prod_smash_burger",
        name = "The Black Label Wagyu-Style Burger",
        slug = "black-label-smash-burger",
        description = "Double smashed prime patties with crisp lacy edges, melted aged English cheddar, slow onion jam, house black garlic aioli in toasted brioche.",
        brandId = "brand_firegrill",
        brandName = "Firegrill Kitchen",
        categoryId = "cat_grill",
        categoryName = "Fire & Grill",
        price = 540.0,
        compareAtPrice = 590.0,
        dietaryType = DietaryType.NON_VEGETARIAN,
        portion = "Generous Single • 340g",
        prepTimeMinutes = 18,
        isFeatured = true,
        chefSignature = false,
        inStock = true,
        calories = 720,
        proteinGrams = 38,
        ingredientsList = listOf("Prime Blend Patties", "Artisan Brioche", "Mature English Cheddar", "Caramelized Onion Jam", "Black Garlic Aioli"),
        allergens = "Contains Wheat, Dairy, Mustard, Eggs.",
        tastingNotes = "Intense savory crust on the patties cut by sweet caramelized onion sweetness and rich velvety cheddar.",
        recipeCost = 175.0,
        modifierGroups = listOf(
          ModifierGroup(
            id = "mod_patty_side",
            name = "Crisp Side Accompaniment",
            required = false,
            options = listOf(
              ModifierOption("side_peri_fries", "Truffle & Rosemary Sea Salt Fries", 110.0),
              ModifierOption("side_onion_rings", "Crisp Beer-Battered Shallot Rings", 95.0)
            )
          )
        )
      ),
      Product(
        id = "prod_avocado_bowl",
        name = "Nourish Avocado & Quinoa Bowl",
        slug = "nourish-avocado-quinoa-bowl",
        description = "Hass avocado ribbons, organic tricolor quinoa, edamame, roasted kabocha squash, pickled radishes, toasted hemp seeds, and yuzu-tahini dressing.",
        brandId = "brand_bowl",
        brandName = "Bowl Theory",
        categoryId = "cat_bowls",
        categoryName = "Artisan Bowls",
        price = 490.0,
        compareAtPrice = null,
        dietaryType = DietaryType.VEGAN,
        portion = "Serves 1 • 420g",
        prepTimeMinutes = 15,
        isFeatured = false,
        chefSignature = false,
        inStock = true,
        calories = 460,
        proteinGrams = 22,
        ingredientsList = listOf("Fresh Hass Avocado", "Tricolor Quinoa", "Japanese Edamame", "Roasted Squash", "Yuzu Tahini", "Hemp Seeds"),
        allergens = "Contains Sesame, Soy. 100% Gluten-Free.",
        tastingNotes = "Clean, refreshing crunch with silky buttery avocado and citrusy nutty yuzu tahini balance.",
        recipeCost = 142.0,
        modifierGroups = listOf(
          ModifierGroup(
            id = "mod_protein_boost",
            name = "Protein Boost",
            required = false,
            options = listOf(
              ModifierOption("boost_tofu", "Smoked Citrus Tofu (+8g)", 80.0),
              ModifierOption("boost_paneer", "Grilled Herb Malai Paneer (+12g)", 95.0),
              ModifierOption("boost_chicken", "Sous-Vide Herb Chicken Breast (+24g)", 120.0)
            )
          )
        )
      ),
      Product(
        id = "prod_paneer_tikka_bowl",
        name = "Artisan Paneer Tikka Grain Bowl",
        slug = "artisan-paneer-tikka-grain-bowl",
        description = "Clay oven charred cottage cheese, whole grain brown basmati, tempered baby spinach, pickled shallots, crisp lotus stem, and smoked makhani reduction.",
        brandId = "brand_bowl",
        brandName = "Bowl Theory",
        categoryId = "cat_bowls",
        categoryName = "Artisan Bowls",
        price = 460.0,
        compareAtPrice = 510.0,
        dietaryType = DietaryType.VEGETARIAN,
        portion = "Serves 1 • 440g",
        prepTimeMinutes = 18,
        isFeatured = false,
        chefSignature = false,
        inStock = true,
        calories = 520,
        proteinGrams = 26,
        ingredientsList = listOf("Malai Paneer", "Brown Basmati", "Baby Spinach", "Pickled Shallots", "Lotus Stem Chips", "Smoked Makhani Glaze"),
        allergens = "Contains Dairy.",
        tastingNotes = "Tandoor charcoal aroma melding with rich velvety makhani sauce and fresh pickled zing.",
        recipeCost = 135.0
      ),
      Product(
        id = "prod_ghee_dosa",
        name = "Coorg Ghee Roast Masala Dosa",
        slug = "coorg-ghee-roast-masala-dosa",
        description = "Cast-iron roasted heirloom red rice batter, laden with artisanal clarified butter from Coorg, spiced baby potato podi, served with three estate chutneys.",
        brandId = "brand_tiffin",
        brandName = "Bengaluru Tiffin Co.",
        categoryId = "cat_tiffin",
        categoryName = "South Indian",
        price = 280.0,
        compareAtPrice = null,
        dietaryType = DietaryType.VEGETARIAN,
        portion = "Classic Breakfast Serving",
        prepTimeMinutes = 12,
        isFeatured = true,
        chefSignature = true,
        inStock = true,
        calories = 390,
        proteinGrams = 11,
        ingredientsList = listOf("Heirloom Rice Batter", "Grass-fed Coorg Ghee", "Spiced Baby Potato", "Roasted Chana Chutney", "Shallot Tomato Chutney", "Molagapodi"),
        allergens = "Contains Dairy. Gluten-Free.",
        tastingNotes = "Uncompromising golden crunch yielding to buttery richness, paired with pungent gun-powder podi.",
        recipeCost = 72.0
      ),
      Product(
        id = "prod_chocolate_mousse",
        name = "Valrhona Guanaja 70% Dark Mousse",
        slug = "valrhona-dark-chocolate-mousse",
        description = "Silk-textured Grand Cru bittersweet chocolate ganache, cacao nib tuile, sea salt flakes, and light Madagascar bourbon vanilla cremeux.",
        brandId = "brand_noir",
        brandName = "Atelier Noir Pâtisserie",
        categoryId = "cat_dessert",
        categoryName = "Sweet Indulgence",
        price = 380.0,
        compareAtPrice = 420.0,
        dietaryType = DietaryType.VEGETARIAN,
        portion = "Individual Pâtisserie • 160g",
        prepTimeMinutes = 10,
        isFeatured = true,
        chefSignature = true,
        inStock = true,
        calories = 360,
        proteinGrams = 6,
        ingredientsList = listOf("Valrhona 70% Guanaja", "Heavy Dairy Cream", "Cacao Nibs", "Maldon Sea Salt", "Bourbon Vanilla"),
        allergens = "Contains Dairy, Soy Lecithin.",
        tastingNotes = "Intense floral dark cocoa with delicate bitterness, offset by flakes of flaky salt and cloud-like vanilla.",
        recipeCost = 105.0
      ),
      Product(
        id = "prod_cold_coffee",
        name = "Estate Single-Origin Nitro Cold Brew",
        slug = "estate-single-origin-cold-brew",
        description = "Steeped for 24 hours from Chikmagalur Baba Budangiri shade-grown Arabica, infused with micro-nitrogen bubbles for a velvety cascading stout texture.",
        brandId = "brand_noir",
        brandName = "Atelier Noir Pâtisserie",
        categoryId = "cat_coffee",
        categoryName = "Coffee & Tonics",
        price = 260.0,
        compareAtPrice = null,
        dietaryType = DietaryType.VEGAN,
        portion = "330ml Glass Bottle",
        prepTimeMinutes = 5,
        isFeatured = false,
        chefSignature = false,
        inStock = true,
        calories = 15,
        proteinGrams = 1,
        ingredientsList = listOf("Single Origin Arabica Beans", "Filtered Himalayan Water", "Pure Nitrogen"),
        allergens = "Allergen Free.",
        tastingNotes = "Naturally sweet chocolate notes, dried cherry hints, and an incredibly creamy nitrogen head.",
        recipeCost = 52.0
      )
    )
  )
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  // Ingredients for Inventory & Recipe Costing
  private val _ingredients = MutableStateFlow(
    listOf(
      Ingredient("ing_basmati", "Aged Dehradun Basmati Rice", "kg", 85.0, 30.0, 140.0, "Himalayan Harvest Co.", "Grains"),
      Ingredient("ing_chicken", "Free-Range Farm Chicken", "kg", 42.0, 20.0, 280.0, "Kaveri Valley Poultry", "Poultry"),
      Ingredient("ing_ghee", "Artisanal Coorg A2 Ghee", "L", 18.5, 10.0, 950.0, "Coorg Organic Dairy", "Dairy"),
      Ingredient("ing_truffle", "Urbani White Truffle Essence", "ml", 650.0, 500.0, 4.5, "Milano Fine Foods", "Gourmet Oils"),
      Ingredient("ing_pasta", "Bronze-Die Semolina Tagliatelle", "kg", 24.0, 15.0, 320.0, "Maison Pasta Works", "Dry Pasta"),
      Ingredient("ing_parm", "24-Month Parmigiano Reggiano", "kg", 8.2, 5.0, 2400.0, "Parma Consortium", "Cheese"),
      Ingredient("ing_avocado", "Imported Hass Avocado", "pcs", 45.0, 25.0, 85.0, "Global Fresh Exports", "Produce"),
      Ingredient("ing_chocolate", "Valrhona Guanaja 70% Discs", "kg", 12.0, 6.0, 1850.0, "Valrhona India", "Pastry"),
      Ingredient("ing_boxes", "Luxury Embossed Bento Packaging", "pcs", 340.0, 150.0, 30.0, "EcoLuxury Packaging Ltd.", "Packaging")
    )
  )
  val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

  // Seed Orders
  private val _orders = MutableStateFlow<List<Order>>(
    listOf(
      Order(
        id = "CB-84918",
        customerName = "Vikramaditya Roy",
        customerPhone = "+91 98450 12894",
        deliveryAddress = "Penthouse 4B, Prestige Kingfisher Towers, Lavelle Rd",
        deliveryArea = "Lavelle Road & UB City",
        items = listOf(
          CartItem(
            id = "c1",
            product = _products.value[1], // Truffle Tagliatelle
            quantity = 2,
            selectedModifiers = mapOf("mod_cheese_taglia" to ModifierOption("ext_parm", "Extra Parmigiano", 60.0)),
            specialInstructions = "Please keep extra warm and include cutlery."
          ),
          CartItem(
            id = "c2",
            product = _products.value[6], // Dark Chocolate Mousse
            quantity = 2
          )
        ),
        subtotal = 2260.0,
        discount = 250.0,
        couponCode = "BLACKLABEL",
        gstTax = 100.5,
        packagingFee = 60.0,
        deliveryFee = 0.0,
        totalAmount = 2170.5,
        status = OrderStatus.PREPARING,
        placedTimeMillis = System.currentTimeMillis() - 12 * 60 * 1000,
        estimatedDeliveryMinutes = 22,
        courierName = "Suresh Gowda (Black Label Carrier #14)",
        paymentMethod = PaymentMethod.BLACK_LABEL_CARD,
        isPaid = true,
        kitchenNotes = "VIP Guest. Ensure gold seal on packaging."
      ),
      Order(
        id = "CB-84919",
        customerName = "Ananya Sen",
        customerPhone = "+91 99002 44102",
        deliveryAddress = "Villa 18, Windmills of Your Mind, Whitefield",
        deliveryArea = "Indiranagar, 100ft Rd",
        items = listOf(
          CartItem(
            id = "c3",
            product = _products.value[0], // Royal Dum Biryani
            quantity = 1,
            selectedModifiers = mapOf("mod_raita" to ModifierOption("raita_burani", "Burani Garlic Raita", 45.0))
          ),
          CartItem(
            id = "c4",
            product = _products.value[7], // Nitro Cold Brew
            quantity = 2
          )
        ),
        subtotal = 1145.0,
        discount = 150.0,
        couponCode = "FIRSTBITE",
        gstTax = 49.75,
        packagingFee = 60.0,
        deliveryFee = 49.0,
        totalAmount = 1153.75,
        status = OrderStatus.ACCEPTED,
        placedTimeMillis = System.currentTimeMillis() - 5 * 60 * 1000,
        estimatedDeliveryMinutes = 30,
        courierName = "Kiran Babu (Carrier #09)",
        paymentMethod = PaymentMethod.UPI_GPAY,
        isPaid = true
      ),
      Order(
        id = "CB-84920",
        customerName = "Devansh Mehta",
        customerPhone = "+91 97401 55620",
        deliveryAddress = "Flat 902, Embassy Habitat, Palace Rd",
        deliveryArea = "Koramangala 4th Block",
        items = listOf(
          CartItem(
            id = "c5",
            product = _products.value[2], // Wagyu Smash Burger
            quantity = 2,
            selectedModifiers = mapOf("mod_patty_side" to ModifierOption("side_peri_fries", "Truffle Fries", 110.0))
          )
        ),
        subtotal = 1300.0,
        discount = 0.0,
        couponCode = null,
        gstTax = 65.0,
        packagingFee = 60.0,
        deliveryFee = 49.0,
        totalAmount = 1474.0,
        status = OrderStatus.READY,
        placedTimeMillis = System.currentTimeMillis() - 25 * 60 * 1000,
        estimatedDeliveryMinutes = 10,
        courierName = "Arjun K. (Carrier #21)",
        paymentMethod = PaymentMethod.UPI_PHONEPE,
        isPaid = true
      )
    )
  )
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  // Active Cart State
  private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _appliedCoupon = MutableStateFlow<String?>("BLACKLABEL")
  val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

  // Location selector
  fun setLocation(location: BengaluruLocation) {
    _selectedLocation.value = location
  }

  // Cart operations
  fun addToCart(product: Product, quantity: Int = 1, modifiers: Map<String, ModifierOption> = emptyMap(), notes: String = "") {
    val current = _cartItems.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.product.id == product.id && it.selectedModifiers == modifiers
    }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
    } else {
      current.add(
        CartItem(
          id = UUID.randomUUID().toString(),
          product = product,
          quantity = quantity,
          selectedModifiers = modifiers,
          specialInstructions = notes
        )
      )
    }
    _cartItems.value = current
  }

  fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
    if (newQuantity <= 0) {
      removeCartItem(cartItemId)
      return
    }
    _cartItems.value = _cartItems.value.map {
      if (it.id == cartItemId) it.copy(quantity = newQuantity) else it
    }
  }

  fun removeCartItem(cartItemId: String) {
    _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
  }

  fun clearCart() {
    _cartItems.value = emptyList()
  }

  fun applyCoupon(code: String): Boolean {
    val upper = code.trim().uppercase()
    return if (upper in listOf("BLACKLABEL", "FIRSTBITE", "BENGALURU")) {
      _appliedCoupon.value = upper
      true
    } else {
      false
    }
  }

  fun removeCoupon() {
    _appliedCoupon.value = null
  }

  // Financial calculations for Cart
  fun getCartSubtotal(): Double = _cartItems.value.sumOf { it.totalPrice }

  fun getCartDiscount(): Double {
    val subtotal = getCartSubtotal()
    return when (_appliedCoupon.value) {
      "BLACKLABEL" -> subtotal * 0.15 // 15% luxury patron privilege
      "FIRSTBITE" -> 150.0.coerceAtMost(subtotal)
      "BENGALURU" -> 100.0.coerceAtMost(subtotal)
      else -> 0.0
    }
  }

  fun getPackagingFee(): Double = if (_cartItems.value.isEmpty()) 0.0 else 40.0
  fun getDeliveryFee(): Double = if (_cartItems.value.isEmpty()) 0.0 else if (getCartSubtotal() > 1500) 0.0 else 49.0
  fun getGstTax(): Double = (getCartSubtotal() - getCartDiscount()).coerceAtLeast(0.0) * 0.05 // 5% F&B GST
  fun getCartGrandTotal(): Double = (getCartSubtotal() - getCartDiscount() + getPackagingFee() + getDeliveryFee() + getGstTax()).coerceAtLeast(0.0)

  // Place Order
  fun placeOrder(
    customerName: String,
    customerPhone: String,
    deliveryAddress: String,
    paymentMethod: PaymentMethod,
    kitchenNotes: String = ""
  ): Order {
    val newOrder = Order(
      id = "CB-" + (10000..99999).random(),
      customerName = customerName.ifBlank { "Patron Guest" },
      customerPhone = customerPhone.ifBlank { "+91 98450 00000" },
      deliveryAddress = deliveryAddress.ifBlank { "Villa 22, Indiranagar Defense Colony" },
      deliveryArea = _selectedLocation.value.name,
      items = _cartItems.value,
      subtotal = getCartSubtotal(),
      discount = getCartDiscount(),
      couponCode = _appliedCoupon.value,
      gstTax = getGstTax(),
      packagingFee = getPackagingFee(),
      deliveryFee = getDeliveryFee(),
      totalAmount = getCartGrandTotal(),
      status = OrderStatus.PLACED,
      placedTimeMillis = System.currentTimeMillis(),
      estimatedDeliveryMinutes = _selectedLocation.value.avgDeliveryMins,
      courierName = "Assigned Black Label Dispatcher",
      paymentMethod = paymentMethod,
      isPaid = true,
      kitchenNotes = kitchenNotes
    )

    // Deduct theoretical inventory for ordered recipes
    deductInventoryForOrder(newOrder)

    // Prepend to orders list
    _orders.value = listOf(newOrder) + _orders.value
    clearCart()
    return newOrder
  }

  // Inventory & KDS state transitions
  fun updateOrderStatus(orderId: String, nextStatus: OrderStatus) {
    _orders.value = _orders.value.map { order ->
      if (order.id == orderId) {
        order.copy(status = nextStatus)
      } else order
    }
  }

  fun toggleProductStock(productId: String) {
    _products.value = _products.value.map {
      if (it.id == productId) it.copy(inStock = !it.inStock) else it
    }
  }

  fun restockIngredient(ingredientId: String, addedAmount: Double) {
    _ingredients.value = _ingredients.value.map {
      if (it.id == ingredientId) it.copy(currentStock = it.currentStock + addedAmount) else it
    }
  }

  private fun deductInventoryForOrder(order: Order) {
    val currentIngs = _ingredients.value.toMutableList()
    order.items.forEach { cartItem ->
      val qty = cartItem.quantity
      when (cartItem.product.id) {
        "prod_biryani" -> {
          updateIng(currentIngs, "ing_basmati", 0.25 * qty)
          updateIng(currentIngs, "ing_chicken", 0.30 * qty)
          updateIng(currentIngs, "ing_ghee", 0.04 * qty)
        }
        "prod_mushroom_pasta" -> {
          updateIng(currentIngs, "ing_pasta", 0.20 * qty)
          updateIng(currentIngs, "ing_parm", 0.05 * qty)
          updateIng(currentIngs, "ing_truffle", 5.0 * qty)
        }
        "prod_chocolate_mousse" -> {
          updateIng(currentIngs, "ing_chocolate", 0.12 * qty)
        }
        "prod_avocado_bowl" -> {
          updateIng(currentIngs, "ing_avocado", 1.0 * qty)
        }
      }
      updateIng(currentIngs, "ing_boxes", 1.0 * qty)
    }
    _ingredients.value = currentIngs
  }

  private fun updateIng(list: MutableList<Ingredient>, id: String, deducted: Double) {
    val index = list.indexOfFirst { it.id == id }
    if (index >= 0) {
      val item = list[index]
      list[index] = item.copy(currentStock = (item.currentStock - deducted).coerceAtLeast(0.0))
    }
  }

  // Recipes & Costing items
  suspend fun initializeRoomRecipes(dao: com.example.data.local.RecipeDao) {
    if (dao.getCount() == 0) {
      val initialRecipes = listOf(
        com.example.data.local.Recipe(
          name = "Royal Dum Awadhi Biryani",
          ingredients = listOf("Aged Basmati", "Chicken", "Kashmiri Saffron", "Coorg Ghee", "Brown Shallots"),
          cost = 168.0,
          price = 580.0
        ),
        com.example.data.local.Recipe(
          name = "Wild Foraged Truffle Tagliatelle",
          ingredients = listOf("Bronze Tagliatelle", "Wild Morels", "Black Porcini", "Normandy Butter", "White Truffle Oil", "24M Parmigiano"),
          cost = 210.0,
          price = 690.0
        ),
        com.example.data.local.Recipe(
          name = "The Black Label Wagyu-Style Burger",
          ingredients = listOf("Prime Patties", "Brioche", "English Cheddar", "Onion Jam", "Black Garlic Aioli"),
          cost = 175.0,
          price = 540.0
        ),
        com.example.data.local.Recipe(
          name = "Nourish Avocado & Quinoa Bowl",
          ingredients = listOf("Hass Avocado", "Tricolor Quinoa", "Edamame", "Roasted Squash", "Yuzu Tahini"),
          cost = 142.0,
          price = 490.0
        ),
        com.example.data.local.Recipe(
          name = "Artisan Paneer Tikka Grain Bowl",
          ingredients = listOf("Malai Paneer", "Brown Basmati", "Spinach", "Pickled Shallots", "Makhani Glaze"),
          cost = 135.0,
          price = 460.0
        ),
        com.example.data.local.Recipe(
          name = "Coorg Ghee Roast Masala Dosa",
          ingredients = listOf("Heirloom Rice Batter", "Coorg Ghee", "Spiced Potato", "Three Chutneys"),
          cost = 72.0,
          price = 280.0
        ),
        com.example.data.local.Recipe(
          name = "Valrhona Guanaja 70% Dark Mousse",
          ingredients = listOf("Valrhona 70% Guanaja", "Heavy Cream", "Cacao Nibs", "Maldon Salt", "Bourbon Vanilla"),
          cost = 105.0,
          price = 380.0
        ),
        com.example.data.local.Recipe(
          name = "Estate Single-Origin Nitro Cold Brew",
          ingredients = listOf("Single Origin Arabica", "Filtered Water", "Pure Nitrogen"),
          cost = 52.0,
          price = 260.0
        )
      )
      dao.insertRecipes(initialRecipes)
    }
  }

  fun getRecipeCosting(): List<RecipeItem> {
    return _products.value.map { prod ->
      val (foodCost, packagingCost) = when (prod.id) {
        "prod_biryani" -> Pair(138.0, 30.0)
        "prod_mushroom_pasta" -> Pair(180.0, 30.0)
        "prod_smash_burger" -> Pair(145.0, 30.0)
        "prod_avocado_bowl" -> Pair(112.0, 30.0)
        "prod_paneer_tikka_bowl" -> Pair(105.0, 30.0)
        "prod_ghee_dosa" -> Pair(42.0, 30.0)
        "prod_chocolate_mousse" -> Pair(75.0, 30.0)
        "prod_cold_coffee" -> Pair(22.0, 30.0)
        else -> Pair(prod.price * 0.30, 30.0)
      }
      RecipeItem(
        productId = prod.id,
        productName = prod.name,
        brandName = prod.brandName,
        ingredients = prod.ingredientsList.map {
          RecipeIngredient("ing_sample", it, 1.0, "portion", 25.0)
        },
        foodCost = foodCost,
        packagingCost = packagingCost,
        sellingPrice = prod.price
      )
    }
  }

  // AI Assistant Knowledge & Analysis Engine
  fun generateAiResponse(query: String): String {
    val clean = query.lowercase().trim()
    val totalOrders = _orders.value.size
    val gov = _orders.value.sumOf { it.totalAmount }
    val lowStockCount = _ingredients.value.count { it.isLowStock }

    return when {
      "margin" in clean || "profit" in clean || "cost" in clean -> {
        """
        📊 **Black Label Financial & Recipe Margin Audit**:
        • **Average Contribution Margin**: 67.8% across active brands.
        • **Highest Margin Performer**: *Estate Single-Origin Nitro Cold Brew* (Gross Margin: 80.0%, ₹208 contribution per bottle).
        • **Highest Gross Contribution Volume**: *Wild Foraged Truffle Tagliatelle* (₹450 gross margin on ₹690 ticket).
        • **Food Cost Percentage**: Currently running at 28.4% (well within the luxury target benchmark of < 30%).
        • **Recommendation**: Bundling the Valrhona Mousse with the Nitro Cold Brew as a 'Nocturne Tasting Pair' will elevate AOV by ₹180 with a blended 73% margin.
        """.trimIndent()
      }
      "stock" in clean || "inventory" in clean || "ingredient" in clean -> {
        val lowList = _ingredients.value.filter { it.isLowStock }.joinToString("\n") {
          "  - ${it.name}: ${it.currentStock} ${it.unit} (Threshold: ${it.minThreshold} ${it.unit}) from ${it.supplier}"
        }
        """
        ⚠️ **Live Inventory & Procurement Health Check**:
        • Total Monitored Ingredients: ${_ingredients.value.size} active SKUs
        • Critical Low-Stock Alerts ($lowStockCount detected):
        $lowList
        • **Automated Action**: Purchase order drafts have been prepared for Himalayan Harvest Co. and Parma Consortium. Replenishing now guarantees zero service disruption for weekend dinner rushes.
        """.trimIndent()
      }
      "forecast" in clean || "demand" in clean || "rush" in clean || "bengaluru" in clean -> {
        """
        📈 **Bengaluru Culinary Demand Forecast (Weekend Projection)**:
        • **Peak Windows**: Friday 20:00 - 22:30 & Sunday 12:30 - 15:00.
        • **Expected Order Velocity**: 120-145 premium tickets per hub.
        • **Category Lift**: +34% projected demand for *Royal Dum Awadhi Biryani* and *The Black Label Wagyu-Style Burger*.
        • **Operational Recommendation**: Pre-marinate chicken by 11:00 AM Friday and prep 45 portion brioche buns in the Indiranagar and Koramangala central kitchens.
        """.trimIndent()
      }
      "waste" in clean || "wastage" in clean || "variance" in clean -> {
        """
        🌿 **Wastage & Consumption Variance Analysis**:
        • **Theoretical vs Physical Consumption Variance**: -1.8% (Exceptional kitchen precision).
        • **Basmati Rice Efficiency**: 98.6% yield with zero spoilage in hot-holding units.
        • **Perishables Watch**: Hass avocados ripening curve is on day 3; recommend highlighting *Nourish Avocado Quinoa Bowl* as Chef Recommendation for the lunch service.
        """.trimIndent()
      }
      else -> {
        """
        ✨ **CloudBite Black Label Executive Summary**:
        • **Total Orders Logged Today**: $totalOrders
        • **Gross Order Value (GOV)**: ₹${String.format("%,.2f", gov)}
        • **Active Hubs**: 5 prime Bengaluru operational kitchens (Indiranagar, Koramangala, Lavelle Rd, HSR, Jayanagar)
        • **Kitchen Health**: Prep times averaging 19.4 mins with 99.2% on-time dispatch rate.
        • **Key Strategic Priority**: Maintain luxury ingredient supply chains while scaling Virtual Kitchen capacity across the South Hub.
        """.trimIndent()
      }
    }
  }
}
