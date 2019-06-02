package cc.t0ast.taskpipe

import cc.t0ast.taskpipe.stages.parsing.ParsedPipeline
import cc.t0ast.taskpipe.stages.parsing.parsePipeline
import cc.t0ast.taskpipe.stages.running.ParallelPipelineRunner
import cc.t0ast.taskpipe.stages.running.PipelineRunner
import cc.t0ast.taskpipe.stages.segmentation.segment
import cc.t0ast.taskpipe.utils.findIndexOfBreakpoint
import kotlinx.coroutines.runBlocking
import java.io.File

suspend fun executePipeline(
    pipelineDir: File,
    outputDir: File,
    startJobIndex: Int,
    runner: PipelineRunner = ParallelPipelineRunner(outputDir)
) {
    val parsedPipeline = parsePipeline(pipelineDir)
    runBlocking { executePipeline(parsedPipeline, outputDir, startJobIndex, runner) }
}

suspend fun executePipeline(
    pipelineDir: File,
    outputDir: File,
    startBreakpoint: String,
    runner: PipelineRunner = ParallelPipelineRunner(outputDir)
) {
    val parsedPipeline = parsePipeline(pipelineDir)
    val startBreakpointIndex = parsedPipeline.findIndexOfBreakpoint(startBreakpoint)
    runBlocking { executePipeline(
        parsedPipeline,
        outputDir,
        startBreakpointIndex + 1,
        runner
    ) }
}

private suspend fun executePipeline(
    parsedPipeline: ParsedPipeline,
    outputDir: File,
    startJobIndex: Int,
    runner: PipelineRunner
) {
    val segmentedPipeline = segment(parsedPipeline)
    runBlocking { runner.run(segmentedPipeline, startJobIndex) }
}
