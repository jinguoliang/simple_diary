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

import com.empty.jinux.simplediary.location.Location

const val INVALID_DIARY_ID = -1
val EMPTY_WEATHER = WeatherInfo("", "")
val EMPTY_LOCATION = LocationInfo(Location(-1.0, -1.0), "")
val EMPTY_CONTENT = Content("", "", -1, EMPTY_WEATHER, EMPTY_LOCATION)
val EMPTY_META = Meta(-1, -1, false)

data class Diary
constructor(
        val id: Int,
        val content: Content,
        val meta: Meta
)

data class WeatherInfo(val description: String, val iconUri: String)

data class LocationInfo(val location: Location, val address: String)

data class Content(var title: String,
                   var content: String,
                   var displayTime: Long,
                   var weatherInfo: WeatherInfo,
                   var locationInfo: LocationInfo)

data class Meta(val createdTime: Long,
                var lastChangeTime: Long,
                var deleted: Boolean = false) {
}