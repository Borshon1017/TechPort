package com.example.techport.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Enhanced Product data class with complete information
data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Float,
    val image: String,
    val specs: Map<String, String>,
    val description: String,
    val isRecommended: Boolean = false
)

// Sample data with realistic products
object ProductData {
    val categories = listOf(
        "Smartphones", "Laptops", "Tablets", "Accessories",
        "Audio", "Cameras", "Wearables", "Gaming"
    )

    val products = listOf(
        Product(
            id = 1,
            name = "TechPhone Pro 15",
            category = "Smartphones",
            price = 899.99,
            rating = 4.5f,
            image = "📱",
            specs = mapOf(
                "Display" to "6.7\" OLED",
                "Processor" to "Snapdragon 8 Gen 3",
                "RAM" to "12GB",
                "Storage" to "256GB",
                "Camera" to "108MP + 12MP + 8MP",
                "Battery" to "5000mAh"
            ),
            description = "Experience flagship performance with our latest smartphone featuring stunning display and advanced camera system.",
            isRecommended = true
        ),
        Product(
            id = 2,
            name = "UltraBook Air M3",
            category = "Laptops",
            price = 1299.99,
            rating = 4.8f,
            image = "💻",
            specs = mapOf(
                "Display" to "14\" Retina",
                "Processor" to "M3 Chip",
                "RAM" to "16GB",
                "Storage" to "512GB SSD",
                "Graphics" to "Integrated GPU",
                "Battery Life" to "Up to 18 hours"
            ),
            description = "Ultra-thin and powerful laptop perfect for professionals and creators on the go.",
            isRecommended = true
        ),
        Product(
            id = 3,
            name = "TabPro 12 Ultra",
            category = "Tablets",
            price = 699.99,
            rating = 4.6f,
            image = "📲",
            specs = mapOf(
                "Display" to "12.4\" AMOLED",
                "Processor" to "Dimensity 9000+",
                "RAM" to "8GB",
                "Storage" to "256GB",
                "Camera" to "13MP + 8MP",
                "S-Pen" to "Included"
            ),
            description = "Versatile tablet with stunning display and productivity features for work and entertainment."
        ),
        Product(
            id = 4,
            name = "AirBuds Pro Max",
            category = "Audio",
            price = 249.99,
            rating = 4.7f,
            image = "🎧",
            specs = mapOf(
                "Driver" to "40mm Dynamic",
                "ANC" to "Hybrid Active Noise Cancelling",
                "Battery" to "30 hours with ANC",
                "Connectivity" to "Bluetooth 5.3",
                "Water Resistance" to "IPX4",
                "Fast Charging" to "5 min = 3 hours"
            ),
            description = "Premium wireless headphones with exceptional sound quality and all-day comfort.",
            isRecommended = true
        ),
        Product(
            id = 5,
            name = "PixelCam 4K Pro",
            category = "Cameras",
            price = 1499.99,
            rating = 4.9f,
            image = "📷",
            specs = mapOf(
                "Sensor" to "Full Frame 45MP",
                "Video" to "4K 60fps",
                "ISO Range" to "100-51200",
                "Stabilization" to "5-axis IBIS",
                "Autofocus" to "693-point AF",
                "Storage" to "Dual SD Card Slots"
            ),
            description = "Professional mirrorless camera for stunning photos and cinematic videos."
        ),
        Product(
            id = 6,
            name = "SmartWatch X5",
            category = "Wearables",
            price = 349.99,
            rating = 4.4f,
            image = "⌚",
            specs = mapOf(
                "Display" to "1.9\" AMOLED",
                "Health Tracking" to "Heart Rate, SpO2, Sleep",
                "Battery" to "7 days typical use",
                "Water Resistance" to "5ATM",
                "GPS" to "Built-in GPS/GLONASS",
                "Sensors" to "ECG, Gyroscope, Compass"
            ),
            description = "Advanced smartwatch with comprehensive health tracking and fitness features."
        ),
        Product(
            id = 7,
            name = "GamePad Elite",
            category = "Gaming",
            price = 179.99,
            rating = 4.6f,
            image = "🎮",
            specs = mapOf(
                "Connectivity" to "Wireless + USB-C",
                "Battery" to "40 hours",
                "Features" to "Programmable buttons, RGB",
                "Compatibility" to "PC, Console, Mobile",
                "Response Time" to "1ms",
                "Vibration" to "Dual Motor Haptic"
            ),
            description = "Professional gaming controller with customizable buttons and ultra-responsive controls.",
            isRecommended = true
        ),
        Product(
            id = 8,
            name = "PowerBank Ultra 30K",
            category = "Accessories",
            price = 79.99,
            rating = 4.5f,
            image = "🔋",
            specs = mapOf(
                "Capacity" to "30000mAh",
                "Output" to "100W PD 3.0",
                "Ports" to "2x USB-C, 1x USB-A",
                "Input" to "65W Fast Recharge",
                "Display" to "LED Power Display",
                "Safety" to "Multi-Protection System"
            ),
            description = "High-capacity power bank with fast charging for all your devices."
        ),
        Product(
            id = 9,
            name = "MechKeys Pro RGB",
            category = "Accessories",
            price = 159.99,
            rating = 4.7f,
            image = "⌨️",
            specs = mapOf(
                "Switch Type" to "Mechanical Hot-Swap",
                "Layout" to "75% Compact",
                "Connectivity" to "Wireless + Wired",
                "Battery" to "3000mAh",
                "RGB" to "Per-Key RGB Lighting",
                "Material" to "Aluminum Frame"
            ),
            description = "Premium mechanical keyboard with customizable switches and stunning RGB."
        ),
        Product(
            id = 10,
            name = "Studio Monitor 27\"",
            category = "Accessories",
            price = 449.99,
            rating = 4.8f,
            image = "🖥️",
            specs = mapOf(
                "Size" to "27\" 4K UHD",
                "Panel" to "IPS",
                "Refresh Rate" to "60Hz",
                "Color Accuracy" to "99% sRGB",
                "Connectivity" to "HDMI, DisplayPort, USB-C",
                "Stand" to "Height/Tilt Adjustable"
            ),
            description = "Professional monitor with exceptional color accuracy for creators."
        )
    )

