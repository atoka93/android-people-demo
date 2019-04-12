package net.attilaszabo.peopledemo.ui.person

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import dagger.android.support.AndroidSupportInjection
import net.attilaszabo.peopledemo.ui.R
import net.attilaszabo.peopledemo.ui.databinding.FragmentPersonBinding
import net.attilaszabo.peopledemo.ui.di.viewmodel.ViewModelFactory
import net.attilaszabo.peopledemo.ui.utils.GlideRequestFinishedListener
import javax.inject.Inject

class PersonFragment : Fragment() {

    // Members

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: PersonViewModel

    // AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PersonViewModel::class.java)

        navArgs<PersonFragmentArgs>().value.apply {
            viewModel.setPerson(person)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentPersonBinding>(inflater, R.layout.fragment_person, container, false)
            .apply {
                viewModel = this@PersonFragment.viewModel
                requestOptions = RequestOptions()
                    .centerCrop()
                    .error(ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.black)))
                    .priority(Priority.HIGH)
                loadedListener = GlideRequestFinishedListener {
                    executePendingBindings()
                    startPostponedEnterTransition()
                }
            }
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.person_shared_element_transition).apply {
            duration = 400
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }
}
