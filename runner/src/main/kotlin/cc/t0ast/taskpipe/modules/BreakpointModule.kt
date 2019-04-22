package cc.t0ast.taskpipe.modules

import cc.t0ast.taskpipe.OperationMode
import cc.t0ast.taskpipe.stages.running.RunContext
import java.io.File

class BreakpointModule : Module() {
    override val name: String = "breakpoint"
    override val supportedOperationModes: Array<OperationMode> = arrayOf(OperationMode.GROUP)

    override suspend fun run(runContext: RunContext, workingDirectory: File, arguments: Map<String, Any>) {
        val message = arguments["message"]
        val name = arguments["name"]

        runContext.stopExecution("Breakpoint hit ($name)", arguments)
        println(message)
    }
}