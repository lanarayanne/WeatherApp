package com.weatherapp.model
import com.google.android.gms.maps.model.LatLng

class City (
    val name : String,
    val weather: String? = null,
    val location: LatLng? = null
){
}