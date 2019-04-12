package net.attilaszabo.peopledemo.data.sources.cachedb

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.attilaszabo.peopledemo.data.people.IPeopleDatabaseSource
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class CacheDatabaseDataSourceModule {

    @Binds
    abstract fun bindIPeopleDatabaseSource(
        cacheDatabaseController: CacheDatabaseController
    ): IPeopleDatabaseSource

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        fun provideCacheDatabase(context: Context): CacheDatabase =
            CacheDatabase.getDatabase(context)
    }
}
