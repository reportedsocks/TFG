package com.tfg.di

import com.tfg.data.ArticleDataSourceImpl
import com.tfg.data.PublicationsDataSourceImpl
import com.tfg.data.ReviewsDataSourceImpl
import com.tfg.data.UserDataSourceImpl
import com.tfg.domain.interfaces.ArticleDataSource
import com.tfg.domain.interfaces.PublicationsDataSource
import com.tfg.domain.interfaces.ReviewsDataSource
import com.tfg.domain.interfaces.UserDataSource
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
    abstract fun userDataSource(
        userDataSource: UserDataSourceImpl
    ):UserDataSource

    @Singleton
    @Binds
    abstract fun publicationsDataSource(
       publicationsDataSourceImpl: PublicationsDataSourceImpl
    ): PublicationsDataSource

    @Singleton
    @Binds
    abstract fun articleDataSource(
        articleDataSourceImpl: ArticleDataSourceImpl
    ): ArticleDataSource

    @Singleton
    @Binds
    abstract fun reviewsDataSource(
        reviewsDataSourceImpl: ReviewsDataSourceImpl
    ): ReviewsDataSource

}