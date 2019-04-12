package net.attilaszabo.peopledemo.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.attilaszabo.peopledemo.ui.main.MainActivity
import net.attilaszabo.peopledemo.ui.main.MainModule

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class, MainFragmentBuilderModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
