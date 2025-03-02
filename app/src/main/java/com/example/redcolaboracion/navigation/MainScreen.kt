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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redcolaboracion.model.UserSession
import com.example.redcolaboracion.view.CameraPreview
import com.example.redcolaboracion.view.EfectiveHelpScreen
import com.example.redcolaboracion.view.EventScreen
import com.example.redcolaboracion.view.GivedHelpScreen
import com.example.redcolaboracion.view.GivedHelpScreenStep2
import com.example.redcolaboracion.view.HistoryScreen
import com.example.redcolaboracion.view.LoginScreen
import com.example.redcolaboracion.view.ProfileScreen
import com.example.redcolaboracion.view.RequestedHelpScreen
import com.example.redcolaboracion.view.UserInfoScreen
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel
import com.example.redcolaboracion.viewmodel.RequestedHelpListViewModel
import com.example.redcolaboracion.viewmodel.RequestedHelpViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val isAuthenticated = remember { mutableStateOf(UserSession.userId != null) }
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
                    println("Route: ${barItem.route}, needAuth: $needAuth, isAuthenticated: ${isAuthenticated.value}")

                        navController.navigate(barItem.route)
                        pendingRoute.value = barItem.route
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
    val requestedHelpId = "requestedHelpId"

    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavItem.RequestedHelp.route) { RequestedHelpScreenL(navController) }
        composable(BottomNavItem.GivedHelp.route) { GivedHelpScreenL(navController) }

        composable("${BottomNavItem.GivedHelp.route}/{$requestedHelpId}") {backStackEntry ->
            GivedHelpScreenStep2(
                requestedHelpId = backStackEntry.arguments?.getString(requestedHelpId) ?: "missing requestHelpId",
                navController
            )
        }

        composable(BottomNavItem.History.route) { HistoryScreenL(navController) }
        composable("${BottomNavItem.History.route}/{section}/{$requestedHelpId}") {backStackEntry ->
            val section = backStackEntry.arguments?.getString("section")

            when (section) {
                "EfectiveHelp" -> {
                    EfectiveHelpScreen(
                        requestedHelpId = backStackEntry.arguments?.getString(requestedHelpId) ?: "missing requestHelpId",
                        navController
                    )
                }
                "UserInfo" -> {
                    UserInfoScreen(
                        userId = backStackEntry.arguments?.getString(requestedHelpId) ?: "missing requestHelpId",
                        navController
                    )
                }
                else -> {
                    Text("Sección no reconocida")
                }
            }
        }
        composable(BottomNavItem.Profile.route) { ProfileScreenL(navController) }
        composable("camera") { CameraPreview(navController) }
        composable("login") { LoginScreenL(pendingRoute, navController)}

        }
}

@Composable
fun HomeScreen(navController: NavController) {
    val eventViewModel: EventViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        EventScreen(eventViewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequestedHelpScreenL(navController: NavController) {
    val requestedHelpViewModel: RequestedHelpViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        println("Usuario actual:" + UserSession.userId)
        if (UserSession.userId == null) {
            navController.navigate("login")
        } else {
            RequestedHelpScreen(requestedHelpViewModel, navController)
        }
    }
}

@Composable
fun GivedHelpScreenL(navController: NavController) {
    val requestedHelpListViewModel: RequestedHelpListViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (UserSession.userId == null) {
            navController.navigate("login")
        } else {
            GivedHelpScreen(requestedHelpListViewModel, navController)
        }
    }
}

@Composable
fun HistoryScreenL(navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (UserSession.userId == null) {
            navController.navigate("login")
        } else {
            HistoryScreen(navController)
        }
    }
}

@Composable
fun ProfileScreenL(navController: NavHostController) {
    val profileViewModel: ProfileViewModel = viewModel()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ProfileScreen(profileViewModel, navController)
    }
}

@Composable
fun LoginScreenL(pendingRoute: MutableState<String?>, navController: NavHostController){
    var userId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    LoginScreen(
        navController,
        onLoginSuccess = {uid ->
            userId = uid.toString()
            pendingRoute.value?.let { route ->
                navController.navigate(route) // Navega a la ruta pendiente
                pendingRoute.value = null // Resetea la ruta pendiente
            }
            //navController.popBackStack("login", inclusive = true)
        },
        onLoginFailed = {error ->
            errorMessage = error.toString()
            println("Error en el usuario o contraseña.")
            Toast.makeText(
                context,
                "Error en el usuario o contraseña. Vuelva a intentarlo.",
                Toast.LENGTH_SHORT
            ).show()
        }
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMain() {
    MainScreen()
}
