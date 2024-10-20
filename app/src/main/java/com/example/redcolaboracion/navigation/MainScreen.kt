package com.example.redcolaboracion.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redcolaboracion.view.EventScreen
import com.example.redcolaboracion.view.GivedHelpScreen
import com.example.redcolaboracion.view.HistoryScreen
import com.example.redcolaboracion.view.LoginScreen
import com.example.redcolaboracion.view.ProfileScreen
import com.example.redcolaboracion.view.RequestedHelpScreen
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel
import com.example.redcolaboracion.viewmodel.RequestedHelpViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val isAuthenticated = remember { mutableStateOf(false) }
    val pendingRoute = remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar =  { BottomTabBar(navController, isAuthenticated, pendingRoute) }
    ) {
        NavigationGraph(navController, isAuthenticated, pendingRoute)
    }
}

@Composable
fun BottomTabBar(
    navController: NavHostController,
    isAuthenticated: MutableState<Boolean>,
    pendingRoute: MutableState<String?>
) {
    val tabBarItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.RequestedHelp,
        BottomNavItem.GivedHelp,
        BottomNavItem.History,
        BottomNavItem.Profile
    )

    BottomAppBar {
        val navBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStack?.destination?.route

        tabBarItems.forEach { barItem ->
            val isSelected = currentRoute?.startsWith(barItem.route) ?: false
            val needAuth = barItem.authentication

            NavigationBarItem(
                selected = isSelected,
                label = {  Text(text = barItem.title) },
                onClick = {
                    if (needAuth == false || (needAuth == true and isAuthenticated.value)) {
                        navController.navigate(barItem.route) {
                            navController.graph.startDestinationRoute.let { route ->
                                if (route != null) {
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                            }
                            // evitar que se recomponga la misma ruta
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        //pendingRoute.value = route
                        navController.navigate("login"){
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) barItem.selectedIcon else barItem.unselectedIcon,
                        contentDescription = barItem.title
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    isAuthenticated: MutableState<Boolean>,
    pendingRoute: MutableState<String?>
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.RequestedHelp.route) { RequestedHelpScreenL() }
        composable(BottomNavItem.GivedHelp.route) { GivedHelpScreenL() }
        composable(BottomNavItem.History.route) { HistoryScreenL() }
        composable(BottomNavItem.Profile.route) { ProfileScreenL() }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    isAuthenticated.value = true
                    pendingRoute.value?.let { navController.navigate(it) }
                    pendingRoute.value = null
                    navController.popBackStack("login", inclusive = true)
                },
                onLoginFailed = {
                    println("Error en el usuario o contraseña.")
                    Toast.makeText(
                        context,
                        "Error en el usuario o contraseña. Vuelva a intentarlo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
fun HomeScreen() {
    val eventViewModel: EventViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        EventScreen(eventViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequestedHelpScreenL() {
    val requestedHelpViewModel: RequestedHelpViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        RequestedHelpScreen(requestedHelpViewModel)
    }
}

@Composable
fun GivedHelpScreenL() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        GivedHelpScreen()
    }
}
@Composable
fun HistoryScreenL() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        HistoryScreen()
    }
}

@Composable
fun ProfileScreenL() {
    val profileViewModel: ProfileViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ProfileScreen(profileViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMain() {
    MainScreen()
}
