package net.attilaszabo.peopledemo.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import net.attilaszabo.peopledemo.PeopleDemoApplication
import net.attilaszabo.peopledemo.data.DataModule
import net.attilaszabo.peopledemo.data.sources.cachedb.CacheDatabaseDataSourceModule
import net.attilaszabo.peopledemo.data.sources.uinames.UiNamesDataSourceModule
import net.attilaszabo.peopledemo.domain.DomainModule
import net.attilaszabo.peopledemo.ui.di.viewmodel.ViewModelModule
import javax.inject.Singleton

@Suppress("unused")
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilder::class,
        ViewModelModule::class,
        DomainModule::class,
        DataModule::class,
        CacheDatabaseDataSourceModule::class,
        UiNamesDataSourceModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: PeopleDemoApplication)
}
