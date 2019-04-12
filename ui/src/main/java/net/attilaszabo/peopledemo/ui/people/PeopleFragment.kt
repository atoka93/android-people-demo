package net.attilaszabo.peopledemo.ui.people

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_people.fragmentPeopleErrorViewGroup
import kotlinx.android.synthetic.main.fragment_people.fragmentPeopleProgressBar
import kotlinx.android.synthetic.main.fragment_people.fragmentPeopleRecyclerView
import kotlinx.android.synthetic.main.fragment_people.fragmentPeopleRetryButton
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.R
import net.attilaszabo.peopledemo.ui.databinding.FragmentPeopleBinding
import net.attilaszabo.peopledemo.ui.databinding.ItemPersonBinding
import net.attilaszabo.peopledemo.ui.di.viewmodel.ViewModelFactory
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.ERROR
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.INLINE_ERROR
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LIST
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LOADING
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LOADING_MORE
import net.attilaszabo.peopledemo.ui.people.adapter.IPeopleAdapterInteractions
import net.attilaszabo.peopledemo.ui.people.adapter.PeopleAdapter
import net.attilaszabo.peopledemo.ui.utils.SpaceItemDecoration
import net.attilaszabo.peopledemo.ui.utils.setVisibility
import javax.inject.Inject

class PeopleFragment : Fragment(), IPeopleAdapterInteractions {

    // Members

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var peopleAdapter: PeopleAdapter

    private lateinit var viewModel: PeopleViewModel
    private var scrollPosition: Parcelable? = null

    // Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PeopleViewModel::class.java)

        scrollPosition = savedInstanceState?.getParcelable(BUNDLE_SCROLL_POSITION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        DataBindingUtil.inflate<FragmentPeopleBinding>(inflater, R.layout.fragment_people, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        initializeBindings()

        if (savedInstanceState == null) {
            viewModel.loadPeople(amount = savedInstanceState?.getInt(BUNDLE_NUMBER_OF_LOADED_PEOPLE))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_NUMBER_OF_LOADED_PEOPLE, peopleAdapter.itemCount)
        outState.putParcelable(BUNDLE_SCROLL_POSITION, fragmentPeopleRecyclerView?.layoutManager?.onSaveInstanceState())
    }

    // Private Api

    private fun setupView() {
        peopleAdapter = PeopleAdapter(this)
        fragmentPeopleRecyclerView.apply {
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
        fragmentPeopleRetryButton.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun initializeBindings() = listOf(
        viewModel.viewState.observe(this, Observer { viewState ->
            when (viewState) {
                LOADING -> {
                    fragmentPeopleProgressBar.setVisibility(true)
                    fragmentPeopleErrorViewGroup.setVisibility(false)
                    fragmentPeopleRecyclerView.setVisibility(false)
                }
                LOADING_MORE -> {
                    peopleAdapter.showLoading()
                }
                LIST -> {
                    fragmentPeopleProgressBar.setVisibility(false)
                    fragmentPeopleErrorViewGroup.setVisibility(false)
                    fragmentPeopleRecyclerView.setVisibility(true)
                }
                ERROR, null -> {
                    fragmentPeopleProgressBar.setVisibility(false)
                    fragmentPeopleErrorViewGroup.setVisibility(true)
                    fragmentPeopleRecyclerView.setVisibility(false)
                }
                INLINE_ERROR -> {
                    peopleAdapter.showError()
                }
            }
        }),

        viewModel.people.observe(this, Observer { newPeople ->
            peopleAdapter.addPeople(newPeople = newPeople)
            fragmentPeopleRecyclerView.layoutManager?.onRestoreInstanceState(scrollPosition)
            scrollPosition = null
        })
    )

    // IPeopleAdapterInteractions

    override fun onPersonClicked(viewDataBinding: ViewDataBinding, person: Person) {
        (viewDataBinding as ItemPersonBinding).apply {
            findNavController().navigate(
                PeopleFragmentDirections.detailsAction(person),
                FragmentNavigatorExtras(
                    itemPersonNameTextView to "name",
                    itemPersonAgeTextView to "age",
                    itemPersonImageView to "picture"
                )
            )
        }
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
