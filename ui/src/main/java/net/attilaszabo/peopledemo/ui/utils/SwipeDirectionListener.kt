package net.attilaszabo.peopledemo.ui.utils

import android.view.MotionEvent

class SwipeDirectionListener(
    private val directionDetectedListener: DirectionDetectedListener,
    private val touchSlop: Int
) {

    // Members

    private var isDirectionDetected = false
    private var startX = 0f
    private var startY = 0f

    // Public Api

    fun onTouchEvent(event: MotionEvent): Boolean =
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                directionDetectedListener.onDirection(Direction.NOT_DETECTED)
                startX = event.x
                startY = event.y
                true
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                directionDetectedListener.onDirection(Direction.NOT_DETECTED)
                isDirectionDetected = false
                startX = 0.0f
                startY = 0.0f
                true
            }
            MotionEvent.ACTION_MOVE -> {
                while (!isDirectionDetected && getDistance(event) > touchSlop) {
                    isDirectionDetected = true
                    directionDetectedListener.onDirection(
                        getDirection(startX, startY, event.x, event.y)
                    )
                }
                true
            }
            else -> false
        }

    // Private Api

    private fun getDirection(x1: Float, y1: Float, x2: Float, y2: Float): Direction {
        val rad = Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
        val angle = (rad * 180 / Math.PI + 180) % 360
        return Direction[angle]
    }

    private fun getDistance(event: MotionEvent): Float {
        val dx = event.getX(0) - startX
        val dy = event.getY(0) - startY
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    // Inner class

    enum class Direction {
        NOT_DETECTED,
        UP,
        LEFT,
        DOWN,
        RIGHT;

        companion object {
            operator fun get(angle: Double) = when (angle) {
                in 45f..135f -> UP
                in 135f..225f -> LEFT
                in 225f..315f -> DOWN
                in 0f..45f, in 315f..360f -> RIGHT
                else -> NOT_DETECTED
            }
        }
    }

    interface DirectionDetectedListener {
        fun onDirection(direction: Direction)
    }
}
