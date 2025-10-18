package com.example.techport.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techport.ui.theme.TechPOrtTheme

data class Product(val name: String)

@Composable
fun HomeScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Top Rated") }
    val laptops = listOf(Product("Laptop 1"), Product("Laptop 2"), Product("Laptop 3"), Product("Laptop 4"))
    val phones = listOf(Product("Phone 1"), Product("Phone 2"), Product("Phone 3"), Product("Phone 4"))
    val accessories = listOf(Product("Accessory 1"), Product("Accessory 2"), Product("Accessory 3"), Product("Accessory 4"))
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Hello, Jade", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "Ready to Explore?", style = MaterialTheme.typography.headlineSmall, color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Articles, Video, Audio and More,...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Filter Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterButton("Top Rated", selectedFilter) { selectedFilter = it }
                FilterButton("New Arrival", selectedFilter) { selectedFilter = it }
                FilterButton("Best Selling", selectedFilter) { selectedFilter = it }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Header for Featured
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Featured", style = MaterialTheme.typography.titleLarge, color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "See more >", color = Color.Gray, modifier = Modifier.clickable {})
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Featured Card
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Featured ads", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product Sections
            ProductSection(title = "Laptops", products = laptops)
            Spacer(modifier = Modifier.height(24.dp))
            ProductSection(title = "Phones", products = phones)
            Spacer(modifier = Modifier.height(24.dp))
            ProductSection(title = "Accessories", products = accessories)
        }
    }
}

@Composable
fun FilterButton(text: String, selectedFilter: String, onClick: (String) -> Unit) {
    val isSelected = text == selectedFilter

    val glowColor = when (text) {
        "Top Rated" -> Color.Red
        "New Arrival" -> Color.Green
        "Best Selling" -> Color.Blue
        else -> Color.Transparent
    }

    val glowModifier = if (isSelected) {
        Modifier.shadow(elevation = 8.dp, spotColor = glowColor, shape = MaterialTheme.shapes.extraLarge)
    } else {
        Modifier
    }

    if (isSelected) {
        Button(
            onClick = { onClick(text) },
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            modifier = glowModifier
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = { onClick(text) },
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.dp, Color.LightGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Text(text)
        }
    }
}

@Composable
fun ProductSection(title: String, products: List<Product>) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = Color.Black, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(products) { product ->
                ProductCard(product = product)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.size(160.dp, 180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = product.name, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    TechPOrtTheme {
        HomeScreen()
    }
}
