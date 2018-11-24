package com.empty.jinux.simplediary.ui.diarydetail.presenter

import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.simplediary.STREAK_MIN_WORDS_COUNTS

class ShowGoodViewHelper(var mWordCountOfToday: Int, private val mListener: Listener) {
    companion object {
        const val TAG = "ShowGoodViewHelper"
    }
    private var mWordCountOfOtherArticleToday: Int = 0

    fun init(currentArticleWordCount: Int) {
        mWordCountOfOtherArticleToday = mWordCountOfToday - currentArticleWordCount
        showOrHideGoodView(currentArticleWordCount, false)
    }

    fun updateCurrentArticleWordCount(currentArticleWordCount: Int) {
        mWordCountOfToday = mWordCountOfOtherArticleToday + currentArticleWordCount
        showOrHideGoodView(currentArticleWordCount, true)
    }

    private var preWordCountToday = 0

    var isToday: Boolean = false

    private fun showOrHideGoodView(currentArticleWordCount: Int, animShow: Boolean) {
        if (!isToday) {
            return
        }

        val currentWordCountToday = mWordCountOfOtherArticleToday + currentArticleWordCount
        if (STREAK_MIN_WORDS_COUNTS in (preWordCountToday + 1)..currentWordCountToday) {
            if (animShow) {
                mListener.onShowGoodViewAnim()
            } else {
                mListener.onShowGood()
            }
        } else if (STREAK_MIN_WORDS_COUNTS in (currentWordCountToday + 1)..preWordCountToday) {
            mListener.onHideGood()
        }
        preWordCountToday = currentWordCountToday
    }

    interface Listener {
        fun onShowGoodViewAnim()
        fun onShowGood()
        fun onHideGood()
    }
}
