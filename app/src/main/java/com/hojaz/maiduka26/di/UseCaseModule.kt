package com.hojaz.maiduka26.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt module for UseCase dependencies.
 * UseCases are automatically provided via @Inject constructor.
 * This module can be extended for complex UseCase configurations.
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    // UseCases with @Inject constructor are automatically provided by Hilt
    // Add custom providers here if needed for complex configurations
}
