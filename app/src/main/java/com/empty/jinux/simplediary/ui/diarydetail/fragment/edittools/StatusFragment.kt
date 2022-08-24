package com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.core.content.res.ResourcesCompat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.log.logw
import com.empty.jinux.baselibaray.utils.toast
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.LocationInfo
import com.empty.jinux.simplediary.databinding.FragmentEditStatusBinding
import com.empty.jinux.simplediary.location.Location
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MyEmotionIcons
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MyWeatherIcons
import com.google.android.gms.location.places.ui.PlacePicker

class StatusFragment : MFragment() {

    companion object {
        final val PLACE_PICKER_REQUEST_ID = 0x23
    }

    val setWeathTask = object : TaskWaitingConditions(4) {
        override fun run() {
            val iconIndex = MyWeatherIcons.getIconIndex(mWeatherIcon!!)
            val child = binding.weatherRadioGroup.getChildAt(iconIndex)
                    ?: binding.weatherRadioGroup.getChildAt(0).apply {
                        mReporter.reportEvent("exception", Bundle())
                    }
            binding.weatherRadioGroup.check(child.id)

            val child1 = binding.emotionRadioGroup.getChildAt(mEmotionId)
                    ?: binding.emotionRadioGroup.getChildAt(0)
            binding.emotionRadioGroup.check(child1.id)

            binding.address.text = mLocationInfo?.address
        }
    }

    private lateinit var binding: FragmentEditStatusBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val iconSize = resources.getDimensionPixelSize(R.dimen.edit_status_icon_size)
        binding.weatherRadioGroup.addRadios(MyWeatherIcons.getAllMyIcon(), iconSize)
        binding.emotionRadioGroup.addRadios(MyEmotionIcons.getAllMyIcon(), iconSize)

        setWeathTask.emit(4)

        binding.weatherRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val index = group.indexOfChild(group.findViewById<RadioButton>(checkedId))
            mPresenter.setWeather(MyWeatherIcons.getIconByIndex(index))
            mReporter.reportClick("detail_tool_weather", MyWeatherIcons.getWeatherName(index))
        }

        binding.emotionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val index = group.indexOfChild(group.findViewById<RadioButton>(checkedId))
            mPresenter.setEmotion(index.toLong())
            mReporter.reportClick("detail_tool_emotion", MyEmotionIcons.getEmotionName(index))

            //            mReporter.reportClick("detail_tool_toggle")
            //            mReporter.reportClick("detail_tool_location")
        }

        binding.edit.setOnClickListener {
            val intentBuilder = PlacePicker.IntentBuilder()
            val intent = try {
                intentBuilder.build(activity)
            } catch (e: Exception) {
                logw(Log.getStackTraceString(e), "google service")
                null
            }

            intent?.also {
                startActivityForResult(it, PLACE_PICKER_REQUEST_ID)
            } ?: context?.apply {
                requireActivity().toast(R.string.google_service_error)
            }
        }
    }

    private var mWeatherIcon: String? = null

    fun showWeather(weather: String, weatherIconUrl: String) {
        mWeatherIcon = weatherIconUrl
        setWeathTask.emit(1)
    }

    private var mEmotionId: Int = 0

    fun showEmotion(id: Long) {
        mEmotionId = id.toInt()
        setWeathTask.emit(2)
    }

    private var mLocationInfo: LocationInfo? = null

    fun showLocation(location: LocationInfo) {
        mLocationInfo = location
        setWeathTask.emit(3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_PICKER_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) return
                val place = PlacePicker.getPlace(activity, data)
                binding.address.text = place.address
                mPresenter.setLocation(LocationInfo(place.latLng.run { Location(latitude, longitude) }, place.address.toString()))
            }
        }

    }


}

private fun RadioGroup.addRadios(iconReses: List<Int>, iconSize: Int) {
    iconReses.map { VectorDrawableCompat.create(resources, it, null)!! }
            .map { drawableNormal: Drawable ->
                RadioButton(context).apply {
                    buttonDrawable = getDrawableNormal(resources, drawableNormal)
                    setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                        if (isChecked) {
                            buttonDrawable = getDrawableSelected(resources, drawableNormal)
                        } else {
                            buttonDrawable = getDrawableNormal(resources, drawableNormal)
                        }
                    }
                }
            }.forEach {
                addView(it, LinearLayout.LayoutParams(iconSize, iconSize).apply { })
            }
}

private fun getDrawableSelected(resources: Resources, origin: Drawable) = LayerDrawable(arrayOf(ResourcesCompat.getDrawable(resources, R.drawable.radio_group_checked_circle, null)!!.apply {  }, origin)).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setLayerGravity(1, Gravity.CENTER)
    } else {

    }
}

private fun getDrawableNormal(resources: Resources, origin: Drawable) = LayerDrawable(arrayOf(ResourcesCompat.getDrawable(resources, R.drawable.radio_group_checked_circle_hide, null)!!.apply {  }, origin)).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setLayerGravity(1, Gravity.CENTER)
    } else {

    }
}


abstract class TaskWaitingConditions(val maxCount: Int) {
    var count = 0

    val map = mutableMapOf<Int, Boolean>()

    fun emit(taskId: Int) {
        if (map[taskId] == true) {
            loge("repeat task $taskId")
            return
        }

        map[taskId] = true
        count++
        if (count == maxCount) {
            run()
        }
    }

    protected abstract fun run()
}