package net.attilaszabo.peopledemo.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import net.attilaszabo.peopledemo.PeopleDemoApplication
import javax.inject.Singleton

@Suppress("unused")
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideApplication(application: Application): PeopleDemoApplication =
        application as PeopleDemoApplication

    @Singleton
    @Provides
    fun provideContext(application: Application): Context =
        application.baseContext
}
