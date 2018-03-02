package com.empty.jinux.simplediary.data.source.local.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.empty.jinux.simplediary.data.source.local.room.COLUMN_CONTENT
import com.empty.jinux.simplediary.data.source.local.room.COLUMN_ID
import com.empty.jinux.simplediary.data.source.local.room.TABLE_DIARY

/**
 * Created by jingu on 2018/3/2.
 */

@Entity(tableName = TABLE_DIARY)
data class Diary(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_ID) var id: Int?,
        @ColumnInfo(name = COLUMN_CONTENT) var text: String
) {
    constructor() : this(null, "")
}