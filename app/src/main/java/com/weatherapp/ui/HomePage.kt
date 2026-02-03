package com.weatherapp.ui

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.copy
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.Forecast
import com.weatherapp.model.MainViewModel
import okhttp3.internal.threadName
import java.text.DecimalFormat

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column {
        if(viewModel.city == null) {
            Column (modifier = Modifier.fillMaxSize()
                .background(Color.Blue).wrapContentSize(Alignment.Center)
            ) {
                Text(text = "Selecione uma cidade!",
                    fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center, fontSize = 28.sp)
            }
        } else {
            Row {
                AsyncImage(
                    model = viewModel.weather(viewModel.city!!).imgUrl,
                    modifier = Modifier.size(100.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )
                Column {
                    Spacer(modifier = Modifier.size(12.dp))
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = viewModel.city ?: "Selecione uma cidade...",
                            fontSize = 28.sp,
                            modifier = Modifier.weight(1f))

                        Spacer(modifier = Modifier.width(8.dp))

                        val city = viewModel.cityMap[viewModel.city]

                        val icon =
                            if (city?.isMonitored == true)
                                Icons.Filled.Notifications
                            else
                                Icons.Outlined.Notifications

                        Icon(
                            imageVector = icon,
                            contentDescription = "Monitorada?",
                            modifier = Modifier.size(32.dp).clickable {
                                viewModel.update(city = city!!.copy(isMonitored = !city.isMonitored))
                            }
                        )

                    }
                    viewModel.city?.let { name ->
                        val weather = viewModel.weather(name)
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(text = weather?.desc ?: "...",
                            fontSize = 20.sp)
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(text = "Temp: " + weather?.temp + "°C",
                            fontSize = 20.sp)

                    }

                }
            }

            viewModel.forecast(viewModel.city!!)?.let { forecasts ->
                LazyColumn {
                    items(items = forecasts) { forecast ->
                        ForecastItem(forecast, onClick = { })
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem (
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier: Modifier = Modifier
) {

    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)

    Row (
        modifier = modifier.fillMaxWidth().padding(12.dp)
            .clickable( onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = forecast.imgUrl,
            modifier = Modifier.size(45.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(modifier = Modifier, text = forecast.weather, fontSize = 24.sp)
            Text(modifier = Modifier, text = forecast.date, fontSize = 20.sp)
            Row {
//                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Min: $tempMin℃", fontSize = 16.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Max: $tempMax℃", fontSize = 16.sp)
            }
        }
    }


}