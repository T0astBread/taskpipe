package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.stages.segmentation.SegmentedPipeline
import java.io.File

abstract class PipelineRunner(currentWorkingDirectory: File) {

    var currentWorkingDirectory: File = currentWorkingDirectory
        set(value) {
            field = value
            updateDirectoryPointers()
        }

    lateinit var contentDirectory: File
        private set

    lateinit var moduleDataDirectory: File
        private set

    open suspend fun run(pipeline: SegmentedPipeline, startJobIndex: Int = 0) {
        this.contentDirectory.mkdirs()
        this.moduleDataDirectory.mkdirs()
    }

    private fun updateDirectoryPointers() {
        this.contentDirectory = File(this.currentWorkingDirectory, "content")
        this.moduleDataDirectory = File(this.currentWorkingDirectory, "module_data")
    }

    init {
        updateDirectoryPointers()
    }
}