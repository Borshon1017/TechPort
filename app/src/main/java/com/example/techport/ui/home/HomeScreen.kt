package com.example.techport.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.techport.ui.theme.TechPOrtTheme

data class Product(val name: String)

@Composable
fun HomeScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val laptops = listOf(Product("Laptop 1"), Product("Laptop 2"), Product("Laptop 3"), Product("Laptop 4"))
    val phones = listOf(Product("Phone 1"), Product("Phone 2"), Product("Phone 3"), Product("Phone 4"))
    val accessories = listOf(Product("Accessory 1"), Product("Accessory 2"), Product("Accessory 3"), Product("Accessory 4"))
    val scrollState = rememberScrollState()

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
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(text = "Hello, Jade", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Ready to Explore?", style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Articles, Video, Audio and More,...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Buttons
        Row {
            Button(onClick = { /*TODO*/ }) { Text("Top Rated") }
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { /*TODO*/ }) { Text("New Arrival") }
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { /*TODO*/ }) { Text("Best Selling") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Featured Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Featured ads", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Laptops, Phones, Accessories Sections
        ProductSection(title = "Laptops", products = laptops)
        Spacer(modifier = Modifier.height(16.dp))
        ProductSection(title = "Phones", products = phones)
        Spacer(modifier = Modifier.height(16.dp))
        ProductSection(title = "Accessories", products = accessories)
    }
}

@Composable
fun ProductSection(title: String, products: List<Product>) {
    Column {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(products) { product ->
                ProductCard(product = product)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .size(150.dp, 120.dp)
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = product.name)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TechPOrtTheme {
        HomeScreen()
    }
}
