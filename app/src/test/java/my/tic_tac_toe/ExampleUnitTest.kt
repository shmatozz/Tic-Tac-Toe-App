package my.tic_tac_toe

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 1)
    }

    @Test
    fun minus_isCorrect() {
        assertEquals(0, 2 - 2)
    }
    @Test
    fun mul_isCorrect() {
        assertEquals(4, 2 * 2)
    }
}

class AppUnitTests {
    @Test
    fun addition_isCorrect() {
        assertEquals(MainActivity.wowItsTestFunc("Matthew"), "Hello Matthew!")
    }
}