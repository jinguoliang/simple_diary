package com.empty.jinux.simplediary.util

fun String.wordsCount(): Int {
    val sentence = toCharArray()
    var inWord = false
    var wordCt = 0
    for (c in sentence) {
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            if (!inWord) {
                wordCt++
                inWord = true
            }
        } else {
            inWord = false
        }
    }
    return wordCt
}