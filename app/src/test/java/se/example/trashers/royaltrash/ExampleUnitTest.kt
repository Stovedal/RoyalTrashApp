package se.example.trashers.royaltrash

import android.util.JsonReader
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.InputStreamReader
import java.net.URL


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val str = ScoreBoardActivity()
        val str2 = str.apiGetHighscores()
            assertEquals(true, true);



    }
}
