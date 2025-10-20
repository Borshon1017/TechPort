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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Purchase History", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
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
                Text("No purchase history found.", modifier = Modifier.align(Alignment.Center), color = Color.Black)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(viewModel.purchaseHistory.reversed()) { purchase ->
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
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text(text = "Report Issue", color = Color.Black) },
        text = {
            Column {
                Text(text = "What's wrong with ${purchase.productName}?", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = issueDescription,
                    onValueChange = { issueDescription = it },
                    label = { Text("Describe the issue") },
                    modifier = Modifier.fillMaxWidth(),
                     colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(issueDescription) },
                enabled = issueDescription.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PurchaseItemCard(purchase: Purchase, onReportClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = purchase.imageUrl,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = purchase.productName,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${purchase.productId}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(purchase.timestamp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                purchase.repairStatus?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(status = it)
                }
            }
            if (purchase.repairStatus == null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onReportClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Report an issue",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return "Purchased on ${sdf.format(Date(timestamp))}"
}

@Composable
fun StatusBadge(status: RepairStatus) {
    val (backgroundColor, textColor) = when (status) {
        RepairStatus.PENDING -> Color(0xFFFFFBEA) to Color(0xFFB45309)
        RepairStatus.IN_PROGRESS -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
        RepairStatus.COMPLETED -> Color(0xFFF0FDF4) to Color(0xFF16A34A)
        RepairStatus.CANCELLED -> Color(0xFFFEF2F2) to Color(0xFFB91C1C)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
