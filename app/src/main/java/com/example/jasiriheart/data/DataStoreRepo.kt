package com.example.jasiriheart.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DataStoreRepo(
    private val dataStore: DataStore<Preferences>
){

    private val keyIsBluetoothConnected = booleanPreferencesKey("bluetooth_on")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val bluetoothActive: LiveData<Boolean> = liveData {
        dataStore.data.collect {
            emit(it[keyIsBluetoothConnected] ?: false)
        }
    }

    fun setBluetoothIsConnected(toggle: Boolean) {
        coroutineScope.launch {
            dataStore.edit { settings ->
                settings[keyIsBluetoothConnected] = toggle
            }
        }
    }

}