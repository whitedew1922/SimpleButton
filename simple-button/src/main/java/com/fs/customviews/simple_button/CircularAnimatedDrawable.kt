package com.fs.customviews.simple_button

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Property
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

/**
 * Created by cindyfeliciasantoso on 25/01/22
 * Copyright (c) Cindy Felicia Santoso
 */
class CircularAnimatedDrawable(color: Int, private val borderWidth: Float) :
    Drawable(), Animatable {
    private val fBounds = RectF()
    private var objectAnimatorSweep: ObjectAnimator? = null
    private var objectAnimatorAngle: ObjectAnimator? = null
    private var modeAppearing = false
    private val paint: Paint = Paint()
    private var running = false
    private var currentGlobalAngleOffset = 0f

    override fun draw(canvas: Canvas) {
        var startAngle = currentGlobalAngle - currentGlobalAngleOffset
        var sweepAngle = currentSweepAngle
        if (!modeAppearing) {
            startAngle += sweepAngle
            sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE
        } else {
            sweepAngle += MIN_SWEEP_ANGLE.toFloat()
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    private fun toggleAppearingMode() {
        modeAppearing = !modeAppearing
        if (modeAppearing) {
            currentGlobalAngleOffset =
                (currentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fBounds.left = bounds.left + borderWidth / 2f + .5f
        fBounds.right = bounds.right - borderWidth / 2f - .5f
        fBounds.top = bounds.top + borderWidth / 2f + .5f
        fBounds.bottom = bounds.bottom - borderWidth / 2f - .5f
    }

    private val mAngleProperty: Property<CircularAnimatedDrawable, Float> =
        object : Property<CircularAnimatedDrawable, Float>(
            Float::class.java, "angle"
        ) {
            override fun get(`object`: CircularAnimatedDrawable): Float {
                return `object`.currentGlobalAngle
            }

            override fun set(
                `object`: CircularAnimatedDrawable,
                value: Float
            ) {
                `object`.currentGlobalAngle = value
            }
        }

    private val mSweepProperty: Property<CircularAnimatedDrawable, Float> =
        object : Property<CircularAnimatedDrawable, Float>(
            Float::class.java, "arc"
        ) {
            override fun get(`object`: CircularAnimatedDrawable): Float {
                return `object`.currentSweepAngle
            }

            override fun set(
                `object`: CircularAnimatedDrawable,
                value: Float
            ) {
                `object`.currentSweepAngle = value
            }
        }

    private fun setupAnimations() {
        objectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty, 360f)
        objectAnimatorAngle?.apply {
            interpolator =
                ANGLE_INTERPOLATOR
            duration = ANGLE_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }
        objectAnimatorSweep = ObjectAnimator.ofFloat(
            this,
            mSweepProperty,
            360f - MIN_SWEEP_ANGLE * 2
        )
        objectAnimatorSweep?.apply {
            interpolator =
                SWEEP_INTERPOLATOR
            duration = SWEEP_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {
                    toggleAppearingMode()
                }
            })
        }
    }

    override fun start() {
        if (isRunning) {
            return
        }
        running = true
        objectAnimatorAngle!!.start()
        objectAnimatorSweep!!.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }
        running = false
        objectAnimatorAngle!!.cancel()
        objectAnimatorSweep!!.cancel()
        invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return running
    }

    var currentGlobalAngle: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    var currentSweepAngle: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    companion object {
        private val ANGLE_INTERPOLATOR: Interpolator = LinearInterpolator()
        private val SWEEP_INTERPOLATOR: Interpolator = DecelerateInterpolator()
        private const val ANGLE_ANIMATOR_DURATION = 1000
        private const val SWEEP_ANIMATOR_DURATION = 1000
        const val MIN_SWEEP_ANGLE = 30
    }

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        paint.color = color
        setupAnimations()
    }
}
