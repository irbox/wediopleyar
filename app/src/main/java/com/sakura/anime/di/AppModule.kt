package com.sakura.anime.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.sakura.anime.application.AnimeApplication
import com.sakura.anime.data.local.database.AnimeDatabase
import com.sakura.anime.data.repository.RoomRepositoryImpl
import com.sakura.anime.domain.repository.RoomRepository
import com.sakura.anime.util.ANIME_DATABASE
import com.sakura.anime.util.createDefaultHttpClient
import com.sakura.anime.util.preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DandanplayHttpClient

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesAnimeApplication(
        @ApplicationContext app: Context
    ): AnimeApplication {
        return app as AnimeApplication
    }

    @Singleton
    @Provides
    fun providesContext(
        @ApplicationContext app: Context
    ): Context {
        return app
    }

    @Singleton
    @Provides  // The Application binding is available without qualifiers.
    fun providesDatabase(application: Application): AnimeDatabase {
        return Room.databaseBuilder(
            application,
            AnimeDatabase::class.java,
            ANIME_DATABASE,
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesRoomRepository(database: AnimeDatabase): RoomRepository {
        return RoomRepositoryImpl(database)
    }

    @Singleton
    @Provides
    fun providesPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.preferences
    }

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return createDefaultHttpClient()
    }

    // 为 DandanplayDanmakuProvider 提供特殊配置的 HttpClient 实例
    @DandanplayHttpClient
    @Singleton
    @Provides
    fun provideDandanplayHttpClient(): HttpClient {
        return createDefaultHttpClient()
    }
}