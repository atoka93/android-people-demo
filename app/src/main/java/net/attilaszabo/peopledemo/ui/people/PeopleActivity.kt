package net.attilaszabo.peopledemo.ui.people

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_people.activityPeopleErrorViewGroup
import kotlinx.android.synthetic.main.activity_people.activityPeopleProgressBar
import kotlinx.android.synthetic.main.activity_people.activityPeopleRecyclerView
import kotlinx.android.synthetic.main.activity_people.activityPeopleRetryButton
import net.attilaszabo.peopledemo.R
import net.attilaszabo.peopledemo.di.ViewModelFactory
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.ERROR
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.INLINE_ERROR
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LIST
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LOADING
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LOADING_MORE
import net.attilaszabo.peopledemo.ui.people.adapter.IPeopleAdapterInteractions
import net.attilaszabo.peopledemo.ui.people.adapter.PeopleAdapter
import net.attilaszabo.peopledemo.ui.people.adapter.PersonViewHolder
import net.attilaszabo.peopledemo.ui.person.PersonActivity
import net.attilaszabo.peopledemo.ui.person.PersonActivity.Companion.BUNDLE_PERSON
import net.attilaszabo.peopledemo.ui.person.PersonActivity.Companion.BUNDLE_POSITION
import net.attilaszabo.peopledemo.ui.utils.SpaceItemDecoration
import net.attilaszabo.peopledemo.ui.utils.setVisibility
import javax.inject.Inject

class PeopleActivity : AppCompatActivity(), IPeopleAdapterInteractions {

    // Members

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var peopleAdapter: PeopleAdapter

    private val compositeDisposable by lazy { CompositeDisposable() }
    private lateinit var viewModel: PeopleViewModel
    private var scrollPosition: Parcelable? = null

    // AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_people)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PeopleViewModel::class.java)

        setupView()

        initializeBindings().forEach {
            compositeDisposable.add(it)
        }

        scrollPosition = savedInstanceState?.getParcelable(BUNDLE_SCROLL_POSITION)

        viewModel.loadPeople(amount = savedInstanceState?.getInt(BUNDLE_NUMBER_OF_LOADED_PEOPLE))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(BUNDLE_NUMBER_OF_LOADED_PEOPLE, peopleAdapter.itemCount)
        outState?.putParcelable(
            BUNDLE_SCROLL_POSITION,
            activityPeopleRecyclerView.layoutManager?.onSaveInstanceState()
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    // Private Api

    private fun setupView() {
        peopleAdapter = PeopleAdapter(this)
        activityPeopleRecyclerView.apply {
            adapter = peopleAdapter
            val spanCount = resources.getInteger(R.integer.span_count_grid)
            addItemDecoration(
                SpaceItemDecoration(
                    spacing = resources.getDimensionPixelOffset(R.dimen.spacing_grid_divider),
                    spanCount = spanCount
                )
            )
            (layoutManager as GridLayoutManager).spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int =
                            peopleAdapter.getSpanSize(position = position, spanCount = spanCount)
                    }
        }
        activityPeopleRetryButton.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun initializeBindings() = listOf(
        viewModel.onViewStateChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewState ->
                when (viewState) {
                    LOADING -> {
                        activityPeopleProgressBar.setVisibility(true)
                        activityPeopleErrorViewGroup.setVisibility(false)
                        activityPeopleRecyclerView.setVisibility(false)
                    }
                    LOADING_MORE -> {
                        peopleAdapter.showLoading()
                    }
                    LIST -> {
                        activityPeopleProgressBar.setVisibility(false)
                        activityPeopleErrorViewGroup.setVisibility(false)
                        activityPeopleRecyclerView.setVisibility(true)
                    }
                    ERROR, null -> {
                        activityPeopleProgressBar.setVisibility(false)
                        activityPeopleErrorViewGroup.setVisibility(true)
                        activityPeopleRecyclerView.setVisibility(false)
                    }
                    INLINE_ERROR -> {
                        peopleAdapter.showError()
                    }
                }
            },

        viewModel.onNewPeople()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newPeople ->
                peopleAdapter.addPeople(newPeople = newPeople)
                activityPeopleRecyclerView.layoutManager?.onRestoreInstanceState(scrollPosition)
                scrollPosition = null
            }
    )

    // IPeopleAdapterInteractions

    override fun onPersonClicked(viewHolder: PersonViewHolder, position: Int, person: Person) {
        val intent = Intent(this, PersonActivity::class.java).apply {
            putExtra(BUNDLE_POSITION, position)
            putExtra(BUNDLE_PERSON, person)
        }

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(
                viewHolder.itemPersonNameTextView,
                viewHolder.itemPersonNameTextView.transitionName
            ),
            Pair(
                viewHolder.itemPersonAgeTextView,
                viewHolder.itemPersonAgeTextView.transitionName
            ),
            Pair(
                viewHolder.itemPersonImageView,
                viewHolder.itemPersonImageView.transitionName
            )
        )

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    override fun loadMorePeople() {
        viewModel.loadMorePeople()
    }

    override fun retryLoadingMorePeople() {
        viewModel.retryLoadingMore()
    }

    companion object {
        private const val BUNDLE_NUMBER_OF_LOADED_PEOPLE = "integerNumberOfLoadedPeople"
        private const val BUNDLE_SCROLL_POSITION = "parcelableScrollPosition"
    }
}
