package com.empty.jinux.simplediary.util

fun String.getFirstLine(limitWidth: Int = 20): String {
    val firstLine = split("\n")[0]
    return firstLine.substring(0 until Math.min(firstLine.length, limitWidth))
}