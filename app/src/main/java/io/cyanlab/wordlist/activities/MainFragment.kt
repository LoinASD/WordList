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

    companion object {

        const val TAG_LISTS = "Lists"
        const val TAG_LINES = "Lines"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v = inflater.inflate(R.layout.main_fragment_layout, container, false)
        return v
    }



}