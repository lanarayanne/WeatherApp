package com.weatherapp.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.PixelCopy.request
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherService(private val context: Context) {
    private var weatherAPI: WeatherServiceAPI
    private val imageLoader = ImageLoader.Builder(context).allowHardware(false).build()

    init {
        val retrofitAPI = Retrofit.Builder().baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }

//    fun getName(lat: Double, lng: Double, onResponse: (String?) -> Unit) {
//        search("$lat,$lng") { loc -> onResponse(loc?.name) }
//    }

    suspend fun getName(lat: Double, lng: Double):String? = withContext(Dispatchers.IO){
        search("$lat,$lng")?.name
    }

//    fun getLocation(name: String, onResponse: (lat: Double?, long: Double?) -> Unit) {
//        search(name) { loc -> onResponse(loc?.lat, loc?.lon) }
//    }

    suspend fun getLocation(name: String): LatLng? = withContext(Dispatchers.IO) {
        val location = search(name)
        location?.lat?.let { lat ->
            location.lon?.let { lon ->
                LatLng(lat, lon)
            }
        }
    }

//    private fun search(query: String, onResponse: (APILocation?) -> Unit) {
//        val call: Call<List<APILocation>?> = weatherAPI.search(query)
//        call.enqueue(object : Callback<List<APILocation>?> {
//            override fun onResponse(
//                call: Call<List<APILocation>?>,
//                response: Response<List<APILocation>?>
//            ) {
//                onResponse(response.body()?.let { if (it.isNotEmpty()) it[0] else null })
//            }
//
//            override fun onFailure(call: Call<List<APILocation>?>, t: Throwable) {
//                Log.w("WeatherApp WARNING", "" + t.message)
//                onResponse(null)
//            }
//        })
//    }

    private fun search(query: String) : APILocation? {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        val apiLoc = call.execute().body()
        return if (!apiLoc.isNullOrEmpty()) apiLoc[0] else null
    }

//    fun getWeather(name: String, onResponse: (APICurrentWeather?) -> Unit) {
//        val call: Call<APICurrentWeather?> = weatherAPI.weather(name)
//        enqueue(call) { onResponse.invoke(it) }
//    }

    suspend fun getWeather(name: String): APICurrentWeather? =
        withContext(Dispatchers.IO) {
            val call: Call<APICurrentWeather?> = weatherAPI.weather(name)
            call.execute().body()
        }

//    fun getForecast(name: String, onResponse: (APIWeatherForecast?) -> Unit) {
//        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
//        enqueue(call) { onResponse.invoke(it) }
//    }

    suspend fun getForecast(name:String) : APIWeatherForecast? = withContext(Dispatchers.IO){
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
        call.execute().body()
    }

//    fun getBitmap(imgUrl: String, onResponse: (Bitmap?) -> Unit) {
//        val request = ImageRequest.Builder(context)
//            .data(imgUrl).allowHardware(false).target(
//                onSuccess = { drawable ->
//                    val bitmap = (drawable as BitmapDrawable).bitmap
//                    onResponse(bitmap)
//                },
//                //OnError = { // handle failure }
//            )
//            .build()
//        imageLoader.enqueue(request)
//    }

    suspend fun getBitmap(imgUrl: String) : Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest.Builder(context).data(imgUrl)
            .allowHardware(false).build()
        val response = imageLoader.execute(request)
        response.drawable?.toBitmap()
    }

    private fun <T> enqueue(call: Call<T?>, onResponse: ((T?) -> Unit)? = null) {
        call.enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                val obj: T? = response.body()
                onResponse?.invoke(obj)
            }

            override fun onFailure(call: Call<T?>, t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
            }
        })
    }




}