package com.example.techport.ui.repair

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.techport.data.Repair
import com.example.techport.data.RepairStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairDetailScreen(
    repair: Repair,
    onBackClick: () -> Unit,
    viewModel: RepairViewModel = viewModel()
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repair Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
            )
        },
        bottomBar = {
            if (repair.status != RepairStatus.COMPLETED && repair.status != RepairStatus.CANCELLED) {
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
                        Button(
                            onClick = { showUpdateDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update Status")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Product Image
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AsyncImage(
                        model = repair.productImageUrl,
                        contentDescription = repair.productName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Product Name & Status
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = repair.productName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(status = repair.status)
                }
            }

            // Dates
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow(
                            label = "Requested",
                            value = formatDateTime(repair.createdAt),
                            icon = Icons.Default.DateRange
                        )
                        if (repair.updatedAt != repair.createdAt) {
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(
                                label = "Last Updated",
                                value = formatDateTime(repair.updatedAt),
                                icon = Icons.Default.Update
                            )
                        }
                        repair.completedAt?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(
                                label = "Completed",
                                value = formatDateTime(it),
                                icon = Icons.Default.CheckCircle
                            )
                        }
                    }
                }
            }

            // Issue Description
            item {
                Text(
                    text = "Issue Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = repair.issueDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Issue Photos
            if (repair.issueImageUrls.isNotEmpty()) {
                item {
                    Text(
                        text = "Issue Photos (${repair.issueImageUrls.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(repair.issueImageUrls) { imageUrl ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Issue photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Cost Information
            item {
                Text(
                    text = "Cost Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (repair.estimatedCost > 0) {
                            DetailRow(
                                label = "Estimated Cost",
                                value = "$${String.format("%.2f", repair.estimatedCost)}",
                                icon = Icons.Default.AttachMoney
                            )
                        }
                        if (repair.actualCost > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(
                                label = "Actual Cost",
                                value = "$${String.format("%.2f", repair.actualCost)}",
                                icon = Icons.Default.Paid
                            )
                        }
                        if (repair.estimatedCost == 0.0 && repair.actualCost == 0.0) {
                            Text(
                                text = "Cost estimation pending",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Technician Notes
            if (repair.technicianNotes.isNotEmpty()) {
                item {
                    Text(
                        text = "Technician Notes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = repair.technicianNotes,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Update Status Dialog
    if (showUpdateDialog) {
        UpdateStatusDialog(
            currentStatus = repair.status,
            onDismiss = { showUpdateDialog = false },
            onConfirm = { newStatus ->
                viewModel.updateRepairStatus(
                    repairId = repair.id,
                    newStatus = newStatus,
                    onSuccess = {
                        showUpdateDialog = false
                        onBackClick()
                    },
                    onError = {
                        showUpdateDialog = false
                    }
                )
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Repair") },
            text = { Text("Are you sure you want to delete this repair request? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRepair(
                            repairId = repair.id,
                            onSuccess = {
                                showDeleteDialog = false
                                onBackClick()
                            },
                            onError = { showDeleteDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UpdateStatusDialog(
    currentStatus: RepairStatus,
    onDismiss: () -> Unit,
    onConfirm: (RepairStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Repair Status") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status Selection
                Text("New Status:", style = MaterialTheme.typography.labelLarge)
                RepairStatus.entries.forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status.displayName)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStatus) }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}