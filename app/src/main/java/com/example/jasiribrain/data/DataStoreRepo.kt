package com.example.jasiribrain.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataStoreRepo(
    private val dataStore: DataStore<Preferences>
){

    private val keyIsBluetoothOn = booleanPreferencesKey("bluetooth_on")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val bluetoothIsActive: LiveData<Boolean> = liveData {
        dataStore.data.collect {
            emit(it[keyIsBluetoothOn] ?: false)
        }
    }

    fun setBluetoothIsActive(toggle: Boolean) {
        coroutineScope.launch {
            dataStore.edit { settings ->
                settings[keyIsBluetoothOn] = toggle
            }
        }
    }

}