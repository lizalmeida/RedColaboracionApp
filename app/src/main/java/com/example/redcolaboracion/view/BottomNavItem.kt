package com.example.redcolaboracion.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    var route: String,
    var unselectedIcon: ImageVector,
    var selectedIcon: ImageVector,
    var title: String
) {
    data object Home: BottomNavItem(
        route = "home",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        title = "Inicio"
    )
    data object MyRequests: BottomNavItem(
        route = "myrequests",
        unselectedIcon = Icons.Outlined.ThumbUp,
        selectedIcon = Icons.Filled.ThumbUp,
        title = "Mis Solicitudes"
    )

    data object Profile: BottomNavItem(
        route = "profile",
        unselectedIcon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle,
        title = "Perfil"
    )
}