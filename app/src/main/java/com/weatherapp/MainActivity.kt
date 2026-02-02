package com.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weatherapp.model.MainViewModel
import com.weatherapp.ui.CityDialog
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.ui.nav.BottomNavBar
import com.weatherapp.ui.nav.BottomNavItem
import com.weatherapp.ui.nav.MainNavHost
import com.weatherapp.ui.nav.Route
import androidx.navigation.NavDestination.Companion.hasRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.api.WeatherService
import com.weatherapp.model.MainViewModelFactory
import com.weatherapp.db.fb.FBDatabase

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val fbDB = remember { FBDatabase() }
            val weatherService = remember { WeatherService(this) }
            val viewModel : MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB, weatherService))
            var showDialog by remember { mutableStateOf(false) }
            //val viewModel : MainViewModel by viewModels()
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {})

            WeatherAppTheme {
                if(showDialog) CityDialog(
                    onDismiss = { showDialog = false},
                    onConfirm = { city ->
                        if(city.isNotBlank()) viewModel.addCity(city)
                        showDialog = false
                    }
                )
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { val name = viewModel.user?.name?:"[carregando...]"
                                Text("Bem-vindo/a! $name") },

                            actions = {

                                IconButton( onClick = {
                                    Firebase.auth.signOut() } ) {
                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    },

                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,

                            )

                        BottomNavBar(viewModel, items)

                    },

                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }

                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(navController = navController, viewModel)
                        LaunchedEffect(viewModel.page) {
                            navController.navigate(viewModel.page) {
                                // Volta pilha de navegação até HomePage (startDest).
                                navController.graph.startDestinationRoute?.let {
                                    popUpTo(it) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        Greeting("Android")
    }
}

