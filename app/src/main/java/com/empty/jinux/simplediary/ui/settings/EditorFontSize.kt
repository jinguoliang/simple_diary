package com.empty.jinux.simplediary.ui.settings

class EditorFontSize(val name: String) {
    companion object {
        const val DEFAULT = "normal"

        val NAMES = listOf("small", DEFAULT, "large")
        val SIZES = mapOf(NAMES[0] to 15.0f, NAMES[1] to 20f, NAMES[2] to 25f)
    }

    val size: Float
        get() = SIZES[name]!!
}