package net.attilaszabo.peopledemo

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import net.attilaszabo.peopledemo.di.DaggerAppComponent
import javax.inject.Inject

class PeopleDemoApplication : Application(), HasActivityInjector {

    // Members

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    // Application

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }

    // HasActivityInjector

    override fun activityInjector() = dispatchingAndroidInjector
}
