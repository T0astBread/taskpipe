package cc.t0ast.taskpipe

import cc.t0ast.taskpipe.stages.parsing.parsePipeline
import cc.t0ast.taskpipe.stages.running.ParallelPipelineRunner
import cc.t0ast.taskpipe.stages.running.PipelineRunner
import cc.t0ast.taskpipe.stages.segmentation.segment
import kotlinx.coroutines.runBlocking
import java.io.File

suspend fun executePipeline(
    pipelineDir: File,
    outputDir: File,
    startJobIndex: Int,
    runner: PipelineRunner = ParallelPipelineRunner(outputDir)
) {
    val parsedPipeline = parsePipeline(pipelineDir)
    val segmentedPipeline = segment(parsedPipeline)
    runBlocking { runner.run(segmentedPipeline, startJobIndex) }
}