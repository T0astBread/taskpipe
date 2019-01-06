package cc.t0ast.taskpipe.modules

import cc.t0ast.taskpipe.OperationMode
import java.io.File

abstract class Module {
    abstract val name: String
    abstract val supportedOperationModes: Array<OperationMode>

    abstract suspend fun run(workingDirectory: File, arguments: Map<String, Any>)

    override fun equals(other: Any?): Boolean {
        return other is Module &&
                this::class == other::class &&
                this.name == other.name &&
                this.supportedOperationModes.contentEquals(other.supportedOperationModes)
    }
}