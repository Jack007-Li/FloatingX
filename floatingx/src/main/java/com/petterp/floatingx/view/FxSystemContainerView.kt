package com.petterp.floatingx.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import com.petterp.floatingx.assist.helper.FxAppHelper
import com.petterp.floatingx.util.screenHeight
import com.petterp.floatingx.util.screenWidth
import com.petterp.floatingx.view.basic.FxBasicParentView

/** 基础悬浮窗View */
@SuppressLint("ViewConstructor")
class FxSystemContainerView @JvmOverloads constructor(
    override val helper: FxAppHelper,
    private val wm: WindowManager,
    context: Context,
    attrs: AttributeSet? = null,
) : FxBasicParentView(helper, context, attrs) {

    private lateinit var wl: WindowManager.LayoutParams

    private var downTouchX = 0f
    private var downTouchY = 0f

    val isAttachToWM: Boolean
        get() = windowToken != null

    override fun initView() {
        super.initView()
        installChildView() ?: return
        initWLParams()
    }

    internal fun registerWM(wm: WindowManager) {
        if (isAttachToWM) return
        wm.addView(this, wl)
    }

    override fun currentX(): Float {
        return wl.x.toFloat()
    }

    override fun currentY(): Float {
        return wl.y.toFloat()
    }

    override fun preCheckPointerDownTouch(event: MotionEvent): Boolean {
        // 当前屏幕存在手指时，check当前手势是否真的在浮窗之上
        val x = event.rawX
        val y = event.rawY
        val location = IntArray(2)
        getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + this.width
        val bottom = top + this.height
        return x >= left && x <= right && y >= top && y <= bottom
    }

    override fun onTouchDown(event: MotionEvent) {
        downTouchX = wl.x.minus(event.rawX)
        downTouchY = wl.y.minus(event.rawY)
    }

    override fun onTouchMove(event: MotionEvent) {
        val x = downTouchX.plus(event.rawX)
        val y = downTouchY.plus(event.rawY)
        safeUpdateXY(x, y)
    }

    override fun onTouchCancel(event: MotionEvent) {
        downTouchX = 0f
        downTouchY = 0f
    }

    override fun updateXY(x: Float, y: Float) {
        wl.x = x.toInt()
        wl.y = y.toInt()
        wm.updateViewLayout(this, wl)
    }

    override fun parentSize(): Pair<Int, Int> {
        return helper.context.screenWidth to helper.context.screenHeight
    }

    private fun initWLParams() {
        wl = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.START
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
        }
    }
}
