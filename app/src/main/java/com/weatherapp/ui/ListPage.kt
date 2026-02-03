package com.weatherapp.ui

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.RegisterActivity
import com.weatherapp.model.City
import com.weatherapp.model.getCities
import com.weatherapp.model.MainViewModel
import com.weatherapp.model.Weather
import com.weatherapp.ui.nav.Route
import kotlin.collections.get

@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val cityList = viewModel.cities
    val activity = LocalActivity.current as Activity //Toasts

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(cityList, key = { it.name }) { city ->
            CityItem(city = city, weather = viewModel.weather(city.name), onClose = {
                /*TODO*/
                viewModel.remove(city)
                Toast.makeText(activity,"${city.name} excluída!", Toast.LENGTH_LONG).show()

            }, onClick = {
                /*TODO*/
                Toast.makeText(activity, "${city.name}", Toast.LENGTH_LONG).show()
                viewModel.city = city.name
                viewModel.page = Route.Home

            })
        }
    }

}


@Composable
fun CityItem(
    city: City,
    weather: Weather,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val desc = if (weather == Weather.LOADING) "Carregando Clima..." else weather.desc
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = weather.imgUrl,
            modifier = Modifier.size(40.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = modifier.weight(1f)) {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = city.name,
                    fontSize = 24.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(12.dp))

                val icon =
                    if (city.isMonitored == true)
                        Icons.Filled.Notifications
                    else
                        Icons.Outlined.Notifications

                Icon( imageVector = icon, contentDescription = "Monitorada?",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                modifier = Modifier,
                text = desc,
                fontSize = 16.sp
            )

        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}