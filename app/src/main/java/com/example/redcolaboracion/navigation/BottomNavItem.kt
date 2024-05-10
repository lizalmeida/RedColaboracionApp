package com.example.redcolaboracion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
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
    data object RequestedHelp: BottomNavItem(
        route = "requested_help",
        unselectedIcon = Icons.Outlined.ThumbUp,
        selectedIcon = Icons.Filled.ThumbUp,
        title = "PidoAyuda"
    )
    data object GivedHelp: BottomNavItem(
        route = "gived_help",
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite,
        title = "DoyAyuda"
    )
    data object History: BottomNavItem(
        route = "history",
        unselectedIcon = Icons.Outlined.List,
        selectedIcon = Icons.Filled.List,
        title = "Historial"
    )

    data object Profile: BottomNavItem(
        route = "profile",
        unselectedIcon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle,
        title = "Perfil"
    )
}