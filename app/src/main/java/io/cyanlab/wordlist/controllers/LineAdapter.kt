package io.cyanlab.wordlist.controllers

import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.cyanlab.wordlist.R
import io.cyanlab.wordlist.models.pdf.Node
import android.view.LayoutInflater



class LineAdapter(var linelist: MutableList<Node>) : RecyclerView.Adapter<LineAdapter.LineHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineHolder {
        return LineHolder(LayoutInflater.from(parent.context).inflate(R.layout.wordlist_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return linelist.size
    }

    override fun onBindViewHolder(holder: LineHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    inner class LineHolder(val view: View): RecyclerView.ViewHolder(view) {


        internal var firstText: TextView = view.findViewById(R.id.first_text)

        internal var secondYext: TextView = view.findViewById(R.id.second_text)
    }
}