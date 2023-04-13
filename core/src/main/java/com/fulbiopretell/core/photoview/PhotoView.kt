package com.fulbiopretell.core.photoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

class PhotoView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attr, defStyle) {

    var attacher: PhotoViewAttacher? = null

    private var pendingScaleType: ImageView.ScaleType? = null

    var isZoomable: Boolean
        get() = attacher?.getZoomable() ?: true
        set(zoomable) {
            attacher?.setZoomable(zoomable)
        }

    val displayRect: RectF?
        get() = attacher?.getDisplayRect()

    var minimumScale: Float
        get() = attacher?.getMinimumScale() ?: PhotoViewAttacher.DEFAULT_MIN_SCALE
        set(minimumScale) {
            attacher?.setMinimumScale(minimumScale)
        }

    var mediumScale: Float
        get() = attacher?.getMediumScale() ?: PhotoViewAttacher.DEFAULT_MID_SCALE
        set(mediumScale) {
            attacher?.setMediumScale(mediumScale)
        }

    var maximumScale: Float
        get() = attacher?.getMaximumScale() ?: PhotoViewAttacher.DEFAULT_MAX_SCALE
        set(maximumScale) {
            attacher?.setMaximumScale(maximumScale)
        }

    var scale: Float
        get() = attacher?.getScale() ?: PhotoViewAttacher.DEFAULT_MIN_SCALE
        set(scale) {
            attacher?.setScale(scale)
        }

    init {
        init()
    }

    private fun init() {
        attacher = PhotoViewAttacher(this)
        super.setScaleType(ImageView.ScaleType.MATRIX)

        if (pendingScaleType != null) {
            setScaleType(pendingScaleType)
            pendingScaleType = null
        }
    }

    override fun getScaleType(): ImageView.ScaleType? {
        return attacher?.getScaleType()
    }

    override fun getImageMatrix(): Matrix? {
        return attacher?.getImageMatrix()
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        l?.let { attacher?.setOnLongClickListener(it) }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        l?.let { attacher?.setOnClickListener(it) }
    }

    override fun setScaleType(scaleType: ImageView.ScaleType?) {
        if (attacher == null) {
            pendingScaleType = scaleType
        } else {
            scaleType?.let {
                attacher?.setScaleType(it)
            }
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher?.update()
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (attacher != null) {
            attacher?.update()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        if (attacher != null) {
            attacher?.update()
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) {
            attacher?.update()
        }
        return changed
    }

    fun setRotationTo(rotationDegree: Float) {
        attacher?.setRotationTo(rotationDegree)
    }

    fun setRotationBy(rotationDegree: Float) {
        attacher?.setRotationBy(rotationDegree)
    }

    fun getDisplayMatrix(matrix: Matrix) {
        attacher?.getDisplayMatrix(matrix)
    }

    fun setDisplayMatrix(finalRectangle: Matrix): Boolean {
        return attacher?.setDisplayMatrix(finalRectangle) ?: false
    }

    fun getSuppMatrix(matrix: Matrix) {
        attacher?.getSuppMatrix(matrix)
    }

    fun setSuppMatrix(matrix: Matrix): Boolean {
        return attacher?.setDisplayMatrix(matrix) ?: false
    }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        attacher?.setAllowParentInterceptOnEdge(allow)
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        attacher?.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
        attacher?.setOnMatrixChangeListener(listener)
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
        attacher?.setOnPhotoTapListener(listener)
    }

    fun setOnOutsidePhotoTapListener(listener: OnOutsidePhotoTapListener) {
        attacher?.setOnOutsidePhotoTapListener(listener)
    }

    fun setOnViewTapListener(listener: OnViewTapListener) {
        attacher?.setOnViewTapListener(listener)
    }

    fun setOnViewDragListener(listener: OnViewDragListener) {
        attacher?.setOnViewDragListener(listener)
    }

    fun setScale(scale: Float, animate: Boolean) {
        attacher?.setScale(scale, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        attacher?.setScale(scale, focalX, focalY, animate)
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        attacher?.setZoomTransitionDuration(milliseconds)
    }

    fun setOnDoubleTapListener(onDoubleTapListener: GestureDetector.OnDoubleTapListener) {
        attacher?.setOnDoubleTapListener(onDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangedListener: OnScaleChangedListener) {
        attacher?.setOnScaleChangeListener(onScaleChangedListener)
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener) {
        attacher?.setOnSingleFlingListener(onSingleFlingListener)
    }
}
