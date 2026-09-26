package com.example.easysell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easysell.data.ProductRepository
import com.example.easysell.data.local.ProductDatabase
import com.example.easysell.ui.EasySellViewModel
import com.example.easysell.ui.screens.GoodsScreen
import com.example.easysell.ui.screens.HistoryScreen
import com.example.easysell.ui.screens.HomeScreen
import com.example.easysell.ui.screens.NewOrderScreen
import com.example.easysell.ui.theme.EasySellTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ProductDatabase.getDatabase(applicationContext)
        val repository = ProductRepository(database.productDao())

        setContent {
            EasySellTheme {
                val easySellViewModel: EasySellViewModel = viewModel(
                    factory = EasySellViewModel.Factory(repository)
                )
                EasySellApp(viewModel = easySellViewModel)
            }
        }
    }
}

@Composable
fun EasySellApp(
    viewModel: EasySellViewModel
) {
    var currentDestination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }

    var creatingOrder by rememberSaveable {
        mutableStateOf(false)
    }

    var orderName by rememberSaveable {
        mutableStateOf("")
    }

    var historyDetailId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val products by viewModel.products
        .collectAsStateWithLifecycle()

    val orders by viewModel.orders
        .collectAsStateWithLifecycle()

    val selectedOrder by historyDetailId
        ?.let { viewModel.getOrder(it) }
        ?.collectAsStateWithLifecycle(initialValue = null)
        ?: remember {
            mutableStateOf(null)
        }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    selected =
                        destination == currentDestination,
                    onClick = {
                        currentDestination = destination
                        creatingOrder = false
                    },
                    icon = {
                        Icon(
                            painterResource(destination.icon),
                            contentDescription = destination.label
                        )
                    },
                    label = {
                        Text(destination.label)
                    }
                )
            }
        }
    ) {

        when {
            creatingOrder -> {
                NewOrderScreen(
                    orderName = orderName,
                    products = products,
                    onSave = { name, quantities ->
                        viewModel.saveOrder(
                            name = name,
                            products = products,
                            quantities = quantities
                        )

                        creatingOrder = false
                    },
                    onCancel = {
                        creatingOrder = false
                    }
                )
            }

            currentDestination == AppDestinations.HOME -> {
                HomeScreen(
                    onNewOrderClick = {
                        // otevření dialogu
                    }
                )
            }

            currentDestination == AppDestinations.HISTORY -> {
                HistoryScreen(
                    orders = orders,
                    selectedOrder = selectedOrder,
                    onOpenOrder = {
                        historyDetailId = it
                    },
                    onBack = {
                        historyDetailId = null
                    }
                )
            }

            currentDestination == AppDestinations.GOODS -> {
                GoodsScreen(
                    products = products,
                    onAddProduct = viewModel::addProduct,
                    onUpdateProduct = viewModel::updateProduct,
                    onDeleteProduct = viewModel::deleteProduct
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.home_icon),
    HISTORY("History", R.drawable.history_icon),
    GOODS("Goods", R.drawable.dataset_icon),
}
