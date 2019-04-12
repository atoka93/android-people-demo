package net.attilaszabo.peopledemo.ui.people

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.mock
import net.attilaszabo.peopledemo.ui.ActivityWithInjectedViewModelTestRule
import net.attilaszabo.peopledemo.ui.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given

@RunWith(AndroidJUnit4::class)
class PeopleActivityTest {

    private val people = listOf(
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson(),
        DomainTestUtils.generatePerson()
    )

    @Rule
    @JvmField
    val activityTestRule =
        ActivityWithInjectedViewModelTestRule(PeopleFragment::class.java) {
            runBlocking {
                val loadPeopleUseCase: LoadPeopleUseCase = mock()
                given(loadPeopleUseCase(0, 10)).willReturn(Result.Success(people))
                given(loadPeopleUseCase(10, 10)).willReturn(Result.Success(people))
                given(loadPeopleUseCase(20, 10)).willReturn(Result.Success(people))
                given(loadPeopleUseCase(30, 10)).willReturn(Result.Success(people))
                given(loadPeopleUseCase(40, 10)).willReturn(Result.Success(people))
                PeopleViewModel(UiTestUtils.getTestCoroutinesDispatcherProvider(), loadPeopleUseCase)
            }
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
