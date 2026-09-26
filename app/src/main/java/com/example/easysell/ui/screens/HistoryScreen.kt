package com.example.easysell.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easysell.data.local.OrderEntity
import com.example.easysell.data.local.OrderWithItems
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    orders: List<OrderEntity>,
    selectedOrder: OrderWithItems?,
    onOpenOrder: (String) -> Unit,
    onBack: () -> Unit
) {
    if (selectedOrder == null) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = orders,
                key = { it.id }
            ) { order ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOpenOrder(order.id)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            order.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(order.id)

                        Text(
                            SimpleDateFormat(
                                "dd.MM.yyyy HH:mm",
                                Locale.getDefault()
                            ).format(Date(order.createdAt))
                        )

                        Text(
                            "%.2f Kč".format(order.total),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

    } else {

        OrderDetailScreen(
            order = selectedOrder,
            onBack = onBack
        )
    }
}

@Composable
fun OrderDetailScreen(
    order: OrderWithItems,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("←")
            }

            Text(
                order.order.name,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Text(
            "ID: ${order.order.id}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(order.items) { item ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${item.productName} × ${item.quantity}"
                    )

                    Text(
                        "%.2f Kč".format(
                            item.unitPrice * item.quantity
                        )
                    )
                }
            }
        }

        HorizontalDivider()

        Text(
            "Celkem: %.2f Kč".format(order.order.total),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}