package net.attilaszabo.peopledemo.ui.person

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_person.activityPersonAgeTextView
import kotlinx.android.synthetic.main.activity_person.activityPersonConstraintLayout
import kotlinx.android.synthetic.main.activity_person.activityPersonImageView
import kotlinx.android.synthetic.main.activity_person.activityPersonNameTextView
import kotlinx.android.synthetic.main.activity_person.activityPersonRegionTextView
import net.attilaszabo.peopledemo.R
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.utils.GlideRequestFinishedListener
import net.attilaszabo.peopledemo.ui.utils.SwipeDirectionListener
import net.attilaszabo.peopledemo.ui.utils.SwipeToDismissListener

class PersonActivity : AppCompatActivity(),
    SwipeDirectionListener.DirectionDetectedListener,
    SwipeToDismissListener.ViewMovedListener,
    SwipeToDismissListener.DismissListener {

    // Members

    private lateinit var directionListener: SwipeDirectionListener
    private var dragDirection: SwipeDirectionListener.Direction = SwipeDirectionListener.Direction.NOT_DETECTED
    private lateinit var swipeToDismissListener: SwipeToDismissListener

    // AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        supportPostponeEnterTransition()

        directionListener = SwipeDirectionListener(
            directionDetectedListener = this,
            touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        )

        swipeToDismissListener = SwipeToDismissListener(
            dismissListener = this,
            viewMovedListener = this
        )

        setupView()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        val wasConsumed = when {
            motionEvent.action == MotionEvent.ACTION_DOWN ||
                    (dragDirection == SwipeDirectionListener.Direction.DOWN && motionEvent.pointerCount == 1) -> {
                swipeToDismissListener.onTouch(
                    view = activityPersonConstraintLayout,
                    event = motionEvent
                )
            }
            else -> null
        }
        directionListener.onTouchEvent(event = motionEvent)
        return wasConsumed ?: super.dispatchTouchEvent(motionEvent)
    }

    // Private Api

    private fun setupView() {
        intent.getParcelableExtra<Person>(BUNDLE_PERSON)?.let { person ->
            intent.getIntExtra(BUNDLE_POSITION, 0).let { position ->
                activityPersonNameTextView.transitionName = "$position${person.name}"
                activityPersonAgeTextView.transitionName = "$position${person.age}"
                activityPersonImageView.transitionName = "$position${person.photo}"
            }

            activityPersonNameTextView.text = person.name
            activityPersonAgeTextView.text = resources.getString(R.string.age, person.age)
            activityPersonRegionTextView.text = person.region

            val glideRequestOptions = RequestOptions()
                .centerCrop()
                .error(ColorDrawable(ContextCompat.getColor(this, android.R.color.black)))
                .priority(Priority.HIGH)
            Glide.with(this)
                .load(person.photo)
                .apply(glideRequestOptions)
                .listener(GlideRequestFinishedListener {
                    supportStartPostponedEnterTransition()
                })
                .into(activityPersonImageView)
        }
    }

    // SwipeDirectionListener.DirectionDetectedListener

    override fun onDirection(direction: SwipeDirectionListener.Direction) {
        this.dragDirection = direction
    }

    // SwipeToDismissListener.ViewMovedListener

    override fun onViewMoved(percentage: Float) {
        activityPersonConstraintLayout?.alpha = percentage
    }

    // SwipeToDismissListener.DismissListener

    override fun onDismiss() {
        finishAfterTransition()
    }

    companion object {
        const val BUNDLE_POSITION = "intPosition"
        const val BUNDLE_PERSON = "parcelablePerson"
    }
}
