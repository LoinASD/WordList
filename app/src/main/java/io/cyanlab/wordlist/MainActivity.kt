package io.cyanlab.wordlist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import io.cyanlab.wordlist.activities.FileManagerActivity
import io.cyanlab.wordlist.activities.MainActivityStuff.SimpleMenuCollapser
import io.cyanlab.wordlist.activities.MainActivityStuff.SimpleMenuController
import io.cyanlab.wordlist.activities.MainActivityStuff.SimpleMenuExpander
import io.cyanlab.wordlist.activities.MainActivityStuff.mPDFCallback
import kotlinx.android.synthetic.main.alt_main.*
import io.cyanlab.wordlist.activities.MainFragment
import io.cyanlab.wordlist.activities.MainMenuFragment
import io.cyanlab.wordlist.controllers.PDFManager
import io.cyanlab.wordlist.controllers.mPDFManager
import io.cyanlab.wordlist.models.database.DBHolder
import io.cyanlab.wordlist.models.database.WordlistsDatabase
import io.cyanlab.wordlist.views.BackdropMenuContainer
import io.cyanlab.wordlist.views.ExpandingMenuContainer
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), DBHolder{

    var menuContainer: ExpandingMenuContainer? = null

    override var database: WordlistsDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alt_main)
        setSupportActionBar(main_toolbar)

        //backdrop.withToolbar(main_toolbar).build()

        menuContainer = BackdropMenuContainer(app_bar_frame, fore_frame, resources?.getDimension(R.dimen.shape_radius) ?: 0f)

        menuContainer?.listeners?.add(SimpleExpandingMenuListener())

        main_toolbar.setNavigationOnClickListener(SimpleMenuController(menuContainer))

        fore_frame.setOnClickListener(SimpleMenuCollapser(menuContainer))

        main_toolbar?.setOnClickListener(SimpleMenuExpander(menuContainer))
      
        supportFragmentManager.beginTransaction().apply {

            add(R.id.fore_frame, MainFragment())

            val mainMenuFragment = MainMenuFragment()
            mainMenuFragment.listener = SimpleMenuListener()

            add(R.id.app_bar_frame, mainMenuFragment)

        }.commitNowAllowingStateLoss()

        thread{

            database = Room.databaseBuilder(applicationContext, WordlistsDatabase::class.java, "base.db").build()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 0)
            return

        val file = data?.getStringExtra("file") ?: return

        val pdfManager: PDFManager = mPDFManager(mPDFCallback(this))

        pdfManager.startParsing(file)
    }


    inner class SimpleExpandingMenuListener: ExpandingMenuContainer.Listener {

        override fun onMenuAnimationStarted(isExpanded: Boolean) {

            val icon = resources?.getDrawable(when (isExpanded){

                true -> R.drawable.ic_close_cyan_900_24dp
                false -> R.drawable.ic_menu_cyan_900_24dp
            })

            supportActionBar?.setHomeAsUpIndicator(icon)
        }

    }

    inner class SimpleMenuListener: MainMenuFragment.Listener{

        override fun onPDFSelected() {

            menuContainer?.listeners?.add(object : ExpandingMenuContainer.Listener{

                override fun onMenuAnimationFinished(isExpanded: Boolean) {
                    val intent = Intent(this@MainActivity, FileManagerActivity::class.java)

                    menuContainer?.listeners?.remove(this)

                    startActivityForResult(intent, 0)
                }
            })

            menuContainer?.collapseMenu()
        }
    }

    override fun onBackPressed() {

        if (menuContainer?.isMenuExpanded == true){

            menuContainer?.collapseMenu()

            return
        }

        super.onBackPressed()
    }

}



