package com.weatherapp.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MainViewModel : ViewModel() {

    private val _user = mutableStateOf<User?> (null)
    val user : User? get() = _user.value

    private val _cities = getCities().toMutableStateList()
    val cities
        get() = _cities.toList()

    fun remove(city: City) {
        _cities.remove(city)
    }

    fun add(name: String, location: LatLng? = null) {
        _cities.add(City(name = name, location = location))
    }
}

fun getCities() = List(20) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}
