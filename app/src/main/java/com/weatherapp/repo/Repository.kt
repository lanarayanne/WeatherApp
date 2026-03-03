package com.weatherapp.repo

import com.weatherapp.db.fb.FBCity
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.db.fb.FBUser
import com.weatherapp.db.fb.toFBCity
import com.weatherapp.db.local.LocalDatabase
import com.weatherapp.db.local.toCity
import com.weatherapp.db.local.toLocalCity
import com.weatherapp.model.City
import com.weatherapp.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class Repository (private val fbDB: FBDatabase,
                  private val localDB : LocalDatabase
) {

//    interface Listener {
//        fun onUserLoaded(user: User)
//        fun onUserSignOut()
//        fun onCityAdded(city: City)
//        fun onCityUpdated(city: City)
//        fun onCityRemoved(city: City)
//    }

//    private var listener : Listener? = null

//    fun setListener(listener: Listener? = null) {
//        this.listener = listener
//    }

    private var ioScope : CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var cityMap = emptyMap<String, City>()

    val cities = localDB.getCities().map {
            list -> list.map { city -> city.toCity() }
    }

    val user = fbDB.user.map { it.toUser() }


    init {
        ioScope.launch {
            fbDB.cities.collect { fbCityList ->
                val cityList = fbCityList.map { it.toCity() }
                val nameList = cityList.map { it.name }
                val deletedCities = cityMap.filter { it.key !in nameList}
                val updatedCities = cityList.filter {it.name in cityMap.keys}
                val newCities = cityList.filter { it.name !in cityMap.keys}
                newCities.forEach {localDB.insert(it.toLocalCity())}
                updatedCities.forEach {localDB.update(it.toLocalCity())}
                deletedCities.forEach {localDB.delete(it.value.toLocalCity())}
                cityMap = cityList.associateBy { it.name }
            }
        }
    }

    fun add(city: City) = fbDB.add(city.toFBCity())

    fun remove(city: City) = fbDB.remove(city.toFBCity())

    fun update(city: City) = fbDB.update(city.toFBCity())

//    override fun onUserLoaded(user: FBUser)= listener?.onUserLoaded(user.toUser())?:Unit
//
//    override fun onUserSignOut() = listener?.onUserSignOut()?:Unit
//
//    override fun onCityAdded(city: FBCity) {
//        localDB.insert(city.toCity().toLocalCity())
//    }
//
//    override fun onCityUpdated(city: FBCity) {
//        localDB.update(city.toCity().toLocalCity())
//    }

//    override fun onCityRemoved(city: FBCity) {
//        localDB.delete(city.toCity().toLocalCity())
//    }
}