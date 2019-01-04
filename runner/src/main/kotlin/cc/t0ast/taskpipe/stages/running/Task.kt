package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.stages.parsing.Job
import java.io.File

class Task(val workingDirectory: File, val jobs: List<Job>) {
    suspend fun run() {
        this.jobs.forEach { it.run(this.workingDirectory) }
    }

    override fun toString(): String {
        return "Task($workingDirectory, [${jobs.joinToString(", ")}])"
    }
}