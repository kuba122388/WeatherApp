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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.ui.DefaultText
import com.example.weatherapp.ui.theme.AppBackgroundGradient
import com.example.weatherapp.ui.theme.AppFont
import com.example.weatherapp.weatherViewModel.WeatherViewModel


@Composable
fun WeatherHomeScreen(viewModel: WeatherViewModel, onSettingsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBackgroundGradient)
            .padding(horizontal = 16.dp)
    ) {
        TopHomeNavBar(onSettingsClick)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            val cityInfo by viewModel.weatherResult.observeAsState()

            cityInfo?.let { CityWeatherMainInfo(it) }

            Spacer(modifier = Modifier.size(30.dp))

            cityInfo?.let { DailyForecast(it) }
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
private fun CityWeatherMainInfo(cityInfo: NetworkResponse<WeatherModel>) {
    when (cityInfo) {
        is NetworkResponse.Loading -> {
            Text(
                text = "Loading...",
                fontFamily = AppFont.Baloo2FontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )
        }

        is NetworkResponse.Success -> {
            val weather = cityInfo.data
            val city = weather.location.name
            val region = weather.location.region
            val temperature = weather.current.temp_c
            val condition = weather.current.condition.text
            val weatherIcon = getWeatherIcon(condition = condition)

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
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy((-8).dp)
                            ) {
                                Spacer(modifier = Modifier.size(20.dp))
                                DefaultText(
                                    city,
                                    fontSize = 32.sp
                                )
                                if (region != "" && region != city) DefaultText(string = region)
                                DefaultText(condition, fontSize = 16.sp)
                                Image(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .padding(vertical = 10.dp),
                                    painter = painterResource(id = weatherIcon),
                                    contentDescription = condition,
                                )
                                DefaultText("${temperature}°C", fontSize = 40.sp)
                                Spacer(modifier = Modifier.size(15.dp))
                                CityWeatherAdditionalInfo(weather)
                            }
                        }
                    }
                }
            }
        }

        is NetworkResponse.Error -> {
            Text(
                text = "Failed to load weather data",
                fontFamily = AppFont.Baloo2FontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun getWeatherIcon(condition: String): Int {
    return when (condition.trim().lowercase()) {
        "sunny" -> R.drawable.weather_sunny
        "cloudy" -> R.drawable.weather_cloudy
        "partly cloudy" -> R.drawable.weather_partly_cloudy
        "mist" -> R.drawable.weather_mist
        "fog" -> R.drawable.weather_mist
        "clear" -> R.drawable.weather_clear_night
        "overcast" -> R.drawable.weather_cloudy_night
        "rain" -> R.drawable.weather_rainy
        "moderate rain" -> R.drawable.weather_rainy
        "light rain shower" -> R.drawable.weather_rainy_sun
        "patchy rain nearby" -> R.drawable.weather_rainy
        else -> R.drawable.weather_sunny
    }
}

@Composable
private fun CityWeatherAdditionalInfo(cityInfo: WeatherModel) {
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
            DefaultText(cityInfo.current.wind_kph + " km/h")
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
            DefaultText(cityInfo.current.humidity + " %")
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
        DefaultText(cityInfo.current.pressure_mb + " hPa")
    }
    Spacer(modifier = Modifier.size(20.dp))
}


@Composable
private fun DailyForecast(cityInfo: NetworkResponse<WeatherModel>) {
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
        when (cityInfo) {
            is NetworkResponse.Loading -> {
                Text(
                    text = "Loading...",
                    fontFamily = AppFont.Baloo2FontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }

            is NetworkResponse.Success -> {
                val forecast = cityInfo.data.forecast
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    forecast.forecastday.forEach { day ->
                        DailyCard(
                            date = day.date,
                            id = getWeatherIcon(condition = day.day.condition.text),
                            contentDescription = day.day.condition.text,
                            temp = day.day.avgtemp_c
                        )
                    }
                }
            }

            is NetworkResponse.Error -> {
                Text(
                    text = "Failed to load weather data",
                    fontFamily = AppFont.Baloo2FontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DailyCard(date: String, id: Int, contentDescription: String, temp: String) {
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
            DefaultText(string = contentDescription, fontSize = 18.sp)
            DefaultText(string = "$temp°C", fontSize = 18.sp)
        }
    }
}
