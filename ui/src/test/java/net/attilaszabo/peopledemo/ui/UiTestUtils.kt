package net.attilaszabo.peopledemo.ui

import kotlinx.coroutines.Dispatchers.Unconfined
import net.attilaszabo.peopledemo.ui.utils.CoroutinesDispatcherProvider

object UiTestUtils {

    fun getTestCoroutinesDispatcherProvider(): CoroutinesDispatcherProvider =
        CoroutinesDispatcherProvider(Unconfined, Unconfined, Unconfined)
}
