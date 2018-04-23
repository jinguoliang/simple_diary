package com.empty.jinux.simplediary.DaggerTest

import dagger.Component
import org.junit.Test
import javax.inject.Inject

/**
 * Created by jingu on 2018/3/3.
 */
class SimpleTest {

    @Test
    fun testSimple() {

    }
}


@Component
interface DogComponent {
    fun getDog(): Dog
}

class Dog @Inject constructor(val name: String)