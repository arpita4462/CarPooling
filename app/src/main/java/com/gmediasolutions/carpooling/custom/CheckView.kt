package com.gmediasolutions.carpooling.custom

import android.animation.ValueAnimator
import com.google.android.gms.internal.y
import com.google.android.gms.internal.x
import java.lang.reflect.Array.getLength
import android.support.annotation.FloatRange
import android.view.animation.AccelerateInterpolator
import android.view.animation.PathInterpolator
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.view.animation.FastOutSlowInInterpolator
import com.google.android.gms.internal.w
import android.content.res.TypedArray
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import com.gmediasolutions.carpooling.R


class CheckView : View {

    private var mCheckInterpolator: Interpolator? = null
    /**
     * The path of the circle around the check mark
     */
    private var mPathCircle: Path? = null
    /**
     * The path of the check mark
     */
    private var mPathCheck: Path? = null
    /**
     * The length of the start of the check mark, before the pivot point
     */
    private var mMinorContourLength: Float = 0.toFloat()
    /**
     * The length of the check mark after the pivot point, and up to the end point.
     */
    private var mMajorContourLength: Float = 0.toFloat()
    /**
     * The size of the check mark and circle paths.
     */
    private var mStrokeWidth = DEFAULT_STROKE_WIDTH
    private var mStrokeColor = DEFAULT_STROKE_COLOR
    /**
     * A Rect describing the area on this View's canvas where the check mark should be drawn.
     * This is intended to account for padding.
     */
    private var mDrawingRect: RectF? = null
    /**
     * A Rect describing the drawable area for the circle around the check mark.
     * This takes into account the extra room needed for the stroke width.
     */
    private var mCircleRect: RectF? = null
    private var mPaint: Paint? = null
    private var mPathMeasure: PathMeasure? = null
    /**
     * A pre-allocated float array to hold path measure results.
     */
    private var mPoint: FloatArray? = null
    /**
     * Where the check mark starts
     */
    private var mCheckStart: PointF? = null
    /**
     * Where the check mark turns upward
     */
    private var mCheckPivot: PointF? = null
    /**
     * Where the check mark ends
     */
    private var mCheckEnd: PointF? = null
    /**
     * Where the circle border starts
     */
    private var mCircleStart: PointF? = null
    private var mCheckAnimator: ValueAnimator? = null
    private var mCircleAnimator: ValueAnimator? = null
    private var mScaleAnimator: ValueAnimator? = null
    private var mChecked = false
    //endregion private methods

    //region animator listeners
    private val mCheckAnimatorListener = object : ValueAnimator.AnimatorUpdateListener {
       override fun onAnimationUpdate(animation: ValueAnimator) {
            val fraction = animation.getAnimatedFraction()
            setCheckPathPercentage(fraction)
            invalidate()
        }
    }

