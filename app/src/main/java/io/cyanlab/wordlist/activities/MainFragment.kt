package io.cyanlab.wordlist.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.cyanlab.wordlist.R
import io.cyanlab.wordlist.models.pdf.Node
import io.cyanlab.wordlist.models.pdf.WordList

class MainFragment : Fragment() {



    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v = inflater.inflate(R.layout.wordlist_panel_layout, container, false)
        return v
    }

    override fun onStop() {
        super.onStop()
    }

    private inner class ListHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var namePlace: TextView

        internal var progressBar: ProgressBar

        //internal var progress: TextView

        init {
            namePlace = itemView.findViewById(R.id.wordlist_name)
            progressBar = itemView.findViewById(R.id.list_progress)
//            progress = itemView.findViewById(R.id.percents)
        }
    }


}