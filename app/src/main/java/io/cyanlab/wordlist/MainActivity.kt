package io.cyanlab.wordlist

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alt_main.*

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

class MainActivity : AppCompatActivity(){

    var menuContainer: ExpandingMenuContainer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alt_main)
        setSupportActionBar(main_toolbar)

        //backdrop.withToolbar(main_toolbar).build()

        menuContainer = BackdropMenuContainer(app_bar_frame, fore_frame, resources?.getDimension(R.dimen.shape_radius) ?: 0f)

        menuContainer?.listeners?.add(SimpleMenuListener())

        main_toolbar.setNavigationOnClickListener {

            val isExpanded = menuContainer?.isMenuExpanded ?: return@setNavigationOnClickListener

            if (isExpanded)

                menuContainer?.collapseMenu()
            else
                menuContainer?.expandMenu()
        }

        fore_frame.setOnClickListener {

            val isExpanded = menuContainer?.isMenuExpanded ?: return@setOnClickListener

            if (!isExpanded)
                return@setOnClickListener

            menuContainer?.collapseMenu()
        }

        main_toolbar?.setOnClickListener {

            val isExpanded = menuContainer?.isMenuExpanded ?: return@setOnClickListener

            if (isExpanded)
                return@setOnClickListener

            menuContainer?.expandMenu()
        }
    }

/*    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }*/

    inner class SimpleMenuListener: ExpandingMenuContainer.Listener {

        override fun onMenuAnimationStarted(isExpanded: Boolean) {

            val icon = resources?.getDrawable(when (isExpanded){

                true -> R.drawable.ic_close_cyan_900_24dp
                false -> R.drawable.ic_menu_cyan_900_24dp
            })

            supportActionBar?.setHomeAsUpIndicator(icon)
        }

        override fun onMenuAnimationFinished(isExpanded: Boolean) {


        }

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


