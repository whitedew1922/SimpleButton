package com.fs.customviews.simple_button

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatButton

/**
 * Created by cindyfeliciasantoso on 25/01/22
 * Copyright (c) Cindy Felicia Santoso
 */
class SimpleButton : AppCompatButton {

    private val paddingProgress = 20
    private var fontSize = 47

    private var canvas: Canvas? = null
    private var animatedDrawable: CircularAnimatedDrawable? = null
    private var initialText: String = ""

    private var buttonType = 0
    private var gradientColors = intArrayOf(
        R.color.purple_200.getColor(context),
        R.color.purple_500.getColor(context)
    )
    private var gradientAngle = 0
    private var buttonColor = R.color.purple_500.getColor(context)
    private var buttonTextColor = R.color.white.getColor(context)
    private var buttonDisableColor = R.color.buttonDisableColor.getColor(context)
    private var disableTextColor = R.color.buttonDisableTextColor.getColor(context)
    private var buttonStrokeWidth = 3
    private var isBackgroundGradient = false
    private var cornerRadius = 15
    private var progressStrokeColor = R.color.white.getColor(context)
    private var progressStrokeWidth = 10f

    var isLoading = false

    /**
     * Simple constructor to use when creating a button from code.
     *
     * @param context The Context the Button is running in, through which it can
     *        access the current theme, resources, etc.
     *
     * @see #Button(Context, AttributeSet)
     */
    constructor(context: Context) : super(context) {
        getAttributes(context, null, null)
    }

