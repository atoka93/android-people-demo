package net.attilaszabo.peopledemo.data.sources.uinames

import dagger.Binds
import dagger.Module
import dagger.Provides
import net.attilaszabo.peopledemo.data.people.IPeopleNetworkSource
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class UiNamesDataSourceModule {

    @Binds
    abstract fun bindIPeopleNetworkSource(
        uiNamesApiController: UINamesApiController
    ): IPeopleNetworkSource

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        fun provideUINamesApiService(): UINamesApiService {
            return UINamesApiClient.service
        }
    }
}
