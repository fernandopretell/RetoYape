package com.fulbiopretell.retoyape.core.helpers

object TimerHelper {
    fun format(timeInMilis: Long) : ArrayList<String> {
        val seconds = ((timeInMilis / 1000).toInt() % 60).toString()
        val minutes = (timeInMilis / (1000 * 60) % 60).toInt().toString()
        val hours = (timeInMilis / (1000 * 60 * 60) % 24).toInt().toString()

        return arrayListOf(hours, minutes, seconds)
    }
}
