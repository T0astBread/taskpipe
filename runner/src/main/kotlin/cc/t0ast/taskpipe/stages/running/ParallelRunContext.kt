package cc.t0ast.taskpipe.stages.running

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel

class ParallelRunContext(private val taskProducer: ReceiveChannel<Task>) : RunContext {
    private val taskProcessors: MutableList<Job> = mutableListOf()

    fun registerProcessor(processor: Job) {
        this.taskProcessors.add(processor)
    }

    override fun stopExecution(reason: String, additionalData: Any?) {
        this.taskProducer.cancel()
        this.taskProcessors.forEach { it.cancel() }
    }
}
