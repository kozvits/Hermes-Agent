package com.nousresearch.hermesagent.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nousresearch.hermesagent.data.api.HermesApiService
import com.nousresearch.hermesagent.data.local.HermesDatabase
import com.nousresearch.hermesagent.data.local.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HermesDatabase {
        return HermesDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideHermesApiService(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): HermesApiService {
        val prefs = PreferencesManager(context)
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // default, overridden in VM
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HermesApiService::class.java)
    }
}
