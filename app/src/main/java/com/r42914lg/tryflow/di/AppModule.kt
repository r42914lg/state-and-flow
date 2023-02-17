package com.r42914lg.tryflow.di

import com.r42914lg.tryflow.data.CategoryLocalDataSource
import com.r42914lg.tryflow.data.CategoryRemoteDataSource
import com.r42914lg.tryflow.data.CategoryRepositoryImpl
import com.r42914lg.tryflow.domain.CategoryInteractorImpl
import com.r42914lg.tryflow.domain.CategoryRepository
import com.r42914lg.tryflow.domain.StatsInteractorImpl
import com.r42914lg.tryflow.presentation.CategoryInteractor
import com.r42914lg.tryflow.presentation.StatsInteractor

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesOkhttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jservice.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)

        return retrofit.build()
    }

    @Singleton
    @Provides
    fun provideRepositoryImpl(
        localDataSource: CategoryLocalDataSource,
        remoteDataSource: CategoryRemoteDataSource
    ) = CategoryRepositoryImpl(localDataSource, remoteDataSource)

    @Singleton
    @Provides
    fun provideRepository(impl: CategoryRepositoryImpl): CategoryRepository = impl

    @Singleton
    @Provides
    fun providePhoneService(retrofit: Retrofit) : CategoryRemoteDataSource.CategoryService
        = retrofit.create(CategoryRemoteDataSource.CategoryService::class.java)

    @Provides
    @Singleton
    fun provideUserDataSource(categoryService: CategoryRemoteDataSource.CategoryService)
        = CategoryRemoteDataSource(categoryService)

    @Singleton
    @Provides
    fun provideCatInteractorImpl(repository: CategoryRepository) = CategoryInteractorImpl(repository)

    @Singleton
    @Provides
    fun provideStatsInteractorImpl(repository: CategoryRepository) = StatsInteractorImpl(repository)

    @Singleton
    @Provides
    fun provideCatInteractor(impl: CategoryInteractorImpl): CategoryInteractor = impl

    @Singleton
    @Provides
    fun provideStatsInteractor(impl: StatsInteractorImpl): StatsInteractor = impl

}