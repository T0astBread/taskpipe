package cc.t0ast.taskpipe.stages.running

abstract class RunContext {
    var isStopped: Boolean = false
        protected set

    open fun stopExecution(reason: String, additionalData: Any?) {
        println("Stopped execution; Reason: $reason")
    }
}