package com.antsyferov.tfg.di

import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.data.FirebaseDataSource
import com.antsyferov.tfg.use_cases.ArticlesUseCase
import com.antsyferov.tfg.use_cases.ArticlesUseCaseImpl
import com.antsyferov.tfg.use_cases.PublicationsListUseCase
import com.antsyferov.tfg.use_cases.PublicationsListUseCaseImpl
import dagger.Binds
import dagger.Module
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
    abstract fun articlesListUseCase(
        articlesListUseCase: ArticlesUseCaseImpl
    ): ArticlesUseCase

    @Singleton
    @Binds
    abstract fun dataSource(
        firebaseDataSource: FirebaseDataSource
    ): DataSource

}