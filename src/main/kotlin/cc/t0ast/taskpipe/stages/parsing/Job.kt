package cc.t0ast.taskpipe.stages.parsing

import cc.t0ast.taskpipe.Module
import java.io.File

class Job(val module: Module, val arguments: Map<String, Any>) {

    suspend fun run(workingDirectory: File) {
        this.module.run(workingDirectory, this.arguments)
    }

    override fun toString(): String {
        return "Job($module, $arguments)"
    }

    override fun equals(other: Any?): Boolean {
        return other is Job && this.module == other.module && this.arguments == other.arguments
    }
}