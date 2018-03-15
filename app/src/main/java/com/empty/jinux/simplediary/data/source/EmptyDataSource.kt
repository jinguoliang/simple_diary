package com.empty.jinux.simplediary.data.source

import com.empty.jinux.simplediary.data.Diary

/**
 * Created by jingu on 2018/3/15.
 */
class EmptyDataSource : DiariesDataSource {
    override fun getDiaries(callback: DiariesDataSource.LoadDiariesCallback) {
        callback.onDiariesLoaded(arrayListOf())
    }

    override fun getDiary(diaryId: Int, callback: DiariesDataSource.GetDiaryCallback) {
        callback.onDataNotAvailable()
    }

    override fun save(diary: Diary) {
    }

    override fun refreshDiaries() {
    }

    override fun deleteAllDiaries() {
    }

    override fun deleteDiary(diaryId: Int) {
    }
}