package io.cyanlab.wordlist.models.pdf

import java.io.*
import java.util.logging.Logger
import java.util.zip.DataFormatException
import java.util.zip.Inflater



class PDFParser() {


    private val log = Logger.getLogger(PDFParser::class.java.name)

    private lateinit var outputStream: OutputStream
    private lateinit var decoder: Thread

    companion object {
        const val DECODE_EXEPTION: Int = 2
        const val OK_CODE: Int = 1

        private var cc: Char = ' '
        private const val markerStream = "stream"
        private const val markerLength = "Length"


        /**
         * Возвращает true когда нашел и false когда не нашел @marker в @inputStream
         *
         * @param inputStream - входной поток
         * @param marker      - Маркер
         * @return true когда нашел и false когда не нашел @marker
         */

        @Throws(IOException::class)
        private fun search4Marker(inputStream: InputStream, marker: String): Boolean {
            for (i in 1 until marker.length) {
                cc = inputStream.read().toChar()
                if (cc != marker[i]) {
                    return false
                }
            }
            return true
        }
    }


    fun parsePdfFile(file: File) : Int {


        return OK_CODE
    }

    /**
     * Парсит все в этой жизни из файла @file, пишет в @out поток.
     *
     * @param file - файл для чтения
     * @param out - поток для записи
     */
    fun parsePdf(file: String, out: PipedOutputStream) {
        try {
            outputStream = out
            val bufInput = BufferedInputStream(FileInputStream(file), 2048)
            val startTime = System.currentTimeMillis()
            cc = bufInput.read().toChar()
            while (bufInput.available() != 0) {
                cc = bufInput.read().toChar()
                var streamLength = 0
                var isFonts = false
                while (cc != '>' && !isFonts && bufInput.available() > 0) {
                    cc = bufInput.read().toChar()
                    if (cc == markerLength[0]) {
                        val isLength = search4Marker(bufInput, markerLength)
                        if (isLength) {
                            val res = StringBuilder()
                            bufInput.read()
                            cc = bufInput.read().toChar()
                            while (cc != '>' && cc != '/') {
                                res.append(cc)
                                cc = bufInput.read().toChar()
                            }
                            try {
                                streamLength = Integer.parseInt(res.toString())
                            } catch (e: NumberFormatException) {
                                isFonts = true
                            }

                            if (cc == '/') {
                                bufInput.skip(streamLength.toLong())
                                continue
                            }
                        }
                    }
                    if (cc == '>') cc = bufInput.read().toChar()
                }

                if (streamLength > 0) {
                    var isStream = false
                    while (!isStream && bufInput.available() > 0) {
                        if (cc == markerStream[0]) {
                            isStream = search4Marker(bufInput, markerStream)
                        }
                        if (!isStream) cc = bufInput.read().toChar()
                    }
                    if (isStream) {
                        bufInput.read()
                        bufInput.read()
                        println(streamLength)
                        //FileOutputStream fOS = new FileOutputStream("C:/Android/WH"+ streamLength+".txt");
                        try {
                            if (decoder != null) {
                                decoder!!.join()
                            }
                            decode(streamLength, bufInput)
                        } catch (e: DataFormatException) {
                            println("Ошибка расшифровки GZIPa")
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            log.warning("Ошибка расшифровки GZIPa")
                            //System.out.println("Ошибка расшифровки GZIPa");
                        }

                    }
                }
            }
            decoder!!.join()
           // MainActivity.h.sendEmptyMessage(1)
            out.close()
            log.warning("PDFParser works (ms): " + (System.currentTimeMillis() - startTime))
        } catch (e: FileNotFoundException) {
           // MainActivity.h.sendEmptyMessage(MainActivity.HANDLE_MESSAGE_NOT_EXTRACTED)
        } catch (e: IOException) {
          //  MainActivity.h.sendEmptyMessage(MainActivity.HANDLE_MESSAGE_NOT_EXTRACTED)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    /**
     * Метод, расшифровывающий @length байт из потока @in и пишущий расшифрованные данные в @outputStream
     *
     * @param length - длина зашифрованной части файла в байтах
     * @param in - входной поток
     */
    @Throws(DataFormatException::class, IOException::class)
    private fun decode(length: Int, `in`: InputStream) {
        val output = ByteArray(length)
        val compressedDataLength = `in`.read(output)
        decoder = Thread(UnGzipper(output, compressedDataLength))
        decoder!!.start()

    }

    /**
     * Исполняемый класс, который декодит текст и пишет его в @outputStream */

    private inner class UnGzipper internal constructor(private val output: ByteArray, private val compressedDataLength: Int) : Runnable {

        override fun run() {
            val decompressor = Inflater()
            decompressor.setInput(output, 0, compressedDataLength)

            val result = ByteArray(compressedDataLength * 10)
            var resultLength = 0
            try {
                resultLength = decompressor.inflate(result)
            } catch (e: DataFormatException) {
                e.printStackTrace()
            }

            decompressor.end()

            try {
                outputStream!!.write(result, 0, resultLength)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }




}
