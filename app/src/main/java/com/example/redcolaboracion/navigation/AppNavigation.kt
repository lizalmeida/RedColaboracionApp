package com.example.redcolaboracion.navigation

import android.net.Uri
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.view.SplashScreen
import com.example.redcolaboracion.view.CameraPreview
import com.example.redcolaboracion.view.EventScreen
import com.example.redcolaboracion.view.ProfileScreen
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
        //startDestination = BottomNavItem.Home.route
    ){
        composable(AppScreens.SplashScreen.route){
            SplashScreen()
        }
        composable(AppScreens.EventScreen.route){
            EventScreen(viewModel = EventViewModel())
        }
        composable(AppScreens.CameraPreview.route) {
            CameraPreview(navController)
        }
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(viewModel = ProfileViewModel())
        }
    }
}

@Composable
fun BottomTabBar2(navController: NavHostController){
    val tabBarItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.RequestedHelp,
        BottomNavItem.GivedHelp,
        BottomNavItem.History,
        BottomNavItem.Profile,
    )
    BottomAppBar {
        val navBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStack?.destination?.route

        tabBarItems.forEach{barItem->
            val isSelected = currentRoute == barItem.route
            NavigationBarItem(
                selected = isSelected,
                label = {
                        Text(text = barItem.title)
                },
                onClick = {
                          navController.navigate(barItem.route)
                },
                icon = {
                    Icon(
                        imageVector = if(isSelected) barItem.selectedIcon else barItem.unselectedIcon,
                        contentDescription = barItem.title)
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMainScreen(){
  //  MainScreen()
}