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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easysell.data.ProductRepository
import com.example.easysell.data.local.ProductDatabase
import com.example.easysell.ui.GoodsViewModel
import com.example.easysell.ui.screens.GoodsScreen
import com.example.easysell.ui.screens.HistoryScreen
import com.example.easysell.ui.screens.HomeScreen
import com.example.easysell.ui.theme.EasySellTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ProductDatabase.getDatabase(applicationContext)
        val repository = ProductRepository(database.productDao())

        setContent {
            EasySellTheme {
                val goodsViewModel: GoodsViewModel = viewModel(
                    factory = GoodsViewModel.Factory(repository)
                )
                EasySellApp(viewModel = goodsViewModel)
            }
        }
    }
}

@Composable
fun EasySellApp(viewModel: GoodsViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val productList by viewModel.products.collectAsStateWithLifecycle(initialValue = emptyList())

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(destination.icon),
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen(
               // onNewOrderClick = { currentDestination = AppDestinations.NEW_ORDER }
            )
            AppDestinations.HISTORY -> HistoryScreen()
            AppDestinations.GOODS -> GoodsScreen(
                products = productList,
                onAddProduct = { newProduct -> viewModel.addProduct(newProduct) }
            )
    //        AppDestinations.NEW_ORDER -> NewOrderScreen(
    //            availableProducts = productList, // Zde předáme sortiment do objednávky
    //            onOrderFinished = { currentDestination = AppDestinations.HOME }
    //        )
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
