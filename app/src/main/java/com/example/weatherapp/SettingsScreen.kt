package com.example.weatherapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.DefaultText
import com.example.weatherapp.ui.theme.AppBackgroundGradient
import com.example.weatherapp.ui.theme.AppFont
import com.example.weatherapp.weatherViewModel.WeatherViewModel

@Composable
fun SettingsScreen(viewModel: WeatherViewModel) {
    val currentRefreshOption = viewModel.refreshTime
    val currentWindUnitOption = viewModel.windSpeedUnit
    val currentTempUnitOption = viewModel.temperatureUnit

    val automaticRefreshOptions =
        listOf("1 min", "5 min", "10 min", "15 min", "30 min", "1 hour", "1 day")
    val windUnitOptions = listOf("km/h", "mph")
    val temperatureOptions = listOf("Celsius", "Fahrenheit")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBackgroundGradient)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopHomeNavBar()

            Spacer(modifier = Modifier.size(40.dp))

            Column(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                SingleSetting(
                    title = "Automatic refresh",
                    list = automaticRefreshOptions,
                    currentOption = currentRefreshOption.intValue
                ) { newIndex ->
                    currentRefreshOption.intValue = newIndex
                    viewModel.saveUserSettings()
                }

                SingleSetting(
                    title = "Unit of wind speed",
                    list = windUnitOptions,
                    currentOption = currentWindUnitOption.intValue
                ) { newIndex ->
                    currentWindUnitOption.intValue = newIndex
                    viewModel.saveUserSettings()
                }
                SingleSetting(
                    title = "Unit of temperature",
                    list = temperatureOptions,
                    currentOption = currentTempUnitOption.intValue
                ) { newIndex ->
                    currentTempUnitOption.intValue = newIndex
                    viewModel.saveUserSettings()
                }
            }
        }
    }
}


@Composable
private fun TopHomeNavBar() {
    return Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_settings2),
            contentDescription = "FullHeart",
            modifier = Modifier
                .size(36.dp)
                .alpha(0.5f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Settings",
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
        )
    }
}

@Composable
fun SingleSetting(
    title: String,
    list: List<String>,
    currentOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    return run {
        DefaultText(string = title, fontSize = 26.sp)
        Spacer(Modifier.size(5.dp))

        val isDropdownExpanded = remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x33000000))
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 5.dp, top = 5.dp, start = 15.dp, end = 5.dp)
                    .clickable { isDropdownExpanded.value = true }
                    .fillMaxWidth(0.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DefaultText(list[currentOption], fontSize = 18.sp)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    tint = Color.White,
                    contentDescription = "Dropdown Icon",
                )
            }
            DropdownMenu(
                modifier = Modifier.background(Color(0xFF1A1E26)),
                expanded = isDropdownExpanded.value,
                onDismissRequest = { isDropdownExpanded.value = false }
            ) {
                list.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { DefaultText(option, fontSize = 18.sp) },
                        onClick = {
                            onOptionSelected(index)
                            isDropdownExpanded.value = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(15.dp))
    }
}
