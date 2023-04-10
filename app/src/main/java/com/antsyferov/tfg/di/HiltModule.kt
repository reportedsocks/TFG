package com.antsyferov.tfg.di

import android.app.Application
import android.content.ContentResolver
import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.data.FirebaseDataSource
import com.antsyferov.tfg.use_cases.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltModule {

    @Singleton
    @Binds
    abstract fun publicationsListUseCase(
        publicationsListUseCaseImpl: PublicationsListUseCaseImpl
    ) : PublicationsListUseCase

    @Singleton
    @Binds
    abstract fun articlesUseCase(
        articlesUseCase: ArticlesUseCaseImpl
    ): ArticlesUseCase

    @Singleton
    @Binds
    abstract fun profileUseCase(
        profileUseCase: ProfileUseCaseImpl
    ): ProfileUseCase

    @Singleton
    @Binds
    abstract fun dataSource(
        firebaseDataSource: FirebaseDataSource
    ): DataSource

}