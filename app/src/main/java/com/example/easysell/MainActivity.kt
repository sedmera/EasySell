package com.example.easysell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.easysell.data.Product
import com.example.easysell.ui.screens.GoodsScreen
import com.example.easysell.ui.screens.HistoryScreen
import com.example.easysell.ui.screens.HomeScreen
import com.example.easysell.ui.theme.EasySellTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasySellTheme {
                EasySellApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun EasySellApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val productList = remember { mutableStateListOf<Product>() }

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
                onAddProduct = { newProduct -> productList.add(newProduct) }
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
