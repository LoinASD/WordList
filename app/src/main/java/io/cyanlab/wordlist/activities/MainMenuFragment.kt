package io.cyanlab.wordlist.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.cyanlab.wordlist.R
import kotlinx.android.synthetic.main.fragment_main_menu.view.*

class MainMenuFragment: Fragment(), View.OnClickListener {

    interface Listener{

        fun onListsSelected(){}

        fun onSettingsSelected(){}

        fun onPDFSelected(){}
    }

    var listener: MainMenuFragment.Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val v = inflater.inflate(R.layout.fragment_main_menu, container, false)

        return v
    }

    override fun onStart() {
        super.onStart()

        view?.menu_lists?.setOnClickListener(this)
        view?.menu_settings?.setOnClickListener(this)
        view?.menu_load_pdf?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0?.id){

            R.id.menu_lists -> listener?.onListsSelected()

            R.id.menu_settings -> listener?.onSettingsSelected()

            R.id.menu_load_pdf -> listener?.onPDFSelected()
        }
    }

    override fun onStop() {
        super.onStop()

        view?.menu_lists?.setOnClickListener(null)
        view?.menu_settings?.setOnClickListener(null)
        view?.menu_load_pdf?.setOnClickListener(null)
    }
}