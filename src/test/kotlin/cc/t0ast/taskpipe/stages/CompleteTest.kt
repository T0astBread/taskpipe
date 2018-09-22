package cc.t0ast.taskpipe.stages

import cc.t0ast.taskpipe.stages.parsing.parsePipeline
import cc.t0ast.taskpipe.stages.running.ParallelPipelineRunner
import cc.t0ast.taskpipe.stages.segmentation.segment
import cc.t0ast.taskpipe.utils.EXAMPLE_PIPELINE_DIR
import cc.t0ast.taskpipe.utils.createTestDirectory
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test

class CompleteTest {

    @Test
    fun testAllStages() {
        val parsedPipeline = parsePipeline(EXAMPLE_PIPELINE_DIR)
        val segmentedPipeline = segment(parsedPipeline)

        val pipelineRunner = ParallelPipelineRunner(createTestDirectory())
        runBlocking { pipelineRunner.run(segmentedPipeline) }
    }
}