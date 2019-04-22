package cc.t0ast.taskpipe.stages.parsing

import cc.t0ast.taskpipe.OperationMode
import cc.t0ast.taskpipe.modules.BreakpointModule
import cc.t0ast.taskpipe.modules.ProcessModule
import cc.t0ast.taskpipe.test_utils.AMOUNT_OF_ENTRIES_IN_RUN
import cc.t0ast.taskpipe.test_utils.EXAMPLE_PIPELINE_DIR
import cc.t0ast.taskpipe.test_utils.EXAMPLE_PIPELINE_WITH_BREAKPOINT_DIR
import org.junit.jupiter.api.Test
import java.lang.Integer.parseInt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParsingTest {

    @Test
    fun testParse() {
        val examplePipelineDir = EXAMPLE_PIPELINE_DIR
        val parsedPipeline = parsePipeline(examplePipelineDir)

        assertEquals("ExamplePipeline", parsedPipeline.name)

        checkJob0(parsedPipeline.jobs[0])
        checkJob1(parsedPipeline.jobs[1])
    }

    private fun checkJob0(job: Job) {
        assert(job.module is ProcessModule)

        assertEquals("mkentry", job.module.name)
        assert(job.module.supportedOperationModes.contentEquals(arrayOf(OperationMode.GROUP)))
        assertEquals(OperationMode.GROUP, job.operationMode)

        assertEquals(1, job.arguments.size)
        assertEquals(AMOUNT_OF_ENTRIES_IN_RUN, parseInt(job.arguments["amount_of_entries"] as String))
    }

    private fun checkJob1(job: Job) {
        assert(job.module is ProcessModule)

        assertEquals("echo", job.module.name)
        assert(job.module.supportedOperationModes.contentEquals(arrayOf(OperationMode.INDIVIDUAL, OperationMode.GROUP)))
        assertEquals(OperationMode.INDIVIDUAL, job.operationMode)

        assertEquals(2, job.arguments.size)
        assertEquals("Hello world from the echo module!", job.arguments["text"])
        assertEquals("content/hellow.txt", job.arguments["target"])
    }

    @Test
    fun testParseEmbeddedModule() {
        val examplePipelineDir = EXAMPLE_PIPELINE_WITH_BREAKPOINT_DIR
        val parsedPipeline = parsePipeline(examplePipelineDir)

        assertEquals("ExamplePipelineWithBreakpoint", parsedPipeline.name)

        assertEquals(3, parsedPipeline.jobs.size)

        val breakpointJob = parsedPipeline.jobs[1]
        assertTrue(breakpointJob.module is BreakpointModule)
        assertEquals("This is a test breakpoint; Verify that it works correctly!", breakpointJob.arguments["message"])

        checkJob0(parsedPipeline.jobs[0])
        checkJob1(parsedPipeline.jobs[2])
    }
}