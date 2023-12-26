package com.petterp.floatingx.app.kotlin

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.app.*
import com.petterp.floatingx.app.simple.FxAnimationImpl
import com.petterp.floatingx.app.test.BlackActivity
import com.petterp.floatingx.app.test.MultipleFxActivity
import com.petterp.floatingx.assist.FxDisplayMode
import com.petterp.floatingx.assist.FxGravity
import com.petterp.floatingx.assist.FxScopeType
import com.petterp.floatingx.impl.lifecycle.FxProxyTagLifecycleImp
import com.petterp.floatingx.listener.IFxViewLifecycle
import com.petterp.floatingx.util.FxScrollImpl

/** Kotlin-Application */
class CustomKtApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        FloatingX.install {
//            setLayout(R.layout.item_floating)
//
//            // 如果你全局 [只需要一个浮窗]，这里可以不用传递 tag，默认我们会使用 FX_DEFAULT_TAG 作为未传递TAG时的默认值
//            // 这样的好处是，后续调用控制器(FloatingX.control())时，不用传递 tag。因为相应的方法默认参数里已经携带了该tag
//            // 比如：FloatingX.control()、FloatingX.configControl()
//            // 注意：如果你重复调用install()方法，且未设置tag，那么新的浮窗将会覆盖旧的默认浮窗
//
//            // 注意：这里的tag是用来区分不同的浮窗的，如果你需要多个浮窗，那么请务必设置不同的tag
//            // 注意: 当你调用控制器时，必须传递对应的tag，否则将会抛出异常，除非你使用了 [可null] 的获取方法
//            setTag(MultipleFxActivity.TAG_1)
//        }

        installTag1(this)
        installTag2(this)
    }

    companion object {

        fun installTag1(context: Application) {
            FloatingX.install {
                setContext(context)
                setSystemScope(FxScopeType.SYSTEM)
                // 设置浮窗展示类型，默认可移动可点击，无需配置
                setDisplayMode(FxDisplayMode.Normal)
                setLayout(R.layout.item_floating)
                // 传递自定义的View
//            setLayoutView(
//                TextView(applicationContext).apply {
//                    text = "App"
//                    textSize = 15f
//                    setBackgroundColor(Color.GRAY)
//                    setPadding(10, 10, 10, 10)
//                }
//            )

                // 设置悬浮窗默认方向
                setGravity(FxGravity.RIGHT_OR_TOP)
                // 启用辅助方向,具体参加方法注释
                setEnableAssistDirection(r = 100f, t = 100f)
                // 设置启用边缘吸附,默认启用
                setEnableEdgeAdsorption(true)
                // 设置边缘偏移量
                setEdgeOffset(10f)
                // 设置启用悬浮窗可屏幕外回弹
                setEnableScrollOutsideScreen(true)
                // 开启历史位置缓存
//                setSaveDirectionImpl(FxConfigStorageToSpImpl(context))
                // 设置启用动画
                setEnableAnimation(true)
                // 设置启用动画实现
                setAnimationImpl(FxAnimationImpl())
                // 设置移动边框
                setBorderMargin(50f, 50f, 50f, 50f)

                /** 指定浮窗可显示的activity方式 */
                // 1.设置是否允许所有activity都进行显示,默认true
                setEnableAllInstall(true)
                // 2.禁止插入Activity的页面, setEnableAllBlackClass(true)时,此方法生效
                addInstallBlackClass(BlackActivity::class.java)
                // 3.允许插入Activity的页面, setEnableAllBlackClass(false)时,此方法生效
//            addInstallWhiteClass(
//                MainActivity::class.java,
//                ImmersedActivity::class.java,
//                ScopeActivity::class.java
//            )

                // 设置点击事件
                setOnClickListener {
                    Toast.makeText(context, "浮窗被点击", Toast.LENGTH_SHORT).show()
                }
                // 设置tag-Activity生命周期回调时的触发
                setTagActivityLifecycle(object : FxProxyTagLifecycleImp() {
                    override fun onCreated(activity: Activity, bundle: Bundle?) {
                        // 允许插入的浮窗activity执行到onCreated时会回调相应方法
                    }
                })
                // 增加生命周期监听
                setViewLifecycle(object : IFxViewLifecycle {
                    override fun initView(view: View) {
                    }
                })
                // 设置滑动监听
                setScrollListener(object : FxScrollImpl() {
                    override fun down() {
                        // 按下
                    }

                    override fun up() {
                        // 释放
                    }

                    override fun dragIng(event: MotionEvent, x: Float, y: Float) {
                        // 正在拖动
                    }

                    override fun eventIng(event: MotionEvent) {
                        // 接收所有事件传递
                    }
                })
                // 设置是否启用日志
                setEnableLog(BuildConfig.DEBUG)
                // 设置浮窗tag
                setTag(MultipleFxActivity.TAG_1)
                // 只有调用了enableFx,默认才会启用fx,否则fx不会自动插入activity
                // ps: 这里的只有调用了enableFx仅仅只是配置工具层的标记,后续使用control.show()也会默认启用
                enableFx()
            }.show()
        }

        fun installTag2(context: Application) {
            FloatingX.install {
                setContext(context)
                setSystemScope(FxScopeType.APP_ACTIVITY)
                setLayoutView(
                    CardView(context).apply {
                        setCardBackgroundColor(Color.GRAY)
                        radius = 30.dpF
                        addView(
                            TextView(this.context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    -2,
                                    60.dp,
                                )
                                gravity = Gravity.CENTER
                                text = "浮窗2-act"
                                setTextColor(Color.WHITE)
                                textSize = 15f
                            },
                        )
                    },
                )
                setTag(MultipleFxActivity.TAG_2)
                setEnableLog(true)
                enableFx()
            }
        }
    }
}
