package io.cyanlab.wordlist.controllers

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import io.cyanlab.wordlist.MainActivity
import io.cyanlab.wordlist.models.pdf.Delegator
import io.cyanlab.wordlist.models.pdf.Node
import io.cyanlab.wordlist.models.pdf.PDFParser
import java.io.IOException
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

interface PDFManager{

    fun startParsing(file: String)

    val callback: PDFManager.Callback?

    interface Callback{

        fun onParserStarted()

        fun onDelegatorStarted()

        fun onParserFinished()

        fun onDelegatorFinished(wlName: String?, nodes: ArrayList<Node>)

        fun onError(what: String)
    }
}

class mPDFManager(override val callback: PDFManager.Callback? = null): PDFManager{

    companion object {


    }

    //-----CODE FROM MAIN ACTIVITY----------------

    override fun startParsing(file: String) {

        try {

            val pout = PipedOutputStream()
            val pin = PipedInputStream(pout)

            val parser = thread {

                PDFParser(LightParserCallback()).parsePdf(file, pout)
            }

            val extractor = thread {

                Delegator(LigthDelegatorCallback()).extract(pin)
            }

            parser.priority = Thread.MAX_PRIORITY
            parser.start()
            extractor.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

//------------------------------------------------------

    /*@Volatile
    internal var wlName: String? = null


    fun handleMessage(msg: Message) {

        //val activity = wrActivity.get() ?: return
        if (msg.what == HANDLE_MESSAGE_PARSED) {
            parser = true
            //((TextView)activity.findViewById(R.id.pbText)).setText("Extrackting Text...");
        }
        if (msg.what == HANDLE_MESSAGE_EXTRACTED) {
            extractor = true
            //wlName = msg.data.getString(WL_NAME)


        }
        if (msg.what == HANDLE_MESSAGE_EXISTS) {
            parser = false
            extractor = false

            //Toast.makeText(activity, "Wordlist with equal name already exists", Toast.LENGTH_SHORT).show()

            // activity.findViewById(R.id.fragment).setVisibility(View.VISIBLE);
           // activity.progBarLayout.setVisibility(View.INVISIBLE)
        }

        if (msg.what == HANDLE_MESSAGE_NOT_EXTRACTED) {
            parser = false
            extractor = false

           //Toast.makeText(activity, "No dictionary found", Toast.LENGTH_SHORT).show()

            //activity.findViewById(R.id.fragment).setVisibility(View.VISIBLE);
            //activity.progBarLayout.setVisibility(View.INVISIBLE)
        }

        if (parser && extractor) {

            parser = false
            extractor = false

            //LIST_NAME = wlName

            *//*Toast.makeText(activity, "Wordlist $LIST_NAME successfully extracted", Toast.LENGTH_LONG).show()
            activity.loadLines()
            (activity.lines as ShowFragment).adapterLoadData()


            //activity.findViewById(R.id.fragment).setVisibility(View.VISIBLE);
            activity.progBarLayout.setVisibility(View.INVISIBLE)*//*


        }
    }*/

    inner class LightParserCallback: PDFParser.Callback{

        override fun onStart() {

            callback?.onParserStarted()
        }

        override fun onStreamWritten() {}

        override fun onFinish() {

            callback?.onParserFinished()
        }

        override fun onErrorOccurred(what: String) {

            callback?.onError(what)
        }

    }

    inner class LigthDelegatorCallback: Delegator.Callback{

        override fun onStart() {

            callback?.onDelegatorStarted()
        }

        override fun onFinish(wlName: String?, nodes: ArrayList<Node>) {

            callback?.onDelegatorFinished(wlName, nodes)
        }

        override fun onDictionaryFound() {}

        override fun onConvertingStart() {}

        override fun onErrorOccured(what: String) {

            callback?.onError(what)
        }


    }

/*    object StaticHandler(activity: MainActivity): MainHandler() {

        private val wrActivity: WeakReference<MainActivity> = WeakReference(activity)
    }*/
}