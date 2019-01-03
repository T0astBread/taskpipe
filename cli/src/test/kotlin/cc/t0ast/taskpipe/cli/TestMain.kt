package cc.t0ast.taskpipe.cli

import cc.t0ast.taskpipe.cli.test_utils.execSelf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemOutRule

class TestMain {
    @Rule
    @JvmField
    val exitRule = ExpectedSystemExit.none()

    @Rule
    @JvmField
    val outRule = SystemOutRule().enableLog()

    @Test
    fun testHelp() {
        exitRule.expectSystemExitWithStatus(0)
        exitRule.checkAssertionAfterwards {
            assertTrue(outRule.log.startsWith("usage: "))
        }
        execSelf("-h")
    }

    @Test
    fun testRun() {
        execSelf("run")
        assertEquals("RUN\n", outRule.log)
    }
}