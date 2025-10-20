package com.example.techport.ui.repair

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techport.data.Repair
import com.example.techport.data.RepairStatus
import com.example.techport.ui.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRepairScreen(
    onBackClick: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
    repairViewModel: RepairViewModel = viewModel()
) {
    var selectedProductId by remember { mutableStateOf("") }
    var selectedProductName by remember { mutableStateOf("") }
    var selectedProductImageUrl by remember { mutableStateOf("") }
    var issueDescription by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var estimatedCost by remember { mutableStateOf("") }
    var expandedProduct by remember { mutableStateOf(false) }

    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Repair") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (selectedProductId.isBlank()) {
                                errorMessage = "Please select a product"
                                return@Button
                            }
                            if (issueDescription.isBlank()) {
                                errorMessage = "Please describe the issue"
                                return@Button
                            }

                            val repair = Repair(
                                productId = selectedProductId,
                                productName = selectedProductName,
                                productImageUrl = selectedProductImageUrl,
                                issueDescription = issueDescription,
                                issueImageUrls = imageUrls,
                                estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                                status = RepairStatus.PENDING
                            )

                            repairViewModel.createRepair(
                                repair = repair,
                                onSuccess = { showSuccess = true },
                                onError = { error -> errorMessage = error }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !repairViewModel.isLoading
                    ) {
                        if (repairViewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Submit Request")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Product Information",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Product Selection
            item {
                ExposedDropdownMenuBox(
                    expanded = expandedProduct,
                    onExpandedChange = { expandedProduct = it }
                ) {
                    OutlinedTextField(
                        value = selectedProductName.ifEmpty { "Select Product" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Product *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProduct)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        leadingIcon = { Icon(Icons.Default.ShoppingBag, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProduct,
                        onDismissRequest = { expandedProduct = false }
                    ) {
                        homeViewModel.products.forEach { product ->
                            DropdownMenuItem(
                                text = { Text(product.name) },
                                onClick = {
                                    selectedProductId = product.id
                                    selectedProductName = product.name
                                    selectedProductImageUrl = product.imageUrl
                                    expandedProduct = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
                Text(
                    text = "Issue Details",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Issue Description
            item {
                OutlinedTextField(
                    value = issueDescription,
                    onValueChange = { issueDescription = it },
                    label = { Text("Describe the Issue *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6,
                    placeholder = { Text("What's wrong with your product? Please provide details...") },
                    leadingIcon = { Icon(Icons.Default.Description, null) }
                )
            }

            // Image URLs
            item {
                Text(
                    text = "Issue Photos",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Add image URLs showing the issue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Image, null) },
                    placeholder = { Text("https://example.com/image.jpg") }
                )
            }

            item {
                Button(
                    onClick = {
                        if (imageUrl.isNotBlank()) {
                            imageUrls = imageUrls + imageUrl
                            imageUrl = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Photo")
                }
            }

            // Display added images
            if (imageUrls.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Added Photos (${imageUrls.size})",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            imageUrls.forEachIndexed { index, url ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Photo ${index + 1}",
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                    IconButton(
                                        onClick = { imageUrls = imageUrls.filterIndexed { i, _ -> i != index } },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Estimated Cost
            item {
                OutlinedTextField(
                    value = estimatedCost,
                    onValueChange = { estimatedCost = it },
                    label = { Text("Estimated Cost (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                    prefix = { Text("$") },
                    placeholder = { Text("0.00") }
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "What happens next?",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Our technicians will review your request and provide a repair estimate within 24 hours.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    // Success Dialog
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Request Submitted!") },
            text = { Text("Your repair request has been submitted. We'll contact you soon with an estimate.") },
            confirmButton = {
                Button(onClick = {
                    showSuccess = false
                    onBackClick()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}