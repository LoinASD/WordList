package io.cyanlab.wordlist.activities.MainActivityStuff

import android.view.View
import io.cyanlab.wordlist.views.ExpandingMenuContainer

class SimpleMenuController(val menuContainer: ExpandingMenuContainer?): View.OnClickListener{

    override fun onClick(p0: View?) {

        val isExpanded = menuContainer?.isMenuExpanded ?: return

        if (isExpanded)

            menuContainer.collapseMenu()
        else
            menuContainer.expandMenu()
    }
}

class SimpleMenuCollapser(val menuContainer: ExpandingMenuContainer?): View.OnClickListener{

    override fun onClick(p0: View?) {

        val isExpanded = menuContainer?.isMenuExpanded ?: return

        if (!isExpanded)
            return

        menuContainer.collapseMenu()
    }
}

class SimpleMenuExpander(val menuContainer: ExpandingMenuContainer?): View.OnClickListener{

    override fun onClick(p0: View?) {

        val isExpanded = menuContainer?.isMenuExpanded ?: return

        if (isExpanded)
            return

        menuContainer.expandMenu()
    }
}