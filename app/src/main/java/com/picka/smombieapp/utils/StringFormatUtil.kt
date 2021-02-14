package com.picka.smombieapp.utils

class StringFormatUtil {

    fun sum(s1: String, s2: String): String {
        val x = if (s1.isEmpty()) 0 else (s1.replace(" ", "")).toInt()
        val y = if (s2.isEmpty()) 0 else (s2.replace(" ", "")).toInt()
        return (x + y).toString()
    }
}