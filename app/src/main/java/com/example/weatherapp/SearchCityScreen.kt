package com.example.weatherapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.api.City
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.ui.DefaultText
import com.example.weatherapp.ui.theme.AppBackgroundGradient
import com.example.weatherapp.ui.theme.AppFont
import com.example.weatherapp.weatherViewModel.WeatherViewModel

@Composable
fun SearchCityScreen(viewModel: WeatherViewModel, onSettingsClick: () -> Unit, onCitySelected: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val weatherResult = viewModel.weatherResult.observeAsState()
    val suggestions by viewModel.citySuggestions.observeAsState(emptyList())

    val favorites = viewModel.favoriteCities.observeAsState(emptySet())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBackgroundGradient)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(brush = AppBackgroundGradient)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }

        ) {
            item {
                TopHomeNavBar(onSettingsClick)
            }
            item {
                CustomSearchView(search = searchQuery, onValueChange = {
                    searchQuery = it
                }, viewModel = viewModel)
            }
            item {
                when (val result = weatherResult.value) {
                    is NetworkResponse.Error -> Text(text = result.message)
                    NetworkResponse.Loading -> CircularProgressIndicator()
                    is NetworkResponse.Success -> Text(text = result.data.toString())
                    null -> {}
                }
            }
            items(suggestions) { city ->
                val isFavorite =
                    favorites.value.any { it == city }
                CityCard(
                    city = city,
                    isFavorite = isFavorite,
                    onClick = {
                        viewModel.selectCity(city)
                        onCitySelected()
                    },
                    onFavoriteToggle = { viewModel.toggleFavorite(city) }
                )
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
        Image(
            painter = painterResource(id = R.drawable.icon_town),
            contentDescription = "CityIcon",
            modifier = Modifier
                .size(36.dp)
                .alpha(0.5f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Discover City",
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
                .clickable {
                    onSettingsClick()
                }
        )
    }
}

@Composable
private fun CustomSearchView(
    search: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    viewModel: WeatherViewModel
) {

    Box(
        modifier = modifier
            .padding(10.dp)
            .clip(CircleShape)
            .background(Color(0X33000000))
    ) {
        TextField(
            value = search,
            onValueChange = {
                onValueChange(it)
                viewModel.searchCities(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 0.dp)
                .defaultMinSize(minHeight = 1.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontFamily = AppFont.Baloo2FontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            ),
            trailingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.icon_search),
                    contentDescription = ""
                )
            },
            placeholder = {
                Text(
                    text = "Search", fontFamily = AppFont.Baloo2FontFamily,
                    fontSize = 22.sp
                )
            },
            singleLine = true,
        )
    }
}

@Composable
fun CityCard(
    city: City,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {

    val context = LocalContext.current

    return Box(
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x66000000))
                .clickable { onClick() }
                .padding(top = 12.dp, bottom = 12.dp, start = 15.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.clickable {
                        onFavoriteToggle()
                        Toast.makeText(
                            context,
                            if (!isFavorite) "City added to favorites" else "City removed from favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    painter = painterResource(id = if (isFavorite) R.drawable.icon_fullheart else R.drawable.icon_emptyheart),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    DefaultText(
                        string = city.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (city.region != "") DefaultText(
                        string = city.region,
                    )
                }
            }
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = R.drawable.icon_arrow),
                contentDescription = ""
            )
        }
    }
}