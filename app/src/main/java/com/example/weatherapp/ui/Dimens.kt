package com.example.weatherapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isTablet(): Boolean {
    val config = LocalConfiguration.current
    return config.screenWidthDp >= 600
}