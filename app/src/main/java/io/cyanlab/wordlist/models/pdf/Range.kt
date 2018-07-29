package io.cyanlab.wordlist.models.pdf

internal class Range(val begin: Int, val end: Int, val newRange: Int) {
    val dif: Int
    private val endNewRange: Int

    val rangeArray: IntArray
        get() {
            val arr = IntArray(dif)
            for (i in 0 until dif) {
                arr[i] = begin + i
            }
            return arr
        }

    init {
        this.dif = end - begin
        this.endNewRange = newRange + dif
    }

    fun getendNewRange(): Int {
        return endNewRange
    }
}
