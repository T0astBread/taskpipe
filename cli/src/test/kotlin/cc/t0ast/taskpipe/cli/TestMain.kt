package cc.t0ast.taskpipe.cli

import cc.t0ast.taskpipe.cli.test_utils.execSelf
import org.junit.Assert.assertEquals
import org.junit.Test

class TestMain {
    @Test
    fun testMain() {
        val output = execSelf()
        assertEquals("Hello world!\n", output)
    }

    @Test
    fun testVerboseMain() {
        val output = execSelf("-v")
        assertEquals("Hello planet earth, horizon and everything we know beyond!\n", output)
    }
}