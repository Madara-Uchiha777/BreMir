package com.example.bremir.di

import com.example.bremir.firestoreDB.DatabaseRepositoryImpl
import com.example.bremir.firestoreDB.IDatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {

    @Provides
    @ViewModelScoped
    fun provideDatabaseRepository() = DatabaseRepositoryImpl() as IDatabaseRepository
}