package io.cyanlab.wordlist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import io.cyanlab.wordlist.activities.FileManagerActivity
import kotlinx.android.synthetic.main.alt_main.*
import io.cyanlab.wordlist.activities.MainFragment
import io.cyanlab.wordlist.controllers.PDFManager
import io.cyanlab.wordlist.controllers.mPDFManager
import io.cyanlab.wordlist.models.database.DBHolder
import io.cyanlab.wordlist.models.database.WordlistsDatabase
import io.cyanlab.wordlist.models.pdf.Node
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

        menuContainer?.listeners?.add(SimpleMenuListener())

        main_toolbar.setNavigationOnClickListener {

            val intent = Intent(this, FileManagerActivity::class.java)

            startActivityForResult(intent, 0)

            /*it.background = null

            val isExpanded = menuContainer?.isMenuExpanded ?: return@setNavigationOnClickListener
          
            if (isExpanded)

                menuContainer?.collapseMenu()
            else
                menuContainer?.expandMenu()*/
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
      
        supportFragmentManager.beginTransaction().add(R.id.fore_frame, MainFragment()).commitNowAllowingStateLoss()

        thread{

            database = Room.databaseBuilder(applicationContext, WordlistsDatabase::class.java, "base.db").build()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 0)
            return

        val file = data?.getStringExtra("file") ?: return

        val pdfManager: PDFManager = mPDFManager(object : PDFManager.Callback{

            override fun onParserStarted() {

                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Parser started", Toast.LENGTH_SHORT)?.show()
                }
            }

            override fun onDelegatorStarted() {
                runOnUiThread{

                    Toast.makeText(this@MainActivity, "Delegator started", Toast.LENGTH_SHORT)?.show()
                }
            }

            override fun onParserFinished() {
                runOnUiThread{

                    Toast.makeText(this@MainActivity, "Parser finished", Toast.LENGTH_SHORT)?.show()
                }
            }

            override fun onDelegatorFinished(wlName: String?, nodes: ArrayList<Node>) {

                runOnUiThread { Toast.makeText(this@MainActivity, "Delegator finished", Toast.LENGTH_SHORT)?.show() }

                saveNodes(wlName, nodes)
            }

            override fun onError(what: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error occurred: $what", Toast.LENGTH_SHORT)?.show()
                }
            }

        })

        pdfManager.startParsing(file)
    }


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



