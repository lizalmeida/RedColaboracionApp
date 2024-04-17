package com.example.redcolaboracion.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
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
        title = "Home"
    )
    data object Favourites: BottomNavItem(
        route = "favourites",
        unselectedIcon = Icons.Outlined.Star,
        selectedIcon = Icons.Filled.Star,
        title = "Favourites"
    )

    data object Profile: BottomNavItem(
        route = "profile",
        unselectedIcon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle,
        title = "Profile"
    )
}