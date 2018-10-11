package net.attilaszabo.peopledemo.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.attilaszabo.peopledemo.data.people.IPeopleDatabaseSource
import net.attilaszabo.peopledemo.data.people.IPeopleNetworkSource
import net.attilaszabo.peopledemo.data.sources.local.database.CacheDatabase
import net.attilaszabo.peopledemo.data.sources.local.database.CacheDatabaseController
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiClient
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiController
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiService
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class DataModule {

    @Binds
    abstract fun bindIPeopleDatabaseSource(
        cacheDatabaseController: CacheDatabaseController
    ): IPeopleDatabaseSource

    @Binds
    abstract fun bindIPeopleNetworkSource(
        uiNamesApiController: UINamesApiController
    ): IPeopleNetworkSource

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        fun provideCacheDatabase(context: Context): CacheDatabase =
            CacheDatabase.getDatabase(context)

        @Provides
        @Singleton
        @JvmStatic
        fun provideUINamesApiService(): UINamesApiService {
            return UINamesApiClient.service
        }
    }
}
