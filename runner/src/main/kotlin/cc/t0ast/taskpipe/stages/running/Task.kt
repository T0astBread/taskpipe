package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.stages.parsing.Job
import java.io.File

class Task(val workingDirectory: File, val jobs: List<Job>) {
    suspend fun run(runContext: RunContext) {
        this.jobs.forEach {
            if(runContext.isStopped)  // break if execution has been stopped (This is required because otherwise, the remaining jobs in the task would still be run)
                return@forEach

            it.run(runContext, this.workingDirectory)
        }
    }

    override fun toString(): String {
        return "Task($workingDirectory, [${jobs.joinToString(", ")}])"
    }
}
