package com.example.techport.ui.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techport.data.RepairStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedPurchase by remember { mutableStateOf<Purchase?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPurchaseHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Purchase History") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.purchaseHistory.isEmpty()) {
                Text("No purchase history found.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(viewModel.purchaseHistory) { purchase ->
                        PurchaseItemCard(
                            purchase = purchase,
                            onReportClick = {
                                selectedPurchase = purchase
                                showReportDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showReportDialog) {
            ReportDialog(
                purchase = selectedPurchase!!,
                onDismiss = { showReportDialog = false },
                onConfirm = {
                    viewModel.reportProduct(selectedPurchase!!, it)
                    showReportDialog = false
                }
            )
        }
    }
}


@Composable
fun ReportDialog(purchase: Purchase, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var issueDescription by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Report Issue") },
        text = {
            Column {
                Text(text = "What's wrong with ${purchase.productName}?")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = issueDescription,
                    onValueChange = { issueDescription = it },
                    label = { Text("Describe the issue") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(issueDescription) },
                enabled = issueDescription.isNotBlank()
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PurchaseItemCard(purchase: Purchase, onReportClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = purchase.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Product ID: ${purchase.productId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatTimestamp(purchase.timestamp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                purchase.repairStatus?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Repair Status: ${it.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when (it) {
                            RepairStatus.PENDING -> Color.Blue
                            RepairStatus.IN_PROGRESS -> Color.Yellow
                            RepairStatus.COMPLETED -> Color.Green
                            RepairStatus.CANCELLED -> Color.Red
                        }
                    )
                }
            }
            if (purchase.repairStatus == null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Red, CircleShape)
                        .clickable { onReportClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Report an issue",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

