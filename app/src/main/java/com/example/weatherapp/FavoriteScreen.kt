package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.isTablet
import com.example.weatherapp.ui.theme.AppBackgroundGradient
import com.example.weatherapp.ui.theme.AppFont
import com.example.weatherapp.weatherViewModel.WeatherViewModel

@Composable
fun FavoriteScreen(viewModel: WeatherViewModel, onSettingsClick: () -> Unit, onCitySelected: () -> Unit) {
    val favoriteCities by viewModel.favoriteCities.observeAsState(emptyList())
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBackgroundGradient)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopHomeNavBar(onSettingsClick)

            Spacer(modifier = Modifier.size(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                if (favoriteCities.isEmpty()) {
                    Text(
                        "Search for a city and tap heart to save it.",
                        fontSize = 16.sp,
                        color = Color(0xf0FFFFFF),
                        modifier = Modifier.fillMaxWidth(0.5f),
                        textAlign = TextAlign.Center
                    )
                }
                favoriteCities.forEach { city ->
                    CityCard(
                        city,
                        isFavorite = true,
                        onClick = {
                            viewModel.selectCity(city, context)
                            onCitySelected()
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(city) })
                }
                Spacer(Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun TopHomeNavBar(onSettingsClick: () -> Unit) {
    return Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(!isTablet())Image(
            painter = painterResource(id = R.drawable.icon_fullheart),
            contentDescription = "FullHeart",
            modifier = Modifier
                .size(36.dp)
                .alpha(0.5f)
        )
        else Spacer(modifier = Modifier.size(36.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Favorite Places",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = AppFont.Baloo2FontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            HorizontalDivider(Modifier.size(80.dp, 0.dp), thickness = 2.dp)
        }
        Image(
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = "Setting icon",
            modifier = Modifier
                .size(36.dp)
                .alpha(0.5f)
                .clickable { onSettingsClick() }
        )
    }
}