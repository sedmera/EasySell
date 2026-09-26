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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.easysell.data.local.OrderEntity
import com.example.easysell.data.local.OrderWithItems
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    orders: List<OrderEntity>,
    selectedOrderId: String?,
    selectedOrder: OrderWithItems?,
    onOpenOrder: (String) -> Unit,
    onBack: () -> Unit
) {
    when {
// Detail byl vybrán, ale ještě se načítá z databáze.
        selectedOrderId != null && selectedOrder == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Máme načtený detail objednávky.
        selectedOrder != null -> {
        OrderDetailScreen(
            order = selectedOrder,
            onBack = onBack
        )
    }

        // Není vybraný žádný detail -> zobraz seznam.
        else -> {
            OrderHistoryList(
                orders = orders,
                onOpenOrder = onOpenOrder
            )
        }
    }

}

@Composable
private fun OrderHistoryList(
    orders: List<OrderEntity>,
    onOpenOrder: (String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Historie objednávek je prázdná",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Uložené objednávky se zde zobrazí.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = orders,
            key = { it.id }
        ) { order ->

            OrderHistoryItem(
                order = order,
                onClick = {
                    onOpenOrder(order.id)
                }
            )
        }
    }

}

@Composable
private fun OrderHistoryItem(
    order: OrderEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = order.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = formatDate(order.createdAt),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = formatMoney(order.total),
                style = MaterialTheme.typography.titleMedium
            )
        }
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack
            ) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = order.order.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formatDate(order.order.createdAt),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "ID objednávky",
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = order.order.id,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (order.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Objednávka neobsahuje žádné položky."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = order.items,
                    key = { it.id }
                ) { item ->

                    OrderItemRow(
                        name = item.productName,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        totalPrice = item.unitPrice * item.quantity
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        HorizontalDivider()

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Celkem",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = formatMoney(order.order.total),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Zpět na historii")
        }
    }

}

@Composable
private fun OrderItemRow(
    name: String,
    quantity: Int,
    unitPrice: Double,
    totalPrice: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "$quantity × ${formatMoney(unitPrice)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = formatMoney(totalPrice),
            style = MaterialTheme.typography.titleMedium
        )
    }

}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat(
        "dd.MM.yyyy HH:mm",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )
}

private fun formatMoney(value: Double): String {
    return String.format(
        Locale.getDefault(),
        "%.2f Kč",
        value
    )
}
