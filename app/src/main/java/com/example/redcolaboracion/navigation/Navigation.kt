package com.example.redcolaboracion.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import com.example.redcolaboracion.view.EventScreen
import com.example.redcolaboracion.view.GivedHelpScreen
import com.example.redcolaboracion.view.ProfileScreen
import com.example.redcolaboracion.view.RequestedHelpScreen
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(viewModel: EventViewModel) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        EventScreen(viewModel)
    }
}

@Composable
fun RequestedHelpScreenL() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        RequestedHelpScreen()
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

    }
}

@Composable
fun ProfileScreenL(viewModel: ProfileViewModel) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ProfileScreen(viewModel)
    }
}

@Composable
fun NavigationGraph(eventViewModel: EventViewModel, profileViewModel: ProfileViewModel, navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(eventViewModel) }
        composable(BottomNavItem.RequestedHelp.route) { RequestedHelpScreenL() }
        composable(BottomNavItem.GivedHelp.route) { GivedHelpScreenL() }
        composable(BottomNavItem.History.route) { HistoryScreenL() }
        composable(BottomNavItem.Profile.route) { ProfileScreenL(profileViewModel) }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(eventViewModel: EventViewModel, loginViewModel: ProfileViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar =  { BottomTabBar(navController = navController) }
    ) {
        NavigationGraph(eventViewModel, loginViewModel, navController)
    }
}

@Composable
fun BottomTabBar(navController: NavHostController) {
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
            NavigationBarItem(
                selected = isSelected,
                label = {  Text(text = barItem.title) },
                onClick = {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMain() {
    MainScreen(eventViewModel = EventViewModel(), loginViewModel = ProfileViewModel())
}
