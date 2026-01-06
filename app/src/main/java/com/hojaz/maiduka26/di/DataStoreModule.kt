package com.hojaz.maiduka26.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for DataStore dependencies.
 * PreferencesManager is already provided in AppModule with @Singleton.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    // PreferencesManager is provided in AppModule
    // This module can be extended for additional DataStore configurations
}
