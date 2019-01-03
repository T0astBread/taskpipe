package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.stages.segmentation.SegmentedPipeline
import java.io.File

abstract class PipelineRunner(var currentWorkingDirectory: File) {
    abstract suspend fun run(pipeline: SegmentedPipeline)
}