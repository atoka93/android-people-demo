package net.attilaszabo.peopledemo.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import net.attilaszabo.peopledemo.PeopleDemoApplication
import javax.inject.Singleton

@Suppress("unused")
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        DataModule::class,
        DomainModule::class,
        ViewModelModule::class,
        ActivityBuilder::class
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
