package com.example.weatherapp.ui.theme

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R

object AppFont {
    val Baloo2FontFamily = FontFamily(
        Font(R.font.baloo2_regular, FontWeight.Normal),
        Font(R.font.baloo2_extra_bold, FontWeight.ExtraBold),
        Font(R.font.baloo2_bold, FontWeight.Bold),
        Font(R.font.baloo2_medium, FontWeight.Medium),
        Font(R.font.baloo2_semi_bold, FontWeight.SemiBold)
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.Baloo2FontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.Baloo2FontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.Baloo2FontFamily),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.Baloo2FontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.Baloo2FontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.Baloo2FontFamily),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.Baloo2FontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.Baloo2FontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.Baloo2FontFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.Baloo2FontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.Baloo2FontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.Baloo2FontFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.Baloo2FontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.Baloo2FontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.Baloo2FontFamily)
)