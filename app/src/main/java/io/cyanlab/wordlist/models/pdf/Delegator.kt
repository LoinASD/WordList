package io.cyanlab.wordlist.models.pdf

import android.os.Bundle
import android.os.Message

import java.io.IOException
import java.io.PipedInputStream
import java.util.ArrayList
import java.util.logging.Logger


class Delegator {

    private var ch: Int = 0
    private var io: PipedInputStream? = null
    private var converter: CharConverter? = null
    private var newWlName: String? = null
    private var isExists: Boolean = false
    //private StringBuilder text;
    private var range: Int = 0
    private var progress: Int = 0
    private var nodes: ArrayList<Node>? = null
    private var waitingNode: Node? = null
    private var waitingNodeLang: Lang? = null
    private var extractor: TextExtractor? = null

    private val COINS_TO_SET_X = 10

    private val engX: Double = 0.toDouble()

    private var isRusXSet: Boolean = false
    private var rusX: Double = 0.toDouble()
    private var rusXCoins: Int = 0
    private var rusXErr: Int = 0

    private fun updateProgress() {
        progress++
    }

    /**
     * Main extraction method. Extracts text from @io
     *
     * @param io
     */


    fun extract(io: PipedInputStream) {

        val startTime = System.currentTimeMillis()
        this.io = io
        nodes = ArrayList()
        extractor = TextExtractor()
        converter = CharConverter()
        gotDictionary = false
        isExists = false

        try {
            parse()
            if (gotDictionary && !isExists) {
                nodeCollect()
            } else if (isExists) {
                MainActivity.h.sendEmptyMessage(MainActivity.HANDLE_MESSAGE_EXISTS)
            } else {
                MainActivity.h.sendEmptyMessage(MainActivity.HANDLE_MESSAGE_NOT_EXTRACTED)
            }

        } catch (e: IOException) {
            e.printStackTrace()

        }

        log.warning("Delegator works in ms:" + (System.currentTimeMillis() - startTime))
        //System.out.printf("Delegator works %d ms", System.currentTimeMillis() - startTime);
    }

    /**
     * Finds BT-ET blocks and sends contents to @TextExtractor, also searches for CMap
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun parse() {

        while (ch != -1) {
            ch = io!!.read()

            if (ch == 'B'.toInt()) {
                ch = io!!.read()
                if (ch == 'T'.toInt()) {
                    extractor!!.textToken()
                }
            } else if (ch == 'b'.toInt()) {
                ch = io!!.read()
                if (ch == 'e'.toInt()) {
                    val line = readLine()
                    if (line == "gincmap")
                        parseCMap()
                }
            }
        }
    }

    /**
     * Parses CMap and fills @CharConverters @Ranges
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun parseCMap() {

        /**
         * This Method parses a charMap in the end of PDF file
         */

        var chars: Array<String>
        var count: Int
        while (ch != -1) {
            lineStr = readLine()
            if (lineStr!!.endsWith("begincodespacerange")) {

                // range looks like <0000> <FFFF>
                range = readLine().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].length - 2
            }

            if (lineStr!!.endsWith("beginbfchar")) {
                count = Integer.parseInt("" + lineStr!![0])
                for (i in count downTo 1) {
                    lineStr = readLine()
                    chars = lineStr!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val c1 = Integer.parseInt(chars[0].substring(1, range + 1), 16)
                    val c2 = Integer.parseInt(chars[1].substring(1, range + 1), 16)
                    converter!!.addNewRange(c1, c1, c2)
                }
            }
            if (lineStr!!.endsWith("beginbfrange")) {
                count = Integer.parseInt("" + lineStr!![0])
                for (i in count downTo 1) {
                    lineStr = readLine()
                    chars = lineStr!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val strC1 = chars[0].substring(1, range + 1)
                    val strC2 = chars[1].substring(1, range + 1)
                    //System.out.printf("%nc1 = %s; c2 = %s%n", strC1, strC2);
                    val c1 = Integer.parseInt(strC1, 16)
                    val c2 = Integer.parseInt(strC2, 16)
                    //System.out.printf("int: c1 = %d; c2 = %d", c1, c2);
                    if (lineStr!!.contains("[")) {
                        val arr = lineStr!!.substring(
                                lineStr!!.indexOf('[') + 1, lineStr!!.length - 1).split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var c: Int
                        var dif = c1
                        for (s in arr) {
                            c = Integer.parseInt(s.substring(1, range + 1), 16)
                            converter!!.addNewRange(dif, dif, c)
                            dif++
                        }
                    } else {
                        val strC3 = chars[2].substring(1, range + 1)
                        val c3 = Integer.parseInt(strC3, 16)
                        //System.out.printf("%nc3 = %s; int3: %d%n", strC3, c3);
                        converter!!.addNewRange(c1, c2, c3)
                    }
                }
            }