    private val mCircleAnimatorListener = object : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val fraction = animation.getAnimatedFraction()
            setCirclePathPercentage(fraction)
            invalidate()
        }
    }

    private val mScaleAnimatorListener = object : ValueAnimator.AnimatorUpdateListener {
      override  fun onAnimationUpdate(animation: ValueAnimator) {
            val value = animation.getAnimatedValue() as Float
            this@CheckView.setScaleX(value)
            this@CheckView.setScaleY(value)
            invalidate()
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, @Nullable attrs: AttributeSet?) {
        resolveAttributes(context, attrs)
        mPathCheck = Path()
        mPathCircle = Path()
        mDrawingRect = RectF()
        mCircleRect = RectF()
        mPathMeasure = PathMeasure()
        mPoint = FloatArray(2)
        mCheckStart = PointF()
        mCheckPivot = PointF()
        mCheckEnd = PointF()
        mCircleStart = PointF()
        mCheckAnimator = ValueAnimator.ofFloat(0f, 1f)
        mCircleAnimator = ValueAnimator.ofFloat(0f, 1f)
        mScaleAnimator = ValueAnimator.ofFloat(1f, SCALE_MIN, 1f)
        mCheckInterpolator = createCheckInterpolatorCompat()
        mPaint = createPaint(mStrokeColor, mStrokeWidth)
    }

    private fun resolveAttributes(c: Context, @Nullable attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }

        val a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.CheckView, 0, 0)

        try {
            mStrokeWidth = a.getDimension(R.styleable.CheckView_checkView_strokeWidth, DEFAULT_STROKE_WIDTH)
            mStrokeColor = a.getColor(R.styleable.CheckView_checkView_strokeColor, DEFAULT_STROKE_COLOR)
        } finally {
            a.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mDrawingRect!!.left = paddingLeft.toFloat()
            mDrawingRect!!.top = paddingTop.toFloat()
            mDrawingRect!!.right = (measuredWidth - paddingRight).toFloat()
            mDrawingRect!!.bottom = (measuredHeight - paddingBottom).toFloat()

            mCheckStart!!.x = mDrawingRect!!.left + mDrawingRect!!.width() / 4
            mCheckStart!!.y = mDrawingRect!!.top + mDrawingRect!!.height() / 2
            mCheckPivot!!.x = mDrawingRect!!.left + mDrawingRect!!.width() * .426f
            mCheckPivot!!.y = mDrawingRect!!.top + mDrawingRect!!.height() * .66f
            mCheckEnd!!.x = mDrawingRect!!.left + mDrawingRect!!.width() * .75f
            mCheckEnd!!.y = mDrawingRect!!.top + mDrawingRect!!.height() * .30f

            mMinorContourLength = distance(mCheckStart!!.x, mCheckStart!!.y, mCheckPivot!!.x, mCheckPivot!!.y)
            mMajorContourLength = distance(mCheckPivot!!.x, mCheckPivot!!.y, mCheckEnd!!.x, mCheckEnd!!.y)

            mCircleRect!!.left = mDrawingRect!!.left + mStrokeWidth / 2
            mCircleRect!!.top = mDrawingRect!!.top + mStrokeWidth / 2
            mCircleRect!!.right = mDrawingRect!!.right - mStrokeWidth / 2
            mCircleRect!!.bottom = mDrawingRect!!.bottom - mStrokeWidth / 2
            mCircleStart!!.x = mCircleRect!!.right
            mCircleStart!!.y = mCircleRect!!.bottom / 2

            if (DEBUG && mDrawingRect!!.width() != mDrawingRect!!.height()) {
                Log.w(
                    TAG,
                    "WARNING: " + CheckView::class.java.simpleName + " will look weird because you've given it a non-square drawing area.  " +
                            "Make sure the width, height, and padding resolve to a square."
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!mChecked) {
            return
        }
        canvas.drawPath(mPathCheck, mPaint)
        canvas.drawPath(mPathCircle, mPaint)
    }

    //region instance methods

    /**
     * Tell this [CheckView] to animate into the checked state.
     */
    fun check() {
        mChecked = true
        mCheckAnimator!!.removeAllUpdateListeners()
        mCheckAnimator!!.setDuration(CHECK_ANIM_DURATION)
            .setInterpolator(mCheckInterpolator)
        mCheckAnimator!!.addUpdateListener(mCheckAnimatorListener)

        mCircleAnimator!!.removeAllUpdateListeners()
        mCircleAnimator!!.setDuration(CHECK_ANIM_DURATION)
            .setInterpolator(mCheckInterpolator)
        mCircleAnimator!!.addUpdateListener(mCircleAnimatorListener)

        mScaleAnimator!!.removeAllUpdateListeners()
        mScaleAnimator!!.setDuration(SCALE_ANIM_DURATION)
            .setStartDelay(SCALE_ANIM_DELAY)
        mScaleAnimator!!.setInterpolator(FastOutSlowInInterpolator())
        mScaleAnimator!!.addUpdateListener(mScaleAnimatorListener)

        mCheckAnimator!!.start()
        mCircleAnimator!!.start()
        mScaleAnimator!!.start()
    }

    /**
     * Reset to an unchecked state.  This will not animate.
     */
    fun uncheck() {
        mChecked = false
        invalidate()
    }
    //endregion instance methods

    //region private methods
    private fun createPaint(@ColorInt color: Int, strokeWidth: Float): Paint {
        val p = Paint()
        p.setColor(color)
        p.setStyle(Paint.Style.STROKE)
        p.setStrokeWidth(strokeWidth)
        p.setStrokeJoin(Paint.Join.ROUND)
        p.setAntiAlias(true)
        p.setStrokeCap(Paint.Cap.ROUND)
        return p
    }

    private fun createCheckInterpolatorCompat(): Interpolator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PathInterpolator(0.755f, 0.05f, 0.855f, 0.06f)
        } else {
            AccelerateInterpolator()
        }
    }

    /**
     * What does the check mark path look like at it's full length?
     */
    private fun setCheckPathFull() {
        mPathCheck!!.reset()
        mPathCheck!!.moveTo(mCheckStart!!.x, mCheckStart!!.y)
        mPathCheck!!.lineTo(mCheckPivot!!.x, mCheckPivot!!.y)
        mPathCheck!!.lineTo(mCheckEnd!!.x, mCheckEnd!!.y)
    }

    /**
     * What does the check mark path look like at `percent` of it's total length?
     */
    private fun setCheckPathPercentage(@FloatRange(from = 0.0, to = 1.0) percent: Float) {
        setCheckPathFull()
        val totalLength = mMinorContourLength + mMajorContourLength
        val pivotPercent = mMinorContourLength / totalLength

        // TODO: try this with a simple getSegment();
        if (percent > pivotPercent) {
            val remainder = percent - pivotPercent
            val distance = totalLength * remainder
            mPathCheck!!.reset()
            mPathCheck!!.moveTo(mCheckPivot!!.x, mCheckPivot!!.y)
            mPathCheck!!.lineTo(mCheckEnd!!.x, mCheckEnd!!.y)
            mPathMeasure!!.setPath(mPathCheck, false)
            mPathMeasure!!.getPosTan(distance, mPoint, null)
            mPathCheck!!.reset()
            mPathCheck!!.moveTo(mCheckStart!!.x, mCheckStart!!.y)
            mPathCheck!!.lineTo(mCheckPivot!!.x, mCheckPivot!!.y)
            mPathCheck!!.lineTo(mPoint!![0], mPoint!![1])
        } else if (percent < pivotPercent) {
            val minorPercent = percent / pivotPercent
            val distance = mMinorContourLength * minorPercent
            mPathMeasure!!.setPath(mPathCheck, false)
            mPathMeasure!!.getPosTan(distance, mPoint, null)
            mPathCheck!!.reset()
            mPathCheck!!.moveTo(mCheckStart!!.x, mCheckStart!!.y)
            mPathCheck!!.lineTo(mPoint!![0], mPoint!![1])
        } else if (percent == pivotPercent) {
            mPathCheck!!.lineTo(mCheckPivot!!.x, mCheckPivot!!.y)
        }
    }

    private fun setCirclePathPercentage(@FloatRange(from = 0.0, to = 1.0) percent: Float) {
        mPathCircle!!.reset()
        mPathCircle!!.moveTo(mCircleStart!!.x, mCircleStart!!.y)
        mPathCircle!!.addArc(mCircleRect, 0f, 360f)

        mPathMeasure!!.setPath(mPathCircle, false)
        val distance = mPathMeasure!!.length * percent
        mPathMeasure!!.getPosTan(distance, mPoint, null)
        mPathCircle!!.reset()
        mPathCircle!!.moveTo(mCircleStart!!.x, mCircleStart!!.y)
        mPathCircle!!.arcTo(mCircleRect, 0f, 359 * percent)
    }

    companion object {

        private val TAG = CheckView::class.java.simpleName
        private val DEBUG = false
        private val CHECK_ANIM_DURATION = 600L
        private val SCALE_ANIM_DELAY = 560L
        private val SCALE_ANIM_DURATION = 500L
        private val DEFAULT_STROKE_WIDTH = 8f
        private val DEFAULT_STROKE_COLOR = -0xe55500 // greenish
        private val SCALE_MIN = 0.80f

        private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val xAbs = Math.abs(x1 - x2)
            val yAbs = Math.abs(y1 - y2)
            return Math.sqrt((yAbs * yAbs + xAbs * xAbs).toDouble()).toFloat()
        }
    }
    //endregion
}