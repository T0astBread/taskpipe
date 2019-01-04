package cc.t0ast.taskpipe.cli

import cc.t0ast.taskpipe.cli.test_utils.execSelf
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

    // ISSUE REGARDING System.err DEPENDENT TESTS:
    // Can't test err logs from logging facilities with System Rules
    // That's why all tests solely dependent on System.err logs (like testVerboseRun) never fail
    // and all System.err assertions are commented out. DO NOT REMOVE THESE TESTS/ASSERTIONS!
    // SEE https://github.com/T0astBread/taskpipe/issues/1

//    @Rule
//    @JvmField
//    val errRule = SystemErrRule().enableLog()

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
        execSelf("-p", "../runner/src/test/resources/example_pipeline", "-o", "./test_runs/%time%".timeStamped())
//        assertFalse(errRule.log.contains("Execution of pipeline ExamplePipeline completed"))
        assertTrue(outRule.log.endsWith("Done\n"))
    }

    @Test
    fun testVerboseRun() {
        execSelf("-p", "../runner/src/test/resources/example_pipeline", "-o", "./test_runs/%time%".timeStamped(), "-v")
//        assertTrue(errRule.log.contains("Execution of pipeline ExamplePipeline completed"))
        assertTrue(outRule.log.endsWith("Done\n"))
    }
}