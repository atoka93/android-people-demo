package net.attilaszabo.peopledemo.ui.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator

class SwipeToDismissListener(
    private val dismissListener: DismissListener?,
    private val viewMovedListener: ViewMovedListener?,
    private val translationLimitPercentage: Double = 0.25
) : View.OnTouchListener {

    // Members

    private var translationLimit: Int = 0
    private var startX = 0f
    private var startY = 0f

    // View.OnTouchListener

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (translationLimit == 0) {
            translationLimit = (view.height * translationLimitPercentage).toInt()
        }
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.getHitRect(Rect())
                startX = event.x
                startY = event.y
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val currentPositionX = view.translationX
                val currentPositionY = view.translationY

                if (currentPositionY < -translationLimit || currentPositionY > translationLimit) {
                    dismissListener?.onDismiss()
                }

                val x = ObjectAnimator.ofFloat(
                    view,
                    PROPERTY_TRANSLATION_X, currentPositionX,
                    ORIGINAL_POSITION
                )
                val y = ObjectAnimator.ofFloat(
                    view,
                    PROPERTY_TRANSLATION_Y, currentPositionY,
                    ORIGINAL_POSITION
                ).apply {
                    addUpdateListener { animation ->
                        callMoveListener(animation.animatedValue as Float)
                    }
                }

                AnimatorSet().apply {
                    playTogether(x, y)
                    duration = ANIMATION_DURATION_MILLISECONDS
                    interpolator = AccelerateInterpolator()
                    start()
                }

                true
            }
            MotionEvent.ACTION_MOVE -> {
                val translationX = event.x - startX
                val translationY = event.y - startY
                view.translationX = translationX
                view.translationY = translationY
                callMoveListener(translationY)
                true
            }
            else -> false
        }
    }

    // Private Api

    private fun callMoveListener(translationY: Float) {
        viewMovedListener?.onViewMoved(
            START_POSITION - (START_POSITION / translationLimit / ALPHA_DRAG_SPEED) * Math.abs(translationY)
        )
    }

    // Inner class

    interface ViewMovedListener {
        fun onViewMoved(percentage: Float)
    }

    interface DismissListener {
        fun onDismiss()
    }

    companion object {
        private const val PROPERTY_TRANSLATION_X = "translationX"
        private const val PROPERTY_TRANSLATION_Y = "translationY"
        private const val ORIGINAL_POSITION = 0.0f
        private const val ANIMATION_DURATION_MILLISECONDS = 250L
        private const val START_POSITION = 1.0f
        private const val ALPHA_DRAG_SPEED = 2
    }
}