    fun getRecommendedProducts() = products.filter { it.isRecommended }

    fun searchProducts(query: String) = products.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
    }

    fun filterByCategory(category: String) = products.filter { it.category == category }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "Jade",
    onProductClick: (Product) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredProducts = remember(searchQuery, selectedCategory) {
        when {
            searchQuery.isNotEmpty() -> ProductData.searchProducts(searchQuery)
            selectedCategory != null -> ProductData.filterByCategory(selectedCategory!!)
            else -> ProductData.products
        }
    }

    val recommendedProducts = ProductData.getRecommendedProducts()

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, $userName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ready to Explore?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                Text(
                    text = "Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ProductData.categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) null else category
                                searchQuery = ""
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (searchQuery.isEmpty() && selectedCategory == null && recommendedProducts.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recommended for You",
                        icon = "⭐"
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recommendedProducts) { product ->
                            RecommendedProductCard(
                                product = product,
                                onClick = { onProductClick(product) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                val title = when {
                    searchQuery.isNotEmpty() -> "Search Results (${filteredProducts.size})"
                    selectedCategory != null -> selectedCategory!!
                    else -> "All Products"
                }

                SectionHeader(title = title, icon = "🛍️")
            }

            item {
                if (filteredProducts.isEmpty()) {
                    EmptyState(searchQuery = searchQuery)
                } else {
                    ProductGrid(
                        products = filteredProducts,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                "Search for products...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
    )
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.animateContentSize()
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RecommendedProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.image,
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFFFC107)
                )
                Text(
                    text = product.rating.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "$${product.price}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),  // ← Changed this line!
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.image,
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = product.category,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${product.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = product.rating.toString(),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔍",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No products found",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Try searching for something else",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}