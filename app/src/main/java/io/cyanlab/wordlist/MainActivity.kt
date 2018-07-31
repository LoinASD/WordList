package io.cyanlab.wordlist

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import io.cyanlab.wordlist.activities.MainFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        backdrop.withToolbar(main_toolbar).build()

        supportFragmentManager.beginTransaction().add(R.id.frame, MainFragment()).commitNowAllowingStateLoss()
    }

/*    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }*/
}
