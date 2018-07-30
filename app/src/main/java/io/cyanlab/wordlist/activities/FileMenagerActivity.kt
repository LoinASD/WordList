package io.cyanlab.wordlist.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast

import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.HashMap
import java.util.logging.Level
import java.util.logging.Logger

import androidx.appcompat.app.AppCompatActivity
import io.cyanlab.wordlist.R

class FileManagerActivity : AppCompatActivity(), View.OnClickListener {
    internal var logger = Logger.getLogger("FM")
    internal lateinit var wayLayout: LinearLayout
    internal lateinit var lw: ListView
    internal lateinit var way: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)
        //----------------------------------//
        wayLayout = findViewById(R.id.wayLayout)
        way = findViewById(R.id.wayTextView)
        lw = findViewById(R.id.treeListView)
        lw.choiceMode = ListView.CHOICE_MODE_SINGLE

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(this, "SD-card is not responding", Toast.LENGTH_SHORT).show()
            setResult(AppCompatActivity.RESULT_CANCELED)
            finish()
            return
        }

        CURRENT_PATH = Environment.getExternalStorageDirectory().absolutePath
        dir = File(CURRENT_PATH!!)
        //-----------------------------------//
        showDir(dir!!)
        val ib = this.findViewById<ImageButton>(R.id.backButton)
        val backBut = View.OnClickListener {
            if (CURRENT_PATH != ROOT_PATH) {
                val f = File(CURRENT_PATH!!)
                CURRENT_PATH = f.parent
                logger.log(Level.INFO, CURRENT_PATH)
                showDir(f.parentFile)
            }
        }
        ib.setOnClickListener(backBut)
        lw.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val f = File(CURRENT_PATH, files!![position])
            CURRENT_PATH = f.absolutePath
            logger.log(Level.INFO, CURRENT_PATH)
            if (f.isDirectory)
                showDir(f.absoluteFile)
            else {
                val intent = Intent()
                intent.putExtra("file", f.absolutePath)
                setResult(AppCompatActivity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun showDir(dir: File) {
        way.text = dir.name
        files = dir.list()
        if (files != null) {
            Arrays.sort(files!!, SortedByName())
            val usefulFiles = ArrayList<String>()
            data = ArrayList()
            var m: MutableMap<String, Any>
            var f: File
            for (file in files!!) {
                f = File(dir, file)
                if (f.isDirectory)
                    if (!file.startsWith(".")) {
                        img = R.drawable.folder
                        usefulFiles.add(file)
                    } else {
                        if (file.endsWith(".pdf")) {
                            img = R.drawable.pdf
                            usefulFiles.add(file)
                        }
                    }
                m = HashMap()
                m[ATTRIBUTE_NAME_TEXT] = file
                m[ATTRIBUTE_NAME_IMAGE] = img
                data!!.add(m)
            }
            usefulFiles.toTypedArray()
            val from = arrayOf(ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE)
            val to = intArrayOf(R.id.fileTextView, R.id.fileImageView)
            sa = SimpleAdapter(this, data, R.layout.file_line, from, to)
            lw.adapter = sa
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.wayTextView) {
            val f = File(CURRENT_PATH!!)
            CURRENT_PATH = f.parent
            showDir(f.parentFile)
        }
    }

    //-------Sorts the list-------

    internal inner class SortedByName : Comparator<String> {
        override fun compare(o1: String, o2: String): Int {
            var str1 = o1
            var str2 = o2
            str1 = str1.toUpperCase()
            str2 = str2.toUpperCase()
            return str1.compareTo(str2)
        }
    }

    companion object {

        private var dir: File? = null
        private var data: ArrayList<Map<String, Any>>? = null
        private var files: Array<String>? = null
        private var img: Int = 0
        private var sa: SimpleAdapter? = null
        private val ATTRIBUTE_NAME_TEXT = "text"
        private val ATTRIBUTE_NAME_IMAGE = "image"
        private var CURRENT_PATH: String? = null// = "/sdcard/storage/0/Download";
        private val ROOT_PATH = Environment.getExternalStorageDirectory().absolutePath
    }


}