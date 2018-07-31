package io.cyanlab.wordlist.activities.MainActivityStuff

import android.widget.Toast
import io.cyanlab.wordlist.MainActivity
import io.cyanlab.wordlist.controllers.PDFManager
import io.cyanlab.wordlist.models.pdf.Node

class mPDFCallback(val activity: MainActivity) : PDFManager.Callback{

    override fun onParserStarted() {

        activity.runOnUiThread {
            Toast.makeText(activity, "Parser started", Toast.LENGTH_SHORT)?.show()
        }
    }

    override fun onDelegatorStarted() {
        activity.runOnUiThread{

            Toast.makeText(activity, "Delegator started", Toast.LENGTH_SHORT)?.show()
        }
    }

    override fun onParserFinished() {
        activity.runOnUiThread{

            Toast.makeText(activity, "Parser finished", Toast.LENGTH_SHORT)?.show()
        }
    }

    override fun onDelegatorFinished(wlName: String?, nodes: ArrayList<Node>) {

        activity.runOnUiThread { Toast.makeText(activity, "Delegator finished", Toast.LENGTH_SHORT)?.show() }

        activity.saveNodes(wlName, nodes)
    }

    override fun onError(what: String) {

        activity.runOnUiThread {

            Toast.makeText(activity, "Error occurred: $what", Toast.LENGTH_SHORT)?.show()
        }
    }
}