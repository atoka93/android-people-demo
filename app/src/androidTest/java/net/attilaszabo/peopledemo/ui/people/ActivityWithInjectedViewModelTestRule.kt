package net.attilaszabo.peopledemo.ui.people

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import net.attilaszabo.peopledemo.PeopleDemoApplication
import net.attilaszabo.peopledemo.di.ViewModelFactory
import javax.inject.Provider

class ActivityWithInjectedViewModelTestRule<A : Activity, VM : ViewModel>(
    activityClass: Class<A>,
    private val viewModel: () -> VM
) : ActivityTestRule<A>(activityClass, true, true) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        val application =
            InstrumentationRegistry.getTargetContext().applicationContext as PeopleDemoApplication
        val viewModel = viewModel()
        application.dispatchingAndroidInjector = createFakeActivityInjector<PeopleActivity> {
            viewModelFactory = ViewModelFactory(
                mapOf(
                    viewModel::class.java to Provider<ViewModel> {
                        viewModel
                    }
                )
            )
        }
    }
}

inline fun <reified T : Activity> createFakeActivityInjector(crossinline block: T.() -> Unit)
        : DispatchingAndroidInjector<Activity> {
    val injector = AndroidInjector<Activity> { instance ->
        if (instance is T) {
            instance.block()
        }
    }
    val map = mapOf(
        Pair<Class<out Activity>, Provider<AndroidInjector.Factory<out Activity>>>(
            T::class.java,
            Provider { AndroidInjector.Factory<Activity> { injector } }
        )
    )
    return DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(map)
}
