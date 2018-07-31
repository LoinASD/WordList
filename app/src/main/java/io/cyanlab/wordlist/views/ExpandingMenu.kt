package io.cyanlab.wordlist.views

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

interface ExpandingMenuContainer{

    var isMenuExpanded: Boolean

    fun expandMenu()

    fun collapseMenu()

    val listeners: ArrayList<Listener>

    interface Listener{

        fun onMenuAnimationStarted(isExpanded: Boolean)

        fun onMenuAnimationFinished(isExpanded: Boolean)
    }
}

class BackdropMenuContainer(val backPanel: View?, private val frontPanel: View?, var offset: Float): ExpandingMenuContainer{

    override var isMenuExpanded: Boolean = false

    override fun expandMenu() {

        isMenuExpanded = true

        animateBackdrop(isMenuExpanded)
    }

    override fun collapseMenu() {

        isMenuExpanded = false

        animateBackdrop(isMenuExpanded)
    }

    private fun animateBackdrop(isExpanded: Boolean){

        val dropHeight = backPanel?.height?.toFloat() ?: 0f - offset

        val startTrans = if (!isExpanded) dropHeight else 0f
        val endTrans = if (!isExpanded) 0f else dropHeight

        val startAlpha = if (!isExpanded) 0.6f else 1f
        val endAlpha = if (!isExpanded) 1f else 0.6f

        frontPanel?.translationY = startTrans
        frontPanel?.alpha = startAlpha

        listeners.forEach { it.onMenuAnimationStarted(isExpanded) }

        frontPanel?.animate()?.
                translationY(endTrans)?.
                alpha(endAlpha)?.
                setDuration(225)?.
                setInterpolator(AccelerateDecelerateInterpolator())?.
                withEndAction {

                    listeners.forEach {it.onMenuAnimationFinished(isExpanded)}
                }?.
                start()
    }

    override val listeners = ArrayList<ExpandingMenuContainer.Listener>()

}