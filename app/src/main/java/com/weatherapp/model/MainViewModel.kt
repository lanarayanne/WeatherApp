package com.weatherapp.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weatherapp.api.WeatherService
import com.weatherapp.api.toForecast
import com.weatherapp.api.toWeather
import com.weatherapp.db.fb.FBCity
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.db.fb.FBUser
import com.weatherapp.db.fb.toFBCity
import com.weatherapp.monitor.ForecastMonitor
import com.weatherapp.repo.Repository
import com.weatherapp.ui.nav.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel (private val repo: Repository,
    private val service: WeatherService,
    private val monitor: ForecastMonitor
): ViewModel() {

    private var _city = mutableStateOf<String?>(null)
    var city: String?
        get() = _city.value
        set(tmp) { _city.value = tmp }

    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

//    private val _cities = mutableStateMapOf<String, City>()
//    val cities: List<City>
//        get() = _cities.values.toList().sortedBy { it.name }

    private val _cities : Flow<Map<String, City>> = repo.cities.map {
            cityList -> cityList.associateBy { it.name }
    }

    val cities = _cities.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

//    private val _weather = mutableStateMapOf<String, Weather>()

    private val _weather = MutableStateFlow<Map<String, Weather>>(emptyMap())
    val weather = _weather.asSharedFlow()

//    private val _forecast = mutableStateMapOf<String, List<Forecast>?>()

    private val _forecast = MutableStateFlow<Map<String, List<Forecast>?>>(emptyMap())
    val forecast = _forecast.asSharedFlow()

//    private val _user = mutableStateOf<User?> (null)
//    val user : User?
//        get() = _user.value


    val user = repo.user.stateIn(viewModelScope, SharingStarted.Lazily, null)

//    fun remove(city: City) {
//        db.remove(city)
//    }

    fun remove(city: City) {
        repo.remove(city)
        monitor.cancelCity(city)
    }

//    fun update(city: City) {
//        db.update(city)
//    }

    fun update(city: City) {
        repo.update(city)
        monitor.updateCity(city)
    }

//    fun addCity(name: String) {
//        service.getLocation(name) { lat, lng ->
//            if (lat != null && lng != null) {
//                db.add(City(name=name, location=LatLng(lat, lng)))
//            }
//        }
//    }
//    fun addCity(location: LatLng) {
//        service.getName(location.latitude, location.longitude) { name ->
//            if (name != null) {
//                db.add(City(name = name, location = location))
//            }
//        }
//    }
//
    fun addCity(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val location = service.getLocation(name)
        repo.add(City( name = name, location = location))
    }
    fun addCity(location: LatLng) = viewModelScope.launch(Dispatchers.IO) {
        val name = service.getName(location.latitude, location.longitude)
        repo.add(City(name = name?:"Unknown", location = location))
    }

//    fun weather (name: String) = _weather.getOrPut(name) {
//        loadWeather(name)
//        Weather.LOADING // return
//    }
//
//    private fun loadWeather(name: String) {
//        service.getWeather(name) { apiWeather ->
//            apiWeather?.let {
//                _weather[name] = apiWeather.toWeather()
//                loadBitmap(name)
//            }
//        }
//    }

    fun loadWeather(name: String) {
        if (_weather.value[name] != null) return
        viewModelScope.launch(Dispatchers.Main) {
            _weather.update { current -> current + (name to Weather.LOADING) }
            runCatching {
                service.getWeather(name)?.toWeather()
            }.onSuccess { weather ->
                _weather.update { curr -> curr + (name to (weather?:Weather.ERROR)) }
            }.onFailure { e ->
                _weather.update { curr -> curr + (name to Weather.ERROR) }
            }
        }
    }

//    fun forecast (name: String) = _forecast.getOrPut(name) {
//        loadForecast(name)
//        emptyList() // return
//    }
//
//    private fun loadForecast(name: String) {
//        service.getForecast(name) { apiForecast ->
//            apiForecast?.let {
//                _forecast[name] = apiForecast.toForecast()
//            }
//        }
//    }

    fun loadForecast(name: String) {
        if (_forecast.value[name] != null) return
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                service.getForecast(name)?.toForecast()
            }.onSuccess { forecast ->
                _forecast.update { curr -> curr + (name to forecast) }
            }
        }
    }

//    fun loadBitmap(name: String) {
//        _weather[name]?.let {weather ->
//            service.getBitmap(weather.imgUrl) {bitmap ->
//                _weather[name] = weather.copy(bitmap=bitmap)
//            }
//        }
//    }

    fun loadBitmap(name: String) {
        val weather = _weather.value[name]
        if (weather == null || weather == Weather.LOADING || weather == Weather.ERROR ||
            weather.bitmap != null
        ) return
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                service.getBitmap(weather.imgUrl)
            }.onSuccess { bitmap ->
                _weather.update { curr ->
                    curr + (name to (weather.copy(bitmap = bitmap))) }
            }.onFailure { /* do nothing */ }
        }
    }

//    val cityMap: Map<String, City> get() = _cities.toMap()
//
//    init {
//        db.setListener(this)
//    }

//    fun add(name: String, location : LatLng? = null) {
//        db.add(City(name = name, location = location))
//    }

//    override fun onUserLoaded(user: User) {
//        _user.value = user
//    }
//
//    override fun onUserSignOut() {
//        monitor.cancelAll()
//    }
//
//    override fun onCityAdded(city: City) {
//        _cities[city.name!!] = city
//        monitor.updateCity(city)
//    }
//
//    override fun onCityUpdated(city: City) {
//        _cities.remove(city.name)
//        _cities[city.name!!] = city
//        monitor.updateCity(city)
//    }
//
//    override fun onCityRemoved(city: City) {
//        _cities.remove(city.name)
//        monitor.cancelCity(city)
//    }

}

class MainViewModelFactory(private val db : Repository,
                           val service: WeatherService,
                           val monitor: ForecastMonitor) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service, monitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun getCities() = List(20) { i ->
    City(name = "Cidade $i")


}

