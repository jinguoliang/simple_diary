/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.empty.jinux.simplediary.data

import com.empty.jinux.baselibaray.utils.formatDateWithWeekday
import com.empty.jinux.baselibaray.utils.formatTime
import com.empty.jinux.simplediary.location.Location
import com.empty.jinux.baselibaray.utils.getFirstLine

const val INVALID_DIARY_ID = -1L

data class Diary
constructor(
        val id: Long,
        val diaryContent: DiaryContent,
        val meta: Meta
)

data class WeatherInfo(val description: String, val icon: String)

data class LocationInfo(val location: Location, val address: String)

data class EmotionInfo(val id: Long)

data class DiaryContent(var title: String,
                        var content: String,
                        var displayTime: Long,
                        var weatherInfo: WeatherInfo? = null,
                        var locationInfo: LocationInfo? = null,
                        var emotionInfo: EmotionInfo? = null) {
    fun getTitleFromContent(): String {
        content.trim().let {
            return it.getFirstLine()
        }
    }

}

data class Meta(val createdTime: Long,
                var lastChangeTime: Long,
                var deleted: Boolean = false)


fun Diary.formatCreatedTime(): String {
    val createdTime = meta.createdTime
    if (createdTime == 0L) {
        return ""
    }
    return formatTime(createdTime)
}

fun DiaryContent.formatDisplayTime(): String {
    val displayTime = displayTime
    if (displayTime == 0L) {
        return ""
    }
    return formatDateWithWeekday(displayTime)
}