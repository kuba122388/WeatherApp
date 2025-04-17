package com.example.weatherapp.ui

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.example.weatherapp.ui.theme.AppFont


@Composable
fun DefaultText(
    string: String,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    return Text(
        string,
        modifier,
        fontFamily = AppFont.Baloo2FontFamily,
        color = Color.White,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
    )
}
