package com.example.jasiriheart.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.example.jasiriheart.data.DataStoreRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {


    /**
     * All provisions for data store classes
     * Provision DataStore & DataStoreRepo
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.createDataStore("settings")
    }

    @Provides
    @Singleton
    fun provideDataStoreRepo(dataStore: DataStore<Preferences>): DataStoreRepo {
        return DataStoreRepo(dataStore)
    }
}