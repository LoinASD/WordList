package io.cyanlab.wordlist.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import android.view.animation.Interpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import io.cyanlab.wordlist.R

class Backdrop(context: Context, attributeSet: AttributeSet): FrameLayout( context, attributeSet) {

    private val dropHeight: Int = context.resources.getDimensionPixelOffset(R.dimen.backdrop_height)

    private lateinit var toolbar: Toolbar
    private var openIcon: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_menu_cyan_900_24dp, null)
    private var closeIcon: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_close_cyan_900_24dp, null)
    private var frontLayerBackground: Int = R.drawable.shape
    private lateinit var navIconClickListener: NavigationIconClickListener


    fun withToolbar(toolbar: Toolbar): Backdrop {
        this.toolbar = toolbar
        this.toolbar.navigationIcon = openIcon
        return this
    }

    fun build() {

        navIconClickListener = NavigationIconClickListener(context,
                backView = getBackView(),
                sheet =  getFrontView(),
                interpolator = AccelerateDecelerateInterpolator(),
                openIcon = openIcon,
                closeIcon = closeIcon
        )

        // throw if toolbar not set
        checkNotNull(toolbar) {
            IllegalStateException("Toolbar must not be null")
        }

        // on toolbar navigation click, handle it
        toolbar.setNavigationOnClickListener(navIconClickListener)
    }

    fun openBackdrop() = navIconClickListener.open()

    fun closeBackdrop() = navIconClickListener.close()


    override fun onFinishInflate() {
        super.onFinishInflate()

        if(childCount != 2){
            throw IllegalArgumentException(" ${this.javaClass.simpleName} Must contain two child")
        }

        getFrontView().background = ResourcesCompat.getDrawable(resources, frontLayerBackground, null)
        //getFrontView().background = ShapeDrawable(CutShape())
    }


    private fun getBackView(): View = getChildAt(0)


    private fun getFrontView(): View = getChildAt(1)

    inner class NavigationIconClickListener(
            context: Context,
            private val backView: View,
            private val sheet: View,
            private val interpolator: Interpolator? = null,
            private val openIcon: Drawable? = null,
            private val closeIcon: Drawable? = null) : View.OnClickListener {

        private val animatorSet = AnimatorSet()
        private val animDuration = 200L
        private var height: Int
        private var backdropShown = false
        private var toolbarNavIcon: AppCompatImageButton? = null

        init {
            val displayMetrics = DisplayMetrics()
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            height = (displayMetrics.heightPixels)
        }

        fun open() = if(!backdropShown){ onClick(toolbarNavIcon!!) } else {}
        fun close() = if(backdropShown){ onClick(toolbarNavIcon!!) } else {}

        override fun onClick(view: View) {
            // only bind once
            if(toolbarNavIcon == null) {
                this.toolbarNavIcon = view as AppCompatImageButton
            }

            backdropShown = !backdropShown


            val size = backView.width + view.height - dropHeight
            val translateY = height - size


            animatorSet.removeAllListeners()
            animatorSet.end()
            animatorSet.cancel()

            updateIcon(view)


            val animator = ObjectAnimator.ofFloat(sheet, "translationY",
                    (if (backdropShown) translateY else 0).toFloat())
            animator.duration = animDuration
            interpolator?.let{interpolator ->
                animator.interpolator = interpolator
            }

            // play the animation
            animatorSet.play(animator)
            animator.start()

        }

        private fun updateIcon(view: View) {
            checkNotNull(toolbarNavIcon)

            if (openIcon != null && closeIcon != null) {
                when(backdropShown) {
                    true -> toolbarNavIcon!!.setImageDrawable(closeIcon)
                    false -> toolbarNavIcon!!.setImageDrawable(openIcon)
                }
            }
        }

    }
}