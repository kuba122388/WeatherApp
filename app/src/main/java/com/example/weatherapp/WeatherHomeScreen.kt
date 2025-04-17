package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.DefaultText
import com.example.weatherapp.ui.theme.AppBackgroundGradient
import com.example.weatherapp.ui.theme.AppFont


@Composable
fun WeatherHomeScreen(cityName: String, cityRegion: String, onSettingsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBackgroundGradient)
            .padding(horizontal = 16.dp)
    ) {
        TopHomeNavBar(onSettingsClick)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            CityWeatherMainInfo(cityName)

            Spacer(modifier = Modifier.size(30.dp))

            DailyForecast()
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
        Image(
            painter = painterResource(id = R.drawable.icon_refresh),
            contentDescription = "Refresh",
            modifier = Modifier
                .size(36.dp)
                .alpha(0.5f)
        )
        Text(
            text = "Last updated: 30s ago",
            style = TextStyle(
                color = Color.White,
                fontFamily = AppFont.Baloo2FontFamily,
                fontSize = 20.sp
            )
        )
        Image(
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = "Setting icon",
            modifier = Modifier
                .size(36.dp)
                .alpha(0.5f)
                .clickable {
                    onSettingsClick()
                }
        )
    }
}

@Composable
private fun CityWeatherMainInfo(city: String) {
    Column {
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = Color(0x33000000))
                        .padding(vertical = 15.dp, horizontal = 10.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(all = 5.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((-8).dp)
                    ) {
                        Spacer(modifier = Modifier.size(20.dp))
                        Text(
                            city,
                            fontFamily = AppFont.Baloo2FontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 32.sp
                        )
                        DefaultText(
                            "Partly Cloudy",
                            fontSize = 16.sp
                        )
                        Image(
                            modifier = Modifier
                                .size(150.dp)
                                .padding(vertical = 10.dp),
                            painter = painterResource(id = R.drawable.weather_partly_cloudy),
                            contentDescription = "Partly Cloudy",
                        )
                        DefaultText(
                            "22°C",
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                        CityWeatherAdditionalInfo()
                    }
                }
            }
        }
    }
}

@Composable
private fun CityWeatherAdditionalInfo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.icon_wind),
                contentDescription = "Wind",
            )
            Spacer(modifier = Modifier.size(10.dp))
            DefaultText("5 km/h")
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.icon_humidity),
                contentDescription = "Wind",
            )
            Spacer(modifier = Modifier.size(5.dp))
            DefaultText("2 %")
        }
    }
    Spacer(modifier = Modifier.size(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.icon_pressure),
            contentDescription = "Air Pressure"
        )
        Spacer(modifier = Modifier.size(5.dp))
        DefaultText("1013 hPa")
    }
    Spacer(modifier = Modifier.size(20.dp))
}

@Composable
private fun DailyForecast() {
    return Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = R.drawable.icon_24h),
                contentDescription = "Clock"
            )
            Spacer(modifier = Modifier.size(15.dp))
            DefaultText(
                string = "Daily Forecast",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DailyCard("Today", R.drawable.weather_partly_cloudy, "Partly Cloudy", 22)
            DailyCard("15 Apr", R.drawable.weather_sunny, "Sunny", 32)
            DailyCard("16 Apr", R.drawable.weather_windy, "Windy", 15)
            DailyCard("17 Apr", R.drawable.weather_snowy, "Snowy", -5)
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DailyCard(date: String, id: Int, contentDescription: String, temp: Int) {
    return Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0x33000000))
            .padding(vertical = 10.dp, horizontal = 20.dp),
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DefaultText(string = date, fontSize = 20.sp)
            Image(
                painter = painterResource(
                    id = id
                ), contentDescription = contentDescription,
                modifier = Modifier.size(45.dp)
            )
            DefaultText(string = "$temp°C", fontSize = 18.sp)
        }
    }
}
