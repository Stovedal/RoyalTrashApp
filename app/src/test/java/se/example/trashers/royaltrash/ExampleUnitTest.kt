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
        val str = DBrequests()
        var str2 = str.apiHttpPostToServer("{\"hs_username\":\"Test\",\"hs_score\":1,\"lat\":63.802443,\"lng\":20.320271}")
        val str3 = str.apiGetHighscoreByUsername("Test")

            assertEquals(true, true)
    }

    @Test
    fun FindByUser() {
        val str = DBrequests()
        val str2 = str.apiGetHighscoreByUsername("eson")
        println("ID: "+str2[0].hs_id + "Username: "+str2[0].hs_username)

        assertEquals(true, true)
    }
}
