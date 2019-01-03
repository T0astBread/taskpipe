package cc.t0ast.taskpipe.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommandLineUtilsTest {

    @Test
    fun testSplitIntoArgs() {
        val expected = arrayOf("start", "my", "command", "with this string")
        val inputCommand = "start my command \"with this string\""
        val actual = inputCommand.splitIntoArgs()
        Assertions.assertArrayEquals(expected, actual)
    }
}