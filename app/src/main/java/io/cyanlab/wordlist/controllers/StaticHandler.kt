package io.cyanlab.wordlist.controllers

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import io.cyanlab.wordlist.MainActivity
import java.lang.ref.WeakReference

open class MainHandler  : Handler() {

    companion object {
        internal const val HANDLE_MESSAGE_PARSED = 1
        const val HANDLE_MESSAGE_EXTRACTED = 2
        const val HANDLE_MESSAGE_NOT_EXTRACTED = 4
        internal const val HANDLE_MESSAGE_DELETED = 5
        const val HANDLE_MESSAGE_EXISTS = 6

        private var player = StaticHandler()

        fun get(): StaticHandler{
            return player
        }
    }



    @Volatile
    internal var parser: Boolean = false
    @Volatile
    internal var extractor: Boolean = false
    @Volatile
    internal var wlName: String? = null


    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val activity = wrActivity.get() ?: return
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

            Toast.makeText(activity, "Wordlist with equal name already exists", Toast.LENGTH_SHORT).show()

            // activity.findViewById(R.id.fragment).setVisibility(View.VISIBLE);
           // activity.progBarLayout.setVisibility(View.INVISIBLE)
        }

        if (msg.what == HANDLE_MESSAGE_NOT_EXTRACTED) {
            parser = false
            extractor = false

            Toast.makeText(activity, "No dictionary found", Toast.LENGTH_SHORT).show()

            //activity.findViewById(R.id.fragment).setVisibility(View.VISIBLE);
            //activity.progBarLayout.setVisibility(View.INVISIBLE)
        }

        if (parser && extractor) {

            parser = false
            extractor = false

            //LIST_NAME = wlName

            /*Toast.makeText(activity, "Wordlist $LIST_NAME successfully extracted", Toast.LENGTH_LONG).show()
            activity.loadLines()
            (activity.lines as ShowFragment).adapterLoadData()


            //activity.findViewById(R.id.fragment).setVisibility(View.VISIBLE);
            activity.progBarLayout.setVisibility(View.INVISIBLE)*/


        }
    }

    object StaticHandler(activity: MainActivity): MainHandler() {

        private val wrActivity: WeakReference<MainActivity> = WeakReference(activity)
    }
}