    /**
     * {@link LayoutInflater} calls this constructor when inflating a Button from XML.
     * The attributes defined by the current theme's
     * {@link android.R.attr#buttonStyle android:buttonStyle}
     * override base view attributes.
     *
     * You typically do not call this constructor to create your own button instance in code.
     * However, you must override this constructor when
     * <a href="{@docRoot}training/custom-views/index.html">creating custom views</a>.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML Button tag being used to inflate the view.
     *
     * @see #Button(Context, AttributeSet, int)
     * @see android.view.View#View(Context, AttributeSet)
     */
    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        getAttributes(context, attr, null)
    }

    /**
     * This constructor allows a Button subclass to use its own class-specific base style from a
     * theme attribute when inflating. The attributes defined by the current theme's
     * {@code defStyleAttr} override base view attributes.
     *
     * <p>For Button's base view attributes see
     * {@link android.R.styleable#Button Button Attributes},
     * {@link android.R.styleable#TextView TextView Attributes},
     * {@link android.R.styleable#View View Attributes}.
     *
     * @param context The Context the Button is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML Button tag that is inflating the view.
     * @param defStyleAttr The resource identifier of an attribute in the current theme
     *        whose value is the the resource id of a style. The specified style’s
     *        attribute values serve as default values for the button. Set this parameter
     *        to 0 to avoid use of default values.
     * @see #Button(Context, AttributeSet, int, int)
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(context, attr, defStyle) {
        getAttributes(context, attr, defStyle)
    }

    init {
        height = 48.toPx(context)
        elevation = 0f
        stateListAnimator = null
        transformationMethod = null

        val typeFace = Typeface.createFromAsset(context.assets, "segoe_ui_bold.ttf")
        typeface = typeFace
    }

    // region Lifecycle
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.canvas = canvas
        drawButtonBackground()
        setLoading()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        if (!text.isNullOrBlank()) initialText = text.toString()
    }
    // endregion

    // region PrivateFunction
    private fun getAttributes(context: Context, attr: AttributeSet?, defStyle: Int?) {
        context.theme.obtainStyledAttributes(attr, R.styleable.CustomButton, defStyle ?: 0, 0)
            .apply {
                try {
                    buttonType = getInteger(R.styleable.CustomButton_buttonType, 0)
                    buttonColor = getColor(R.styleable.CustomButton_buttonColor, buttonColor)
                    buttonTextColor = getColor(R.styleable.CustomButton_android_textColor, buttonTextColor)
                    buttonDisableColor = getColor(R.styleable.CustomButton_buttonDisableColor, buttonDisableColor)
                    disableTextColor = getColor(R.styleable.CustomButton_buttonDisableTextColor, disableTextColor)
                    buttonStrokeWidth = getInteger(R.styleable.CustomButton_buttonStrokeWidth, buttonStrokeWidth)

                    isBackgroundGradient = getBoolean(R.styleable.CustomButton_isBackgroundGradient, isBackgroundGradient)
                    val startGradientColor = getColor(R.styleable.CustomButton_gradientColorStart, gradientColors[0])
                    val endGradientColor = getColor(R.styleable.CustomButton_gradientColorEnd, gradientColors[1])
                    gradientColors = intArrayOf(startGradientColor, endGradientColor)
                    gradientAngle = getInteger(R.styleable.CustomButton_gradientAngle, gradientAngle)

                    cornerRadius = getInteger(R.styleable.CustomButton_buttonCornerRadius, cornerRadius)

                    progressStrokeColor = getColor(R.styleable.CustomButton_progressStrokeColor, progressStrokeColor)
                    progressStrokeWidth = getFloat(R.styleable.CustomButton_progressStrokeWidth, progressStrokeWidth)

                    fontSize = getDimensionPixelSize(R.styleable.CustomButton_android_textSize, fontSize)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.toFloat())
                } finally {
                    recycle()
                }
            }

        invalidate()
        requestLayout()
    }

    private fun drawButtonBackground() {
        if (buttonType == 0) {
            drawPrimaryBackground()
        } else {
            drawSecondaryBackground()
        }
    }

    private fun drawPrimaryBackground() {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setSize(width, height)
        shape.cornerRadius = cornerRadius.toDp(context).toFloat()

        if (isBackgroundGradient && isEnabled) {
            shape.gradientType = GradientDrawable.LINEAR_GRADIENT
            shape.orientation = getGradientAngle()
            shape.colors = gradientColors
            setTextColor(R.color.white.getColor(context))
        } else {
            shape.color = if (isEnabled) {
                setTextColor(buttonTextColor)
                ColorStateList.valueOf(buttonColor)
            } else {
                setTextColor(disableTextColor)
                ColorStateList.valueOf(buttonDisableColor)
            }
        }

        background = shape
    }

    private fun drawSecondaryBackground() {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setSize(width, height)
        shape.cornerRadius = cornerRadius.toDp(context).toFloat()

        if (isEnabled) {
            shape.color = ColorStateList.valueOf(Color.TRANSPARENT)
            shape.setStroke(buttonStrokeWidth, ColorStateList.valueOf(buttonColor))
            setTextColor(buttonColor)
        } else {
            setTextColor(disableTextColor)
            shape.color = ColorStateList.valueOf(buttonDisableColor)
        }

        background = shape
    }

    private fun getGradientAngle(): GradientDrawable.Orientation {
        return when (gradientAngle) {
            0 -> GradientDrawable.Orientation.TOP_BOTTOM
            1 -> GradientDrawable.Orientation.TR_BL
            2 -> GradientDrawable.Orientation.RIGHT_LEFT
            3 -> GradientDrawable.Orientation.BR_TL
            4 -> GradientDrawable.Orientation.BOTTOM_TOP
            5 -> GradientDrawable.Orientation.BL_TR
            6 -> GradientDrawable.Orientation.LEFT_RIGHT
            7 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.TOP_BOTTOM
        }
    }

    private fun drawIndeterminateProgress(canvas: Canvas) {
        if (animatedDrawable == null) {
            val size = (height - paddingProgress) / 2
            val maxSize = 30

            val centerX = width / 2
            val centerY = height / 2

            val left = (centerX - Integer.min(maxSize, size))
            val right = (centerX + Integer.min(maxSize, size))
            val bottom = (centerY + Integer.min(maxSize, size))
            val top = (centerY - Integer.min(maxSize, size))

            animatedDrawable = CircularAnimatedDrawable(progressStrokeColor, progressStrokeWidth)
            animatedDrawable?.setBounds(left, top, right, bottom)
            animatedDrawable?.callback = this
            animatedDrawable?.start()
        } else {
            animatedDrawable?.draw(canvas)
        }
    }

    private fun setLoading() {
        if (isLoading) {
            canvas?.let {
                drawIndeterminateProgress(it)
            }
            text = ""
        } else {
            if (animatedDrawable != null) {
                animatedDrawable?.stop()
                animatedDrawable = null
            }
            text = initialText
        }
    }
    // endregion

    // region PublicFunction
    fun setButtonType(type: Type) {
        buttonType = type.value
        drawButtonBackground()
    }

    fun setButtonColor(@ColorInt color: Int) {
        buttonColor = color
        drawButtonBackground()
    }

    fun setDisableButtonColor(@ColorInt color: Int) {
        buttonDisableColor = color
        drawButtonBackground()
    }

    fun setButtonDisableTextColor(@ColorRes color: Int) {
        disableTextColor = color
        drawButtonBackground()
    }

    fun setButtonStrokeWidth(width: Int) {
        buttonStrokeWidth = width
        drawButtonBackground()
    }

    fun setIsGradient(isGradient: Boolean) {
        isBackgroundGradient = isGradient
        drawButtonBackground()
    }

    fun setGradientColor(angle: Angle, @ColorInt vararg colors: Int) {
        gradientColors = colors
        gradientAngle = angle.value
        drawButtonBackground()
    }

    fun setCornerRadius(radius: Int) {
        cornerRadius = radius
        drawButtonBackground()
    }

    fun setProgressStroke(@ColorInt color: Int, width: Float) {
        progressStrokeColor = color
        progressStrokeWidth = width
        drawButtonBackground()
    }

    fun showLoading() {
        this.isLoading = true
        isEnabled = false
        setLoading()
    }

    fun hideLoading() {
        this.isLoading = false
        isEnabled = true
        setLoading()
    }
    // endregion

    enum class Type(val value: Int) {
        PRIMARY(0),
        SECONDARY(1)
    }

    enum class Angle(val value: Int) {
        TOP_BOTTOM(0),
        TOP_RIGHT_BOTTOM_LEFT(1),
        RIGHT_LEFT(2),
        BOTTOM_RIGHT_TOP_LEFT(3),
        BOTTOM_TOP(4),
        BOTTOM_LEFT_TOP_RIGHT(5),
        LEFT_RIGHT(6),
        TOP_LEFT_BOTTOM_RIGHT(7)
    }
}