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
import java.util.*

/**
 * Immutable model class for a Diary.
 */
data class Diary

/**
 * Use this constructor to create a new Diary.
 *
 * @param title       title of the diary
 * @param description description of the diary
 * @param id          id of the diary
 * @param completed   true if the diary is completed, false if it's active
 */
constructor(
        val title: String = "",
        val description: String = "",
        val id: String = UUID.randomUUID().toString(),
        val isCompleted: Boolean = false,
        val createdTime: Long = System.currentTimeMillis(),
        val displayTime: Long = createdTime
) {

    val titleForList: String
        get() = if (!Strings.isNullOrEmpty(title)) {
            title
        } else {
            description
        }

    val isActive: Boolean
        get() = !isCompleted

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(title) && Strings.isNullOrEmpty(description)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val diary = o as Diary?
        return Objects.equal(id, diary!!.id) &&
                Objects.equal(title, diary.title) &&
                Objects.equal(description, diary.description)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, title, description)
    }

    override fun toString(): String {
        return "Diary with title " + title
    }

}
