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

import com.google.common.base.Objects
import com.google.common.base.Strings

/**
 * Immutable model class for a Diary.
 */
data class Diary
constructor(
        val id: Int?,
        val content: String = "",
        val createdTime: Long = System.currentTimeMillis(),
        val displayTime: Long = createdTime
) {

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(content)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val diary = other as Diary?
        return Objects.equal(id, diary!!.id) &&
                Objects.equal(content, diary.content)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, content)
    }

    override fun toString(): String {
        return "$id: $content"
    }

}
