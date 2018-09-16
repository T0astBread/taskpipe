package cc.t0ast.taskpipe

import java.io.File

interface Module {
    val name: String
    val isGroupModule: Boolean

    suspend fun run(workingDirectory: File, arguments: Map<String, Any>)
}