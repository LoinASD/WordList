package io.cyanlab.wordlist.models.pdf

import java.util.ArrayList


internal class CharConverter {

    private val ranges = ArrayList<Range>()

    fun convert(c: Int): Char {
        val res: Int
        for (range in ranges) {

            if (c >= range.begin && c <= range.end) {
                res = range.newRange + (c - range.begin)
                return res.toChar()
            }
        }
        return c.toChar()
    }

    fun addNewRange(c1: Int, c2: Int, c3: Int) {
        ranges.add(Range(c1, c2, c3))
    }
}