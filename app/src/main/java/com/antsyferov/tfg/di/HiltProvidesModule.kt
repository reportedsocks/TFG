package com.antsyferov.tfg.di

import android.app.Application
import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HiltProvidesModule {

    @Provides
    fun contentResolver(
        application: Application
    ): ContentResolver {
        return application.contentResolver
    }

}