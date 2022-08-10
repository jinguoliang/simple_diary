package com.empty.jinux.simplediary.data.source

import com.empty.jinux.simplediary.data.Diary

/**
 * Created by jingu on 2018/3/15.
 */
class EmptyDataSource : DiariesDataSource {
    override suspend fun getDiaries(): List<Diary> {
        return arrayListOf()
    }

    override suspend fun getDiary(diaryId: Long): Diary? {
        return null
    }

    override suspend fun save(diary: Diary): Long {
        return -1
    }

    override suspend fun refreshDiaries() {
    }

    override suspend fun deleteAllDiaries() {
    }

    override suspend fun deleteDiary(diaryId: Long): Boolean {
        return false
    }
}