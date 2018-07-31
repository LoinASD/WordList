package io.cyanlab.wordlist.models.pdf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Node : Serializable {

    var weight: Int = 0

    @ColumnInfo(name = "nodeWLName")
    var wlName: String? = null

    @PrimaryKey
    var id: Int? = null

    var primText: String? = null

    var transText: String? = null

    internal fun convertText(converter: CharConverter) {
        System.out.printf("P: %s%nT: %s%n%n", primText, transText)
        for (j in 0..1) {
            val text = if (j == 0) transText else primText
            if (text != null && text.contains("<")) {
                val message = StringBuilder()
                var cc: Char
                var last = 0
                var i = text.indexOf('<')

                while (i != -1 && i < text.length && i >= last) {
                    message.append(text.substring(last, i))
                    cc = text[++i]
                    var numChar: StringBuilder

                    while (cc != '>') {
                        numChar = StringBuilder()

                        if (text.length - i < 4)
                            return

                        for (k in 0..3) { // 4 - char`s length in HEX
                            numChar.append(cc)
                            cc = text[++i]
                        }
                        val c: Int
                        try {
                            c = Integer.parseInt(numChar.toString(), 16)
                            val ch = converter.convert(c)
                            if (ch != '.' && LangChecker.langCheck(ch) !== Lang.NUM)
                                message.append(ch)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            println(numChar)
                            println(cc)
                        }

                    }
                    last = i + 1
                    i = text.substring(last).indexOf('<') + last
                }
                if (j == 0) {
                    transText = message.toString()
                } else
                    primText = message.toString()
            }

        }


    }


}
