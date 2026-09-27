package com.example.easysell.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easysell.data.local.Product
import java.util.Locale

@Composable
fun NewOrderScreen(
    orderName: String,
    products: List<Product>,
    onSave: (
        name: String,
        quantities: Map<Int, Int>
    ) -> Unit,
    onCancel: () -> Unit
) {
    var quantities by remember {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    val selectedProducts = products.filter { product ->
        (quantities[product.id] ?: 0) > 0
    }

    val total = selectedProducts.sumOf { product ->
        product.price * (quantities[product.id] ?: 0)
    }

    fun increase(product: Product) {
        val current = quantities[product.id] ?: 0

        quantities = quantities.toMutableMap().apply {
            this[product.id] = current + 1
        }
    }

    fun decrease(product: Product) {
        val current = quantities[product.id] ?: 0

        quantities = quantities.toMutableMap().apply {
            if (current <= 1) {
                remove(product.id)
            } else {
                this[product.id] = current - 1
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = orderName,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Souhrn objednávky",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                TextButton(
                    onClick = onCancel
                ) {
                    Text("Zrušit")
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = selectedProducts,
                    key = { it.id }
                ) { product ->

                    val quantity =
                        quantities[product.id] ?: 0

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    product.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    String.format(
                                        Locale.getDefault(),
                                        "%.2f Kč × %d",
                                        product.price,
                                        quantity
                                    )
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        decrease(product)
                                    }
                                ) {
                                    Text("−")
                                }

                                Text(
                                    text = quantity.toString(),
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 8.dp
                                    )
                                )

                                IconButton(
                                    onClick = {
                                        increase(product)
                                    }
                                ) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = String.format(
                    Locale.getDefault(),
                    "Celkem: %.2f Kč",
                    total
                ),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedProducts.isNotEmpty(),
                onClick = {
                    onSave(
                        orderName,
                        quantities
                    )
                }
            ) {
                Text("Uložit objednávku")
            }
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = "Sortiment",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = products,
                    key = { it.id }
                ) { product ->

                    val quantity =
                        quantities[product.id] ?: 0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            increase(product)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = product.category.colors().container,
                            contentColor = product.category.colors().content
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    product.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    product.category.displayName
                                )

                                Text(
                                    String.format(
                                        Locale.getDefault(),
                                        "%.2f Kč",
                                        product.price
                                    )
                                )
                            }

                            if (quantity > 0) {
                                Text(
                                    "× $quantity",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            } else {
                                TextButton(
                                    onClick = {
                                        increase(product)
                                    }
                                ) {
                                    Text("Přidat")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}