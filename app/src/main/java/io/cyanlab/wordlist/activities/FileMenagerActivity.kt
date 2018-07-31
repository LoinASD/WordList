package io.cyanlab.wordlist.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SimpleAdapter
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
import kotlinx.android.synthetic.main.activity_file_manager.*

class FileManagerActivity : AppCompatActivity() {

    private lateinit var files: Array<String>
    private var sa: SimpleAdapter? = null

    private val ROOT_PATH = Environment.getExternalStorageDirectory().absolutePath
    private var CURRENT_PATH = ROOT_PATH// = "/sdcard/storage/0/Download";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)
        //----------------------------------//

        file_list.choiceMode = ListView.CHOICE_MODE_SINGLE

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {

            Toast.makeText(this, "SD-card is not responding", Toast.LENGTH_SHORT).show()

            setResult(AppCompatActivity.RESULT_CANCELED)
            finish()

            return
        }

        CURRENT_PATH = Environment.getExternalStorageDirectory().absolutePath

        val dir = File(CURRENT_PATH)

        showDir(dir)

        val ib = this.findViewById<ImageButton>(R.id.file_back)

        val backBut = View.OnClickListener {

            if (CURRENT_PATH != ROOT_PATH) {

                val f = File(CURRENT_PATH)
                CURRENT_PATH = f.parent

                showDir(f.parentFile)

                return@OnClickListener
            }

            setResult(Activity.RESULT_CANCELED)

            finish()
        }

        ib.setOnClickListener(backBut)

        file_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val f = File(CURRENT_PATH, files[position])

            CURRENT_PATH = f.absolutePath

            if (f.isDirectory){

                showDir(f.absoluteFile)
                return@OnItemClickListener
            }

            val intent = Intent()
            intent.putExtra("file", f.absolutePath)
            setResult(AppCompatActivity.RESULT_OK, intent)
            finish()
        }
    }

    private fun showDir(dir: File) {

        file_head_text.text =
                if (dir.absolutePath == ROOT_PATH)
                    "Home"
                else
                    dir.name

        file_back?.setImageDrawable(getDrawable( if (dir.absolutePath == ROOT_PATH)
            R.drawable.ic_arrow_back
        else
            R.drawable.up))

        files = dir.list() ?: return

        Arrays.sort(files, SortedByName())

        val usefulFiles = ArrayList<String>()

        val data = ArrayList<Map<String, Any>>()

        var m: MutableMap<String, Any>
        var f: File

        for (file in files) {

            var img = 0

            f = File(dir, file)

            if (file.startsWith("."))
                continue

            if (f.isDirectory){

                img = R.drawable.folder

                usefulFiles.add(file)

            } else {

                if (!file.endsWith(".pdf"))
                    continue

                img = R.drawable.pdf

                usefulFiles.add(file)
            }

            m = HashMap()

            m[ATTRIBUTE_NAME_TEXT] = file
            m[ATTRIBUTE_NAME_IMAGE] = img

            data.add(m)
        }

        usefulFiles.toArray(this@FileManagerActivity.files)

        val from = arrayOf(ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE)
        val to = intArrayOf(R.id.file_name, R.id.file_image)

        sa = SimpleAdapter(this, data, R.layout.file_line, from, to)

        file_list.adapter = sa
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

        private const val ATTRIBUTE_NAME_TEXT = "text"
        private const val ATTRIBUTE_NAME_IMAGE = "image"

    }


}