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

        internal var progress: TextView

        internal var listLayout: LinearLayout

        init {
            namePlace = itemView.findViewById(R.id.name_line)
            progressBar = itemView.findViewById(R.id.progressBar2)
            progress = itemView.findViewById(R.id.percents)
            listLayout = itemView.findViewById(R.id.list_layout)
        }
    }

    private inner class WLAdapter private constructor(@param:LayoutRes @field:LayoutRes
                                                      private val resource: Int) : RecyclerView.Adapter() {
        internal var list: WordList

        internal var lists: List<WordList>? = null

        internal var nodes: List<Node>? = null

        val itemCount: Int
            get() =
                if (MODE == SHOW_WL && lists != null || MODE == SHOW_LINES && nodes != null) if (MODE == SHOW_WL) lists!!.size else nodes!!.size else 0

        private fun colorLines() {
            for (i in 0 until main.getChildCount()) {
                if (i % 2 == 0) {
                    main.getChildAt(i).setBackgroundColor(resources.getColor(R.color.colorAccentLowAlpha))
                } else
                    main.getChildAt(i).setBackgroundColor(resources.getColor(R.color.colorWhite))
            }
        }

        internal fun loadFromDB() {

            var load = Thread()

            when (MODE) {

                SHOW_WL -> {
                    load = Thread(Runnable { lists = MainActivity.database.listDao().getAllLists() })

                }
                SHOW_LINES -> {
                    load = Thread(Runnable { nodes = MainActivity.database.nodeDao().getNodes(LIST_NAME) })
                }
            }
            try {
                if (MODE == SHOW_LINES) {
                    val loadName = Thread(Runnable { list = MainActivity.database.listDao().getWordlist(LIST_NAME) })
                    loadName.start()
                    loadName.join()
                    changeHeader()
                }
                load.start()
                load.join()
                adapter.notifyDataSetChanged()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }


        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(resource, parent, false)

            val holder: RecyclerView.ViewHolder

            when (resource) {
                R.layout.list_line -> {
                    holder = ListHolder(view)
                }
                R.layout.simple_line -> {
                    holder = NodeHolder(view)
                }
                else -> holder = NodeHolder(view)
            }

            return holder
        }

        fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (MODE) {
                SHOW_WL -> {
                    (holder as ListHolder).namePlace.setText(lists!![position].getWlName())
                    (holder as ListHolder).progressBar.setMax(lists!![position].maxWeight)
                    (holder as ListHolder).progressBar.setProgress(lists!![position].maxWeight - lists!![position].currentWeight)
                    val prog = (lists!![position].maxWeight - lists!![position].currentWeight) * 100 / (if (lists!![position].maxWeight !== 0) lists!![position].maxWeight else 1) + "%"
                    (holder as ListHolder).progress.setText(prog)


                    (holder as ListHolder).listLayout.setOnClickListener(View.OnClickListener { view ->
                        val wlName = lists!![position].getWlName()
                        listener.onListSelected(wlName, view)
                    })
                }
                SHOW_LINES -> {

                    val prim = nodes!![position].getPrimText()
                    val trans = nodes!![position].getTransText()

                    (holder as NodeHolder).primTV.setText(prim)
                    (holder as NodeHolder).transTV.setText(trans)
                    (holder as NodeHolder).lineLayout.setOnLongClickListener(View.OnLongClickListener {
                        bsManager.expandBottomSheet(nodes!![position])

                        true
                    })
                }
            }
        }

        fun getItemId(i: Int): Long {
            return i.toLong()
        }

    }
}