package net.attilaszabo.peopledemo.ui.people

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import io.reactivex.Flowable
import net.attilaszabo.peopledemo.R
import net.attilaszabo.peopledemo.TestUtils
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.mock
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given

@RunWith(AndroidJUnit4::class)
class PeopleActivityTest {

    private val people = listOf(
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson(),
        TestUtils.generatePerson()
    )

    @Rule
    @JvmField
    val activityTestRule = ActivityWithInjectedViewModelTestRule(PeopleActivity::class.java) {
        val loadPeopleUseCase: LoadPeopleUseCase = mock()
        given(loadPeopleUseCase.execute(0, 10)).willReturn(Flowable.just(Result.Success(people)))
        given(loadPeopleUseCase.execute(10, 10)).willReturn(Flowable.just(Result.Success(people)))
        PeopleViewModel(loadPeopleUseCase)
    }

    @Test
    fun activityPeopleRecyclerViewIsVisible() {
        onView(ViewMatchers.withId(R.id.activityPeopleRecyclerView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}
