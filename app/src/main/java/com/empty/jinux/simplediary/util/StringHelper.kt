package com.empty.jinux.simplediary.util

fun String.wordsCount(): Int {
    var inWord = false
    var wordCt = 0
    for (c in this) {
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            if (!inWord) {
                wordCt++
                inWord = true
            }
        } else if (isChineseByBlock(c)) {
            if (!isChinesePunctuation(c)) {
                wordCt++
            }
            inWord = false
        } else {
            inWord = false
        }
    }
    return wordCt
}

fun isChineseByBlock(c: Char): Boolean {
    val ub = Character.UnicodeBlock.of(c)
    return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT)
}

fun isChinesePunctuation(c: Char): Boolean {
    val ub = Character.UnicodeBlock.of(c)
    return (ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
}