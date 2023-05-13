package com.tfg.di

import com.tfg.domain.use_cases.ArticlesUseCase
import com.tfg.domain.use_cases.ArticlesUseCaseImpl
import com.tfg.domain.use_cases.ProfileUseCase
import com.tfg.domain.use_cases.ProfileUseCaseImpl
import com.tfg.domain.use_cases.PublicationsListUseCase
import com.tfg.domain.use_cases.PublicationsListUseCaseImpl
import com.tfg.domain.use_cases.ReviewsUseCase
import com.tfg.domain.use_cases.ReviewsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainHiltModule {

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
    abstract fun reviewsUseCase(
        reviewsUseCase: ReviewsUseCaseImpl
    ): ReviewsUseCase

}