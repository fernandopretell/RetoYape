package com.fulbiopretell.core.photoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.Matrix.ScaleToFit
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewParent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.OverScroller

class PhotoViewAttacher: View.OnTouchListener,
    View.OnLayoutChangeListener {

    companion object {
          val DEFAULT_MAX_SCALE = 3.0f
          val DEFAULT_MID_SCALE = 1.75f
          val DEFAULT_MIN_SCALE = 1.0f
          val DEFAULT_ZOOM_DURATION = 200
    }


    private val HORIZONTAL_EDGE_NONE = -1
    private val HORIZONTAL_EDGE_LEFT = 0
    private val HORIZONTAL_EDGE_RIGHT = 1
    private val HORIZONTAL_EDGE_BOTH = 2
    private val VERTICAL_EDGE_NONE = -1
    private val VERTICAL_EDGE_TOP = 0
    private val VERTICAL_EDGE_BOTTOM = 1
    private val VERTICAL_EDGE_BOTH = 2
    private val SINGLE_TOUCH = 1

    private var mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    private var mZoomDuration = DEFAULT_ZOOM_DURATION
    private var mMinScale = DEFAULT_MIN_SCALE
    private var mMidScale = DEFAULT_MID_SCALE
    private var mMaxScale = DEFAULT_MAX_SCALE

    private var mAllowParentInterceptOnEdge = true
    private var mBlockParentIntercept = false

    private lateinit var mImageView: ImageView

    // Gesture Detectors
    private lateinit var mGestureDetector: GestureDetector
    private lateinit var mScaleDragDetector: CustomGestureDetector

    // These are set so we don't keep allocating them on the heap
    private val mBaseMatrix = Matrix()
    private val mDrawMatrix = Matrix()
    private val mSuppMatrix = Matrix()
    private val mDisplayRect = RectF()
    private val mMatrixValues = FloatArray(9)

    // Listeners
    private var mMatrixChangeListener: OnMatrixChangedListener? = null
    private var mPhotoTapListener: OnPhotoTapListener? = null
    private var mOutsidePhotoTapListener: OnOutsidePhotoTapListener? = null
    private var mViewTapListener: OnViewTapListener? = null
    private var mOnClickListener: View.OnClickListener? = null
    private var mLongClickListener: OnLongClickListener? = null
    private var mScaleChangeListener: OnScaleChangedListener? = null
    private var mSingleFlingListener: OnSingleFlingListener? = null
    private var mOnViewDragListener: OnViewDragListener? = null

    private var mCurrentFlingRunnable: FlingRunnable? = null
    private var mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH
    private var mVerticalScrollEdge = VERTICAL_EDGE_BOTH
    private var mBaseRotation: Float = 0.toFloat()

    private var mZoomEnabled = true
    private var mScaleType = ScaleType.FIT_CENTER

    private val onGestureListener = object : OnGestureListener {

        override fun onDrag(dx: Float, dy: Float) {
            if (mScaleDragDetector.isScaling) {
                return  // Do not drag if we are already scaling
            }
            if (mOnViewDragListener != null) {
                mOnViewDragListener?.onDrag(dx, dy)
            }
            mSuppMatrix.postTranslate(dx, dy)
            checkAndDisplayMatrix()

            val parent = mImageView.parent
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling && !mBlockParentIntercept) {
                if (mHorizontalScrollEdge == HORIZONTAL_EDGE_BOTH
                    || mHorizontalScrollEdge == HORIZONTAL_EDGE_LEFT && dx >= 1f
                    || mHorizontalScrollEdge == HORIZONTAL_EDGE_RIGHT && dx <= -1f
                    || mVerticalScrollEdge == VERTICAL_EDGE_TOP && dy >= 1f
                    || mVerticalScrollEdge == VERTICAL_EDGE_BOTTOM && dy <= -1f
                ) {
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
            } else {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
        }

        override fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float) {
            mCurrentFlingRunnable = FlingRunnable(mImageView.context)
            mCurrentFlingRunnable?.fling(
                getImageViewWidth(mImageView),
                getImageViewHeight(mImageView), velocityX.toInt(), velocityY.toInt()
            )
            mImageView.post(mCurrentFlingRunnable)
        }

        override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
            if (getScale() < mMaxScale || scaleFactor < 1f) {
                if (mScaleChangeListener != null) {
                    mScaleChangeListener?.onScaleChange(scaleFactor, focusX, focusY)
                }
                mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
                checkAndDisplayMatrix()
            }
        }
    }

    constructor(imageView: ImageView)  {

        mImageView = imageView

        imageView.setOnTouchListener(this)
        imageView.addOnLayoutChangeListener(this)
        if (imageView.isInEditMode) {
            return
        }
        mBaseRotation = 0.0f
        // Create Gesture Detectors...
        mScaleDragDetector = CustomGestureDetector(imageView.context, onGestureListener)
        mGestureDetector =
            GestureDetector(imageView.context, object : GestureDetector.SimpleOnGestureListener() {

                // forward long click listener
                override fun onLongPress(e: MotionEvent) {
                    if (mLongClickListener != null) {
                        mLongClickListener?.onLongClick(mImageView)
                    }
                }

                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent,
                    velocityX: Float, velocityY: Float
                ): Boolean {
                    if (mSingleFlingListener != null) {
                        if (getScale() > DEFAULT_MIN_SCALE) {
                            return false
                        }
                        return if (e1.pointerCount > SINGLE_TOUCH || e2.pointerCount > SINGLE_TOUCH) {
                            false
                        } else mSingleFlingListener?.onFling(e1, e2, velocityX, velocityY)?:false
                    }
                    return false
                }
            })
        mGestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (mOnClickListener != null) {
                    mOnClickListener?.onClick(mImageView)
                }
                val displayRect = getDisplayRect()
                val x = e.x
                val y = e.y
                if (mViewTapListener != null) {
                    mViewTapListener?.onViewTap(mImageView, x, y)
                }

                displayRect?.let {
                    if (it.contains(x, y)) {
                        val xResult = (x - it.left) / it.width()
                        val yResult = (y - it.top) / it.height()
                        if (mPhotoTapListener != null) {
                            mPhotoTapListener?.onPhotoTap(mImageView, xResult, yResult)
                        }
                        return true
                    } else {
                        if (mOutsidePhotoTapListener != null) {
                            mOutsidePhotoTapListener?.onOutsidePhotoTap(mImageView)
                        }
                    }
                }

                return false
            }

            override fun onDoubleTap(ev: MotionEvent): Boolean {
                try {
                    val scale = getScale()
                    val x = ev.x
                    val y = ev.y
                    if (scale < getMediumScale()) {
                        setScale(getMediumScale(), x, y, true)
                    } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
                        setScale(getMaximumScale(), x, y, true)
                    } else {
                        setScale(getMinimumScale(), x, y, true)
                    }
                } catch (e: ArrayIndexOutOfBoundsException) {
                    // Can sometimes happen when getX() and getY() is called
                }

                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                // Wait for the confirmed onDoubleTap() instead
                return false
            }
        })
    }

    fun setOnDoubleTapListener(newOnDoubleTapListener: GestureDetector.OnDoubleTapListener) {
        this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangeListener: OnScaleChangedListener) {
        this.mScaleChangeListener = onScaleChangeListener
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener
    }


    fun getDisplayRect(): RectF? {
        checkMatrixBounds()
        return getDisplayRect(getDrawMatrix())
    }

    fun setDisplayMatrix(finalMatrix: Matrix?): Boolean {
        requireNotNull(finalMatrix) { "Matrix cannot be null" }
        if (mImageView.drawable == null) {
            return false
        }
        mSuppMatrix.set(finalMatrix)
        checkAndDisplayMatrix()
        return true
    }

    fun setBaseRotation(degrees: Float) {
        mBaseRotation = degrees % 360
        update()
        setRotationBy(mBaseRotation)
        checkAndDisplayMatrix()
    }

    fun setRotationTo(degrees: Float) {
        mSuppMatrix.setRotate(degrees % 360)
        checkAndDisplayMatrix()
    }

    fun setRotationBy(degrees: Float) {
        mSuppMatrix.postRotate(degrees % 360)
        checkAndDisplayMatrix()
    }

    fun getMinimumScale(): Float {
        return mMinScale
    }

    fun getMediumScale(): Float {
        return mMidScale
    }

    fun getMaximumScale(): Float {
        return mMaxScale
    }

    fun getScale(): Float {
        return Math.sqrt(
            (Math.pow(
                getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble(),
                2.0
            ).toFloat() + Math.pow(
                getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble(),
                2.0
            ).toFloat()).toDouble()
        ).toFloat()
    }

    fun getScaleType(): ScaleType {
        return mScaleType
    }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAllowParentInterceptOnEdge = allow
    }

    fun setMinimumScale(minimumScale: Float) {
        Util.checkZoomLevels(minimumScale, mMidScale, mMaxScale)
        mMinScale = minimumScale
    }

    fun setMediumScale(mediumScale: Float) {
        Util.checkZoomLevels(mMinScale, mediumScale, mMaxScale)
        mMidScale = mediumScale
    }

    fun setMaximumScale(maximumScale: Float) {
        Util.checkZoomLevels(mMinScale, mMidScale, maximumScale)
        mMaxScale = maximumScale
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        Util.checkZoomLevels(minimumScale, mediumScale, maximumScale)
        mMinScale = minimumScale
        mMidScale = mediumScale
        mMaxScale = maximumScale
    }

    fun setOnLongClickListener(listener: OnLongClickListener) {
        mLongClickListener = listener
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
        mMatrixChangeListener = listener
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
        mPhotoTapListener = listener
    }

    fun setOnOutsidePhotoTapListener(mOutsidePhotoTapListener: OnOutsidePhotoTapListener) {
        this.mOutsidePhotoTapListener = mOutsidePhotoTapListener
    }

    fun setOnViewTapListener(listener: OnViewTapListener) {
        mViewTapListener = listener
    }

    fun setOnViewDragListener(listener: OnViewDragListener) {
        mOnViewDragListener = listener
    }

    fun setScale(scale: Float) {
        setScale(scale, false)
    }

    fun setScale(scale: Float, animate: Boolean) {
        setScale(
            scale,
            (mImageView.right / 2).toFloat(),
            (mImageView.bottom / 2).toFloat(),
            animate
        )
    }

    fun setScale(
        scale: Float, focalX: Float, focalY: Float,
        animate: Boolean
    ) {
        // Check to see if the scale is within bounds
        require(!(scale < mMinScale || scale > mMaxScale)) { "Scale must be within the range of minScale and maxScale" }
        if (animate) {
            mImageView.post(
                AnimatedZoomRunnable(
                    getScale(), scale,
                    focalX, focalY
                )
            )
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY)
            checkAndDisplayMatrix()
        }
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    fun setZoomInterpolator(interpolator: Interpolator) {
        mInterpolator = interpolator
    }

    fun setScaleType(scaleType: ScaleType) {
        if (Util.isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType
            update()
        }
    }

    fun getZoomable(): Boolean {
        return mZoomEnabled
    }

    fun setZoomable(zoomable: Boolean) {
        mZoomEnabled = zoomable
        update()
    }

    fun update() {
        if (getZoomable()) {
            // Update the base matrix using the current drawable
            updateBaseMatrix(mImageView.drawable)
        } else {
            // Reset the Matrix...
            resetMatrix()
        }
    }

    /**
     * Get the display matrix
     *
     * @param matrix target matrix to copy to
     */
    fun getDisplayMatrix(matrix: Matrix) {
        matrix.set(getDrawMatrix())
    }

    /**
     * Get the current support matrix
     */
    fun getSuppMatrix(matrix: Matrix) {
        matrix.set(mSuppMatrix)
    }

    private fun getDrawMatrix(): Matrix {
        mDrawMatrix.set(mBaseMatrix)
        mDrawMatrix.postConcat(mSuppMatrix)
        return mDrawMatrix
    }

    fun getImageMatrix(): Matrix {
        return mDrawMatrix
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        this.mZoomDuration = milliseconds
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays its contents
     */
    private fun resetMatrix() {
        mSuppMatrix.reset()
        setRotationBy(mBaseRotation)
        setImageViewMatrix(getDrawMatrix())
        checkMatrixBounds()
    }

    private fun setImageViewMatrix(matrix: Matrix) {
        mImageView.imageMatrix = matrix
        // Call MatrixChangedListener if needed
        if (mMatrixChangeListener != null) {
            val displayRect = getDisplayRect(matrix)
            if (displayRect != null) {
                mMatrixChangeListener?.onMatrixChanged(displayRect)
            }
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private fun checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix())
        }
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private fun getDisplayRect(matrix: Matrix): RectF? {
        val d = mImageView.drawable
        if (d != null) {
            mDisplayRect.set(
                0f, 0f, d.intrinsicWidth.toFloat(),
                d.intrinsicHeight.toFloat()
            )
            matrix.mapRect(mDisplayRect)
            return mDisplayRect
        }
        return null
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param drawable - Drawable being displayed
     */
    private fun updateBaseMatrix(drawable: Drawable?) {
        if (drawable == null) {
            return
        }
        val viewWidth = getImageViewWidth(mImageView).toFloat()
        val viewHeight = getImageViewHeight(mImageView).toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        mBaseMatrix.reset()
        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight
        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate(
                (viewWidth - drawableWidth) / 2f,
                (viewHeight - drawableHeight) / 2f
            )

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            val scale = Math.max(widthScale, heightScale)
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate(
                (viewWidth - drawableWidth * scale) / 2f,
                (viewHeight - drawableHeight * scale) / 2f
            )

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            val scale = Math.min(1.0f, Math.min(widthScale, heightScale))
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate(
                (viewWidth - drawableWidth * scale) / 2f,
                (viewHeight - drawableHeight * scale) / 2f
            )

        } else {
            var mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
            val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)
            if (mBaseRotation.toInt() % 180 != 0) {
                mTempSrc = RectF(0f, 0f, drawableHeight.toFloat(), drawableWidth.toFloat())
            }
            when (mScaleType) {
                ImageView.ScaleType.FIT_CENTER -> mBaseMatrix.setRectToRect(
                    mTempSrc,
                    mTempDst,
                    ScaleToFit.CENTER
                )
                ImageView.ScaleType.FIT_START -> mBaseMatrix.setRectToRect(
                    mTempSrc,
                    mTempDst,
                    ScaleToFit.START
                )
                ImageView.ScaleType.FIT_END -> mBaseMatrix.setRectToRect(
                    mTempSrc,
                    mTempDst,
                    ScaleToFit.END
                )
                ImageView.ScaleType.FIT_XY -> mBaseMatrix.setRectToRect(
                    mTempSrc,
                    mTempDst,
                    ScaleToFit.FILL
                )
                else -> {
                }
            }
        }
        resetMatrix()
    }

    private fun checkMatrixBounds(): Boolean {
        val rect = getDisplayRect(getDrawMatrix()) ?: return false
        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f
        val viewHeight = getImageViewHeight(mImageView)
        if (height <= viewHeight) {
            when (mScaleType) {
                ImageView.ScaleType.FIT_START -> deltaY = -rect.top
                ImageView.ScaleType.FIT_END -> deltaY = viewHeight.toFloat() - height - rect.top
                else -> deltaY = (viewHeight - height) / 2 - rect.top
            }
            mVerticalScrollEdge = VERTICAL_EDGE_BOTH
        } else if (rect.top > 0) {
            mVerticalScrollEdge = VERTICAL_EDGE_TOP
            deltaY = -rect.top
        } else if (rect.bottom < viewHeight) {
            mVerticalScrollEdge = VERTICAL_EDGE_BOTTOM
            deltaY = viewHeight - rect.bottom
        } else {
            mVerticalScrollEdge = VERTICAL_EDGE_NONE
        }
        val viewWidth = getImageViewWidth(mImageView)
        if (width <= viewWidth) {
            when (mScaleType) {
                ImageView.ScaleType.FIT_START -> deltaX = -rect.left
                ImageView.ScaleType.FIT_END -> deltaX = viewWidth.toFloat() - width - rect.left
                else -> deltaX = (viewWidth - width) / 2 - rect.left
            }
            mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH
        } else if (rect.left > 0) {
            mHorizontalScrollEdge = HORIZONTAL_EDGE_LEFT
            deltaX = -rect.left
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right
            mHorizontalScrollEdge = HORIZONTAL_EDGE_RIGHT
        } else {
            mHorizontalScrollEdge = HORIZONTAL_EDGE_NONE
        }
        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY)
        return true
    }

    private fun getImageViewWidth(imageView: ImageView): Int {
        return imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView): Int {
        return imageView.height - imageView.paddingTop - imageView.paddingBottom
    }

    private fun cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable?.cancelFling()
            mCurrentFlingRunnable = null
        }
    }

    override fun onTouch(v: View?, ev: MotionEvent?): Boolean {
        var handled = false
        if (getZoomable() && Util.hasDrawable(v as ImageView)) {
            when (ev?.getAction()) {
                MotionEvent.ACTION_DOWN -> {
                    val parent = v.getParent()
                    // First, disable the Parent from intercepting the touch
                    // event
                    parent?.requestDisallowInterceptTouchEvent(true)
                    // If we're flinging, and the user presses down, cancel
                    // fling
                    cancelFling()
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (getScale() < mMinScale) {
                        val rect = getDisplayRect()
                        if (rect != null) {
                            v.post(
                                AnimatedZoomRunnable(
                                    getScale(), mMinScale,
                                    rect.centerX(), rect.centerY()
                                )
                            )
                            handled = true
                        }
                    } else if (getScale() > mMaxScale) {
                        val rect = getDisplayRect()
                        if (rect != null) {
                            v.post(
                                AnimatedZoomRunnable(
                                    getScale(), mMaxScale,
                                    rect.centerX(), rect.centerY()
                                )
                            )
                            handled = true
                        }
                    }
            }
            // Try the Scale/Drag detector
            if (mScaleDragDetector != null) {
                val wasScaling = mScaleDragDetector.isScaling
                val wasDragging = mScaleDragDetector.isDragging

                ev?.let {
                    handled = mScaleDragDetector.onTouchEvent(it)
                }

                val didntScale = !wasScaling && !mScaleDragDetector.isScaling
                val didntDrag = !wasDragging && !mScaleDragDetector.isDragging
                mBlockParentIntercept = didntScale && didntDrag
            }
            // Check to see if the user double tapped
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev!!)) {
                handled = true
            }

        }
        return handled
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
// Update our base matrix, as the bounds have changed
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            updateBaseMatrix(mImageView.drawable)
        }
    }

    private inner class AnimatedZoomRunnable(
        private val mZoomStart: Float, private val mZoomEnd: Float,
        private val mFocalX: Float, private val mFocalY: Float
    ) : Runnable {
        private val mStartTime: Long

        init {
            mStartTime = System.currentTimeMillis()
        }

        override fun run() {
            val t = interpolate()
            val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
            val deltaScale = scale / getScale()
            onGestureListener.onScale(deltaScale, mFocalX, mFocalY)
            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(mImageView, this)
            }
        }

        private fun interpolate(): Float {
            var t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration
            t = Math.min(1f, t)
            t = mInterpolator.getInterpolation(t)
            return t
        }
    }

    private inner class FlingRunnable(context: Context) : Runnable {

        private val mScroller: OverScroller
        private var mCurrentX: Int = 0
        private var mCurrentY: Int = 0

        init {
            mScroller = OverScroller(context)
        }

        fun cancelFling() {
            mScroller.forceFinished(true)
        }

        fun fling(
            viewWidth: Int, viewHeight: Int, velocityX: Int,
            velocityY: Int
        ) {
            val rect = getDisplayRect() ?: return
            val startX = Math.round(-rect.left)
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int
            if (viewWidth < rect.width()) {
                minX = 0
                maxX = Math.round(rect.width() - viewWidth)
            } else {
                maxX = startX
                minX = maxX
            }
            val startY = Math.round(-rect.top)
            if (viewHeight < rect.height()) {
                minY = 0
                maxY = Math.round(rect.height() - viewHeight)
            } else {
                maxY = startY
                minY = maxY
            }
            mCurrentX = startX
            mCurrentY = startY
            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(
                    startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0
                )
            }
        }

        override fun run() {
            if (mScroller.isFinished) {
                return  // remaining post that should not be handled
            }
            if (mScroller.computeScrollOffset()) {
                val newX = mScroller.currX
                val newY = mScroller.currY
                mSuppMatrix.postTranslate(
                    (mCurrentX - newX).toFloat(),
                    (mCurrentY - newY).toFloat()
                )
                checkAndDisplayMatrix()
                mCurrentX = newX
                mCurrentY = newY
                // Post On animation
                Compat.postOnAnimation(mImageView, this)
            }
        }
    }
}
