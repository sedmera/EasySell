package com.example.easysell.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.easysell.data.local.Product
import com.example.easysell.data.local.ProductCategory
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsScreen(
    products: List<Product>,
    onAddProduct: (Product) -> Unit,
    onUpdateProduct: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit
) {
    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingProduct by remember {
        mutableStateOf<Product?>(null)
    }

    var selectedCategoryFilter by remember {
        mutableStateOf<ProductCategory?>(null)
    }

    val filteredProducts = remember(
        products,
        selectedCategoryFilter
    ) {
        val filtered =
            if (selectedCategoryFilter == null) {
                products
            } else {
                products.filter {
                    it.category == selectedCategoryFilter
                }
            }

        filtered.sortedBy {
            it.category.displayName
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Přidat sortiment"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Správa sortimentu",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected =
                        selectedCategoryFilter == null,
                    onClick = {
                        selectedCategoryFilter = null
                    },
                    label = {
                        Text("Vše")
                    }
                )

                ProductCategory.entries.forEach { category ->
                    FilterChip(
                        selected =
                            selectedCategoryFilter == category,
                        onClick = {
                            selectedCategoryFilter =
                                if (selectedCategoryFilter == category) {
                                    null
                                } else {
                                    category
                                }
                        },
                        label = {
                            Text(category.displayName)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Žádný sortiment k zobrazení")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredProducts,
                        key = { it.id }
                    ) { product ->
                        ProductItemCard(
                            product = product,
                            onUpdate = {
                                onUpdateProduct(it)
                            },
                            onDelete = {
                                onDeleteProduct(it)
                            },
                            onEdit = {
                                editingProduct = product
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductEditorDialog(
            product = null,
            onDismiss = {
                showAddDialog = false
            },
            onSave = { product ->
                onAddProduct(product)
                showAddDialog = false
            }
        )
    }

    editingProduct?.let { product ->
        ProductEditorDialog(
            product = product,
            onDismiss = {
                editingProduct = null
            },
            onSave = {
                onUpdateProduct(it)
                editingProduct = null
            },
            onDelete = {
                onDeleteProduct(it)
                editingProduct = null
            }
        )
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onUpdate: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit,
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
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "Druh: ${product.category.displayName}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                String.format(
                    Locale.getDefault(),
                    "%.2f Kč",
                    product.price
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditorDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    onDelete: ((Product) -> Unit)? = null
) {
    var name by remember(product) {
        mutableStateOf(product?.name ?: "")
    }

    var priceText by remember(product) {
        mutableStateOf(
            product?.price?.toString() ?: ""
        )
    }

    var category by remember(product) {
        mutableStateOf(
            product?.category ?: ProductCategory.FOOD
        )
    }

    var expanded by remember(product) {
        mutableStateOf(false)
    }

    val price =
        priceText
            .replace(',', '.')
            .toDoubleOrNull()

    val valid =
        name.isNotBlank() &&
                price != null &&
                price > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (product == null) {
                    "Přidat produkt"
                } else {
                    "Upravit produkt"
                }
            )
        },

        text = {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Název položky")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        priceText = it
                    },
                    label = {
                        Text("Cena (Kč)")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Druh zboží")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded = expanded
                                )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        ProductCategory.entries.forEach {
                                categoryOption ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        categoryOption.displayName
                                    )
                                },
                                onClick = {
                                    category =
                                        categoryOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {
            Button(
                enabled = valid,
                onClick = {
                    onSave(
                        Product(
                            id = product?.id ?: 0,
                            name = name.trim(),
                            price = price!!,
                            category = category
                        )
                    )
                }
            ) {
                Text("Uložit")
            }
        },

        dismissButton = {
            Row {
                if (product != null && onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete(product)
                        }
                    ) {
                        Text("Smazat")
                    }
                }

                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Zrušit")
                }
            }
        }
    )
}