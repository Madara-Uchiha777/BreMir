package com.example.bremir.di

import com.example.bremir.auth.AuthRepositoryImpl
import com.example.bremir.auth.IAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    @ViewModelScoped
    fun provideAuthRepository() = AuthRepositoryImpl() as IAuthRepository
}