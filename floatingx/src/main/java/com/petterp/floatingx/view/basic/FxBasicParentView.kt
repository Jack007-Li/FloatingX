package com.petterp.floatingx.view.basic

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.petterp.floatingx.assist.helper.FxBasisHelper
import com.petterp.floatingx.util.INVALID_LAYOUT_ID
import com.petterp.floatingx.view.FxViewHolder
import com.petterp.floatingx.view.IFxInternalView

/**
 * @author petterp
 */
abstract class FxBasicParentView @JvmOverloads constructor(
    open val helper: FxBasisHelper,
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), IFxInternalView {
    private var isInitLayout = true
    private var _childView: View? = null
    private var _viewHolder: FxViewHolder? = null
    private val touchHelper = FxViewTouchHelper()
    private val animateHelper = FxViewAnimationHelper()
    private val locationHelper = FxViewLocationHelper()

    abstract fun currentX(): Float
    abstract fun currentY(): Float
    abstract fun updateXY(x: Float, y: Float)
    abstract fun parentSize(): Pair<Int, Int>?

    abstract fun onTouchDown(event: MotionEvent)
    abstract fun onTouchMove(event: MotionEvent)
    abstract fun onTouchCancel(event: MotionEvent)
    open fun preCheckPointerDownTouch(event: MotionEvent): Boolean = true
    open fun onLayoutInit() {}

    override val childView: View? get() = _childView
    override val containerView: FrameLayout get() = this
    override val viewHolder: FxViewHolder? get() = _viewHolder

    open fun initView() {
        touchHelper.initConfig(this)
        animateHelper.initConfig(this)
        locationHelper.initConfig(this)
    }

    override fun moveToEdge() {
        val (x, y) = locationHelper.getDefaultEdgeXY() ?: return
        moveLocation(x, y, true)
    }

    override fun moveLocation(x: Float, y: Float, useAnimation: Boolean) {
        moveToXY(x, y, useAnimation)
    }

    override fun moveLocationByVector(x: Float, y: Float, useAnimation: Boolean) {
        moveToXY(x + currentX(), y + currentY(), useAnimation)
    }

    override fun updateView() {
        helper.fxLog.d("fxView -> updateView")
        locationHelper.updateLocationStatus()
        removeView(_childView)
        installChildView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        locationHelper.onSizeChanged()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        checkOrInitLayout()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return touchHelper.interceptTouchEvent(event) || super.onInterceptTouchEvent(event)
    }

    protected fun safeUpdateXY(x: Float, y: Float) {
        val safeX = locationHelper.safeX(x, true)
        val safeY = locationHelper.safeY(y, true)
        updateXY(safeX, safeY)
    }

    protected fun installChildView(): View? {
        _childView = inflateLayoutView() ?: inflateLayoutId()
        if (_childView != null) _viewHolder = FxViewHolder(_childView)
        return _childView
    }

    private fun inflateLayoutView(): View? {
        val view = helper.layoutView ?: return null
        helper.fxLog.d("fxView -> init, way:[layoutView]")
        val lp = view.layoutParams ?: LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        addView(view, lp)
        return view
    }

    private fun inflateLayoutId(): View? {
        if (helper.layoutId == INVALID_LAYOUT_ID) return null
        helper.fxLog.d("fxView -> init, way:[layoutId]")
        val view = LayoutInflater.from(context).inflate(helper.layoutId, this, false)
        addView(view)
        return view
    }

    private fun moveToXY(x: Float, y: Float, useAnimation: Boolean) {
        val endX = locationHelper.safeX(x)
        val endY = locationHelper.safeY(y)
        if (currentX() == endX && currentY() == endY) return
        internalMoveToXY(useAnimation, endX, endY)
        locationHelper.checkOrSaveLocation(endX, endY)
        helper.fxLog.d("fxView -> moveToXY: start(${currentX()},${currentY()}),end($endX,$endY)")
    }

    private fun internalMoveToXY(useAnimation: Boolean, endX: Float, endY: Float) {
        if (useAnimation) {
            animateHelper.start(endX, endY)
        } else {
            updateXY(endX, endY)
        }
    }

    private fun checkOrInitLayout() {
        if (!isInitLayout) return
        isInitLayout = false
        locationHelper.initLayout(this)
        onLayoutInit()
    }
}
