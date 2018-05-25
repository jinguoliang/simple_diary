package com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MyEmotionIcons
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MyWeatherIcons
import kotlinx.android.synthetic.main.fragment_edit_status.*

class StatusFragment : MFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val iconSize = resources.getDimensionPixelSize(R.dimen.edit_status_icon_size)
        weatherRadioGroup.addRadios(MyWeatherIcons.getAllMyIcon(), iconSize)
        emotionRadioGroup.addRadios(MyEmotionIcons.getAllMyIcon(), iconSize)

        weatherRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val index = group.indexOfChild(group.findViewById<RadioButton>(checkedId))
            mPresenter.setWeather(MyWeatherIcons.getIconByIndex(index))
            mReporter.reportClick("detail_tool_weather", MyWeatherIcons.getWeatherName(index))

        }

        emotionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val index = group.indexOfChild(group.findViewById<RadioButton>(checkedId))
            mPresenter.setEmotion(index.toLong())
            mReporter.reportClick("detail_tool_emotion", MyEmotionIcons.getEmotionName(index))

            //            mReporter.reportClick("detail_tool_toggle")
            //            mReporter.reportClick("detail_tool_location")
        }
    }

    fun showWeather(weather: String, weatherIconUrl: String) {
        val iconIndex = MyWeatherIcons.getIconIndex(weatherIconUrl)
        val child = weatherRadioGroup.getChildAt(iconIndex)
                ?: weatherRadioGroup.getChildAt(0).apply {
                    mReporter.reportEvent("exception", Bundle())
                }
        weatherRadioGroup.check(child.id)
    }

    fun showEmotion(id: Long) {
        val child = emotionRadioGroup.getChildAt(id.toInt())
                ?: emotionRadioGroup.getChildAt(0)
        emotionRadioGroup.check(child.id)
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun RadioGroup.addRadios(iconReses: List<Int>, iconSize: Int) {
    iconReses.map {
        RadioButton(context).apply {
            buttonDrawable = VectorDrawableCompat.create(resources, it, null)?.apply { setBounds(0, 0, 50, 50) }
            buttonTintList = ResourcesCompat.getColorStateList(resources, R.color.button_selector, null)
        }
    }.forEach {
        addView(it, LinearLayout.LayoutParams(iconSize, iconSize).apply { })
    }
}