package cc.t0ast.taskpipe.stages.running

interface RunContext {
    fun stopExecution(reason: String, additionalData: Any?)
}