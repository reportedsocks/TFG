package com.tfg.di

import com.tfg.domain.interfaces.DataSource
import com.tfg.data.FirebaseDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataHiltModule {

    @Singleton
    @Binds
    abstract fun dataSource(
        firebaseDataSource: FirebaseDataSource
    ): DataSource

}