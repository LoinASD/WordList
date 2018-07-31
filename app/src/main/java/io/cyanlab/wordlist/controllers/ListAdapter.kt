package io.cyanlab.wordlist.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.cyanlab.wordlist.R
import io.cyanlab.wordlist.models.pdf.WordList

class ListAdapter(var wordlist: MutableList<WordList>) : RecyclerView.Adapter<ListAdapter.ListHolder>() {



    override fun onBindViewHolder(holder: ListHolder, position: Int) {

                holder.namePlace.text = wordlist[position].name
                holder.progressBar.max = wordlist[position].maxWeight
                holder.progressBar.progress = wordlist[position].maxWeight - wordlist[position].currentWeight
                holder.firstLang.text = wordlist[position].firstLang
                holder.secondLang.text = wordlist[position].secondLang

/*                holder.view.setOnClickListener { view ->
                    val name = wordlist[position].name
                    listener.onListSelected(name, view)
                }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return ListHolder(LayoutInflater.from(parent.context).inflate(R.layout.wordlist_panel_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return wordlist.size
    }

    inner class ListHolder(val view: View): RecyclerView.ViewHolder(view) {


        internal var namePlace: TextView = view.findViewById(R.id.wordlist_name)

        internal var progressBar: ProgressBar = view.findViewById(R.id.list_progress)

        internal var firstLang: TextView = view.findViewById(R.id.wordlist_first_lang)

        internal var secondLang: TextView = view.findViewById(R.id.wordlist_second_lang)

    }
}