            if (lineStr == "endcmap") {
                gotDictionary = true
                break
            }
        }

    }

    /**
     * Reads line from @inputStream. Lightweight realization of @Readers @readLine;
     * @return Line
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readLine(): String {

        /**
         * This is lightweight realisation of Reader`s readline;
         * Checks inputStream in lines
         */

        val l = StringBuilder()
        while (true) {
            if (io!!.available() != 0) {
                while (ch != -1) {
                    ch = io!!.read()
                    if (ch == -1) return ""
                    if (ch.toChar() != '\n') {
                        l.append(ch.toChar())
                    } else {
                        if (l.length > 0 && l[l.length - 1] == '\r')
                            l.deleteCharAt(l.length - 1)
                        return l.toString()
                    }
                }
            }
        }

    }

    /**
     * Method, called when the whole text has been read.
     * Converts text within Nodes with CharConverter using personal thread for each Node
     */
    private fun nodeCollect() {

        /*
          This method take all prims, convert text and sort
         */

        nodes!!.add(waitingNode)

        val list = WordList()
        list.setWlName(newWlName)
        list.currentWeight = nodes!!.size * ShowFragment.RIGHT_ANSWERS_TO_COMPLETE
        list.maxWeight = list.currentWeight

        val group = ThreadGroup("Converting")

        for (node in nodes!!) {

            val thread = Thread(group, Runnable { node.convertText(converter) })

            thread.start()
        }

        while (group.activeCount() > 0) {
        }

        MainActivity.database.nodeDao().insertAll(nodes)
        MainActivity.database.listDao().insertList(list)


        val message = Message()

        message.what = MainActivity.HANDLE_MESSAGE_EXTRACTED

        val data = Bundle()

        data.putString(MainActivity.WL_NAME, newWlName)

        message.data = data

        MainActivity.h.sendMessage(message)


    }


    /**
     * Class that extracts text from InputStream and fills Nodes with it
     */
    private inner class TextExtractor {

        internal var x: Double = 0.toDouble()

        /**
         * Special number to describe the error of X arrangement
         */
        internal val xArea = 20

        internal var textBuffer = ArrayList<TextPlusX>()

        /**
         * Keeps recently read texts language;
         */
        private var curLang: Lang? = null

        /**
         * Extracts texts X coordinate
         * @return
         */
        private// Get current coordinates and pass it to node
        val nodeX: Double
            get() {

                try {
                    ch = io!!.read()
                    while (ch.toChar() != '[') {
                        lineStr = readLine()
                        if (lineStr!!.endsWith("Tm")) {
                            val cord = lineStr!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            return java.lang.Double.parseDouble(cord[4])
                        }
                        ch = io!!.read()
                    }
                } catch (e: IOException) {
                    return -1.0
                }

                return -1.0
            }

        /**
         * This method finds proper Translation X.
         * While proper X is not found, text and its X are stored in @textBuffer.
         */
        private fun handleX(X: Double) {
            if (rusX != 0.0) {
                if (rusX == X) {
                    rusXCoins++
                    rusXErr--
                } else {
                    rusXErr++
                }
                if (rusXCoins == COINS_TO_SET_X) {
                    isRusXSet = true
                    loadBuffer()
                }
                if (rusXErr == COINS_TO_SET_X) {
                    rusXErr = 0
                    rusXCoins = 0
                    rusX = X
                }
            } else {
                rusX = X
            }
        }

        /**
         * When proper X is found, creates nodes that were in buffer.
         */
        private fun loadBuffer() {
            for (textPlusX in textBuffer) {

                if (waitingNode == null) {

                    waitingNode = Node()
                    waitingNode!!.setWlName(newWlName)

                    waitingNode!!.setWeight(ShowFragment.RIGHT_ANSWERS_TO_COMPLETE)

                    waitingNode!!.setPrimText(textPlusX.text)
                    waitingNodeLang = Lang.ENG

                    continue
                }

                delegateText(textPlusX.text, textPlusX.x)

            }


        }

        /**
         * Extracts text from IS
         * Sets current Text Language @curLang
         * @return @String extracted text
         */
        @Throws(IOException::class)
        private fun extractRawText(): String {

            ch = io!!.read()
            while (ch.toChar() != '[') {
                lineStr = readLine()
                ch = io!!.read()
            }


            val text = StringBuilder()
            while (ch.toChar() != ']') {
                while (ch.toChar() != '(' && ch.toChar() != '<') {
                    // skip all text out brackets
                    ch = io!!.read()
                }
                if (ch.toChar() == '<') {
                    curLang = Lang.BRACE
                    text.append(ch.toChar())
                    while (ch.toChar() != '>') {
                        ch = io!!.read()
                        text.append(ch.toChar())
                    }
                } else {
                    curLang = Lang.UNDEFINED
                    ch = io!!.read()
                    while (ch.toChar() != ')') {

                        if (curLang !== Lang.ENG && LangChecker.langCheck(ch.toChar()) === Lang.ENG)
                            curLang = Lang.ENG

                        if (curLang !== Lang.ENG && LangChecker.langCheck(ch.toChar()) === Lang.NUM || ch.toChar() == '.') {
                            ch = io!!.read()
                            continue
                        }

                        text.append(if (ch != 47) ch.toChar() else ',')
                        ch = io!!.read()
                    }
                }
                ch = io!!.read()
            }

            return text.toString()

        }

        /**
         * Calls all other methods of this class
         *
         * The result of its work is a Node, filled with raw text
         */
        @Throws(IOException::class)
        private fun textToken() {

            /**
             * This Method extract text and passes it to Node
             */

            //Node node = new Node();
            x = nodeX

            val text = extractRawText()

            if (newWlName == null) {
                newWlName = text.trim { it <= ' ' }.replace(" ".toRegex(), "_").replace(":".toRegex(), "")

                for (s in MainActivity.database.listDao().loadNames()) {
                    if (newWlName == s) {
                        isExists = true
                    }
                }
            } else {

                val nodeLang = curLang

                if (!isRusXSet && nodeLang === Lang.BRACE) {
                    handleX(x)
                }

                if (!isRusXSet) {
                    textBuffer.add(TextPlusX(text, x, nodeLang))

                } else {
                    delegateText(text, x)
                }


            }


        }

        /**
         * In most common cases creates a Node filled with text
         *
         *
         * Also fills unfinished Nodes with translation texts
         *
         * @param text
         * @param x
         */
        private fun delegateText(text: String, x: Double) {

            val nodeLang = if (x >= engX && x < rusX - xArea) Lang.ENG else Lang.RUS

            if (nodeLang === (if (waitingNodeLang === Lang.ENG) Lang.RUS else Lang.ENG)) {

                waitingNode!!.setWlName(newWlName)
                if (nodeLang === Lang.ENG) {

                    nodes!!.add(waitingNode)

                    waitingNode = Node()

                    waitingNode!!.setWeight(ShowFragment.RIGHT_ANSWERS_TO_COMPLETE)

                    waitingNode!!.setPrimText(text)

                } else
                    waitingNode!!.setTransText(text)
                waitingNodeLang = nodeLang

            } else {
                if (waitingNodeLang === Lang.ENG)
                    waitingNode!!.setPrimText(waitingNode!!.getPrimText().concat(text))
                else
                    waitingNode!!.setTransText(waitingNode!!.getTransText().concat(text))
            }
        }
    }

    /**
     * Class, used to store info about buffered text in @TextExtractor
     */
    private inner class TextPlusX internal constructor(internal val text: String, internal val x: Double, internal val textLang: Lang)

    companion object {
        private var lineStr: String? = null
        private var gotDictionary: Boolean = false

        val log = Logger.getLogger(Delegator::class.java.name)
    }
}
