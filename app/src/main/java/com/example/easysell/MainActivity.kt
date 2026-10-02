package com.example.easysell

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easysell.data.OrderRepository
import com.example.easysell.data.ProductRepository
import com.example.easysell.data.UserPreferencesRepository
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

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1001
            )
        }

        val database = ProductDatabase.getDatabase(applicationContext)
        val userPreferencesRepository =
            UserPreferencesRepository(applicationContext)

        val productRepository = ProductRepository(
            database.productDao()
        )

        val orderRepository = OrderRepository(
            database.orderDao()
        )

        setContent {
            EasySellTheme {
                val easySellViewModel: EasySellViewModel = viewModel(
                    factory = EasySellViewModel.Factory(
                        productRepository = productRepository,
                        orderRepository = orderRepository,
                        userPreferencesRepository = userPreferencesRepository
                    )
                )

                EasySellApp(
                    viewModel = easySellViewModel
                )
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

    var showNewOrderDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val orderName by viewModel.orderName
        .collectAsStateWithLifecycle()

    var historyDetailId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val products by viewModel.products.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    val selectedOrderState =
        historyDetailId?.let { orderId ->
            viewModel.getOrder(orderId)
                .collectAsStateWithLifecycle(initialValue = null)
        }

    val selectedOrder = selectedOrderState?.value

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    selected = destination == currentDestination,
                    onClick = {
                        currentDestination = destination
                        creatingOrder = false
                        showNewOrderDialog = false
                        historyDetailId = null
                    },
                    icon = {
                        Icon(
                            painter = painterResource(destination.icon),
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
                    orderName = orderName,
                    onOrderNameChange = viewModel::setOrderName,
                    onNewOrderClick = {
                        creatingOrder = true
                    }
                )
            }

            currentDestination == AppDestinations.HISTORY -> {
                HistoryScreen(
                    orders = orders,
                    selectedOrderId = historyDetailId,
                    selectedOrder = selectedOrder,
                    onOpenOrder = { id ->
                        historyDetailId = id
                    },
                    onBack = {
                        historyDetailId = null
                    },
                    onDeleteOrder = { id ->
                        viewModel.deleteOrder(id)
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
    val icon: Int
) {
    HOME("Home", R.drawable.home_icon),
    HISTORY("History", R.drawable.history_icon),
    GOODS("Goods", R.drawable.dataset_icon)
}