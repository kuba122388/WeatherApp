package com.example.weatherapp

import SharedPreferencesHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.api.City
import com.example.weatherapp.ui.theme.AppBackgroundGradient
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.weatherViewModel.WeatherViewModel
import com.example.weatherapp.weatherViewModel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    private val sharedPreferencesHelper by lazy { SharedPreferencesHelper(this) }
    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(sharedPreferencesHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(viewModel = weatherViewModel, navController = navController)
                        }
                        composable("weather/{cityName}") { backStackEntry ->
                            val cityName = backStackEntry.arguments?.getString("cityName")
                            val cityRegion = backStackEntry.arguments?.getString("cityRegion")
                            WeatherHomeScreen(cityName = cityName ?: "", cityRegion = cityRegion ?: "", onSettingsClick = {})
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen(viewModel: WeatherViewModel, navController: NavController) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(1) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBackgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavBar(selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it })
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                when (selectedIndex) {
                    0 -> SearchCityScreen(viewModel) { selectedIndex = 3 }
                    1 -> WeatherHomeScreen {selectedIndex = 3 }
                    2 -> FavoriteScreen(viewModel, navController) { selectedIndex = 3 }
                    3 -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    return NavigationBar(
        containerColor = Color.Transparent,
    ) {
        NavigationBarItem(
            icon = {
                Image(
                    painterResource(id = R.drawable.icon_search),
                    contentDescription = "Home",
                    modifier = if (selectedIndex == 0) Modifier.alpha(1f) else Modifier.alpha(0.3f)
                )
            },
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0x11FFFFFF)
            )
        )
        NavigationBarItem(
            icon = {
                Image(
                    painterResource(id = R.drawable.icon_home),
                    contentDescription = "Forecast",
                    modifier = if (selectedIndex == 1) Modifier.alpha(1f) else Modifier.alpha(0.3f)
                )
            },
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0x11FFFFFF)
            )
        )
        NavigationBarItem(
            icon = {
                Image(
                    painterResource(id = R.drawable.icon_emptyheart),
                    contentDescription = "Settings",
                    modifier = if (selectedIndex == 2) Modifier.alpha(1f) else Modifier.alpha(0.3f)
                )
            },
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0x11FFFFFF)
            )
        )
    }
}

