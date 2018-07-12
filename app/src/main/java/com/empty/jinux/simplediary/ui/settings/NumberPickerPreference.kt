package com.empty.jinux.simplediary.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import com.empty.jinux.simplediary.R

/*
 * Add this to your XML resource.
 */
class NumberPickerPreference : android.support.v7.preference.DialogPreference {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        dialogLayoutResource = R.layout.dialog_settings_number_picer
    }

    private var numberPicker: NumberPicker? = null

//    override fun onCreateDialogView(): View {
//        return generateNumberPicker()
//    }

    fun generateNumberPicker(): NumberPicker {
        numberPicker = NumberPicker(context)
        numberPicker!!.minValue = 1025
        numberPicker!!.maxValue = 65535
        numberPicker!!.value = 1025

        /*
         * Anything else you want to add to this.
         */


        return numberPicker!!
    }

//    override fun onDialogClosed(positiveResult: Boolean) {
//        super.onDialogClosed(positiveResult)
////        if (positiveResult) {
////            val port = numberPicker!!.value
////            Log.d("NumberPickerPreference", "NumberPickerValue : $port")
////        }
//    }


}