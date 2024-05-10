package com.example.redcolaboracion.navigation

sealed class AppScreens(val route: String) {
    data object SplashScreen: AppScreens("splash_screen")
    data object EventScreen: AppScreens("event_screen")
    data object ProfileScreen: AppScreens("profile_screeen")
    data object GivedHelpScreen: AppScreens("gived_help_screen")
    data object RequestedHelpScreen: AppScreens("requested_help_screen")
    data object CameraPreview: AppScreens("camera_preview")


}