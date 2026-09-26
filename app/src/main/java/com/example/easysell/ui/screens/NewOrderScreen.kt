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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easysell.data.local.Product

@Composable
fun NewOrderScreen(
    orderName: String,
    products: List<Product>,
    onSave: (
        orderName: String,
        quantities: Map<Int, Int>
    ) -> Unit,
    onCancel: () -> Unit
) {
    var quantities by remember {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    val selectedItems = products.filter {
        (quantities[it.id] ?: 0) > 0
    }

    val total = selectedItems.sumOf { product ->
        product.price * (quantities[product.id] ?: 0)
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        // LEVÁ POLOVINA — SOUHRN
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {

            Text(
                text = orderName,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedItems) { product ->

                    val quantity = quantities[product.id] ?: 0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(product.name)
                            Text(
                                "$quantity × ${product.price} Kč"
                            )
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    val newQuantity = quantity - 1

                                    quantities =
                                        quantities.toMutableMap().apply {
                                            if (newQuantity <= 0) {
                                                remove(product.id)
                                            } else {
                                                put(product.id, newQuantity)
                                            }
                                        }
                                }
                            ) {
                                Text("−")
                            }

                            Text(
                                quantity.toString(),
                                modifier = Modifier.padding(8.dp)
                            )

                            IconButton(
                                onClick = {
                                    quantities =
                                        quantities.toMutableMap().apply {
                                            put(product.id, quantity + 1)
                                        }
                                }
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = "Celkem: %.2f Kč".format(total),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedItems.isNotEmpty(),
                onClick = {
                    onSave(orderName, quantities)
                }
            ) {
                Text("Uložit objednávku")
            }
        }

        VerticalDivider()

        // PRAVÁ POLOVINA — SORTIMENT
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(products) { product ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(product.name)
                            Text("${product.price} Kč")
                            Text(product.category.displayName)
                        }

                        Button(
                            onClick = {
                                val quantity =
                                    quantities[product.id] ?: 0

                                quantities =
                                    quantities.toMutableMap().apply {
                                        put(product.id, quantity + 1)
                                    }
                            }
                        ) {
                            Text("+")
                        }
                    }
                }
            }
        }
    }
}