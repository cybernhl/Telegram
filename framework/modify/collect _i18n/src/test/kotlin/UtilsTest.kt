import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun testIndexOfFunEnd() {
        val result = "test()".indexOfFunEnd(5)
        assertEquals(5, result)
    }

    //unknown string content: [popupOptions[Utilities.clamp(option,  popupOptions.length - 1,  0)]]
    //unknown string content: [priorityOptions[Utilities.clamp(option,  priorityOptions.length - 1,  0)]]
    //unknown string content: [vibrateLabels[Utilities.clamp(getNotificationsSettings().getInt(key,  0),  vibrateLabels.length - 1,  0)]]
    @Test
    fun testLocaleString01() {
        val content =
            "LocaleController.getString(ApplicationLoader.getMapsProvider().getInstallMapsString())"
        val start = "LocaleController.getString(".length
        val end = content.indexOfFunEnd(start)
        assertEquals(
            content.substring(start, end),
            "ApplicationLoader.getMapsProvider().getInstallMapsString()"
        )
    }

    @Test
    fun testLocaleString02() {
        val content = "LocaleController.getString(\"Settings\", R.string.Settings)"
        val start = "LocaleController.getString(".length
        val end = content.indexOfFunEnd(start)
        assertEquals(content.substring(start, end), "\"Settings\", R.string.Settings")
    }

    @Test
    fun testLocaleString03() {
        val content = "LocaleController.getString(\"Settings\")"
        val start = "LocaleController.getString(".length
        val end = content.indexOfFunEnd(start)
        assertEquals(content.substring(start, end), "\"Settings\"")
    }

    @Test
    fun testLocaleString04() {
        val content = "LocaleController.getString(\"Settings\" + SomeString())"
        val start = "LocaleController.getString(".length
        val end = content.indexOfFunEnd(start)
        assertEquals(content.substring(start, end), "\"Settings\" + SomeString()")
    }
}
