package com.empty.jinux.simplediary

import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.data.source.DiariesRepository
import com.empty.jinux.simplediary.utils.any
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Created by jingu on 2018/3/3.
 */


class DiaryRepositoryTest {

    @Mock
    lateinit var mDiariesLocalSource: DiariesDataSource
    @Mock
    lateinit var mDiariesRemoteSource: DiariesDataSource
    @Mock
    lateinit var mLoadDiariesCallback: DiariesDataSource.LoadDiariesCallback


    lateinit var diariesRepository: DiariesRepository

    @Before
    fun setupDiariesRepository() {
        MockitoAnnotations.initMocks(this)

        diariesRepository = DiariesRepository(mDiariesRemoteSource, mDiariesLocalSource)
    }

    @Test
    fun getDiaries_requestAllDiariesFromLocalDataSource() {
        diariesRepository.getDiaries(mLoadDiariesCallback)

        verify(mDiariesLocalSource).getDiaries(any())
    }
}