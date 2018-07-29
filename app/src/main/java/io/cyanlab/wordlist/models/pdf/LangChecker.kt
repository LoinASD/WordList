package io.cyanlab.wordlist.models.pdf

internal object LangChecker {

    private val UNICODE_RANGE_RUS = intArrayOf(1040, 1103)
    private val UNICODE_RANGE_ENG = intArrayOf(65, 122)
    private val UNICODE_RANGE_NUMERIC = intArrayOf(48, 57)

    fun langCheck(ch: Char): Lang {
        val cc = ch.toInt()
        if (cc >= UNICODE_RANGE_ENG[0] && cc <= UNICODE_RANGE_ENG[1]) {
            return Lang.ENG
        } else if (cc >= UNICODE_RANGE_RUS[0] && cc <= UNICODE_RANGE_RUS[1]) {
            return Lang.RUS
        } else if (cc >= UNICODE_RANGE_NUMERIC[0] && cc <= UNICODE_RANGE_NUMERIC[1]) {
            return Lang.NUM
        } else if (cc == '<'.toInt())
            return Lang.BRACE
        return Lang.UNDEFINED
    }
}