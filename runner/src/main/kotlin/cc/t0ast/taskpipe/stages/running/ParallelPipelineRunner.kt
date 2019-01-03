package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.stages.segmentation.SegmentedPipeline
import cc.t0ast.taskpipe.utils.concurrency.WaitGroup
import cc.t0ast.taskpipe.utils.logging.getLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import java.io.File
import java.util.stream.Collectors
import java.util.stream.IntStream

private val LOGGER = getLogger(ParallelPipelineRunner::class.java.simpleName)
private val PROCESSOR_LOGGER = getLogger("${ParallelPipelineRunner::class.java.simpleName} -> TaskProcessor")
private val PRODUCER_LOGGER = getLogger("${ParallelPipelineRunner::class.java.simpleName} -> TaskProducer")

private const val TASK_QUEUE_CAPACITY = 265
private const val AMOUNT_OF_PROCESSOR_COROUTINES = 6

class ParallelPipelineRunner(currentWorkingDirectory: File): PipelineRunner(currentWorkingDirectory) {
    private val taskWaitGroup = WaitGroup()

    override suspend fun run(pipeline: SegmentedPipeline) {
        LOGGER.fine("Running pipeline ${pipeline.name}")

        val producer = GlobalScope.launchTaskProducer(pipeline)
        val processors = IntStream.range(0, AMOUNT_OF_PROCESSOR_COROUTINES)
                .mapToObj { i -> GlobalScope.launchTaskProcessor(producer, i) }
                .collect(Collectors.toList())
        for(processor in processors) {
            processor.join()
        }

        LOGGER.fine("Execution of pipeline ${pipeline.name} completed")
    }

    private fun CoroutineScope.launchTaskProducer(pipeline: SegmentedPipeline) = produce(capacity = TASK_QUEUE_CAPACITY) {
        PRODUCER_LOGGER.fine("Started")

        for(segment in pipeline.segments) {
            PRODUCER_LOGGER.finer("Took segment $segment")

            if(segment.jobs.isEmpty()) {
                PRODUCER_LOGGER.warning("Skipped empty segment $segment. This shouldn't happen. Check if you have any errors in your segmentation code.")
                continue
            }

            if(segment.isGroupSegment) {
                PRODUCER_LOGGER.finer("Enqueueing group segment $segment")
                send(Task(currentWorkingDirectory, segment.jobs))
                taskWaitGroup.add()
            }
            else {
                LOGGER.finer("Enqueueing individual segment $segment")
                currentWorkingDirectory.listFiles().forEach { entryDir ->
                    send(Task(entryDir, segment.jobs))
                    taskWaitGroup.add()
                    PRODUCER_LOGGER.finest("Enqueued task chain from segment $segment for entry $entryDir")
                }
            }

            PRODUCER_LOGGER.finer("Waiting for segment to finish")
            taskWaitGroup.wait()
            PRODUCER_LOGGER.finer("Segment finished")
        }

        PRODUCER_LOGGER.fine("Finished")
    }

    private fun CoroutineScope.launchTaskProcessor(taskChannel: ReceiveChannel<Task>, processorId: Int? = null) = launch {
        PROCESSOR_LOGGER.fine("#$processorId started")

        for(task in taskChannel) {
            PROCESSOR_LOGGER.finer("#$processorId took a task")
            PROCESSOR_LOGGER.finest("#$processorId took task with modules [${task.jobs.map { job ->
                job.module.name
            }.joinToString(", ")}]")
            task.run()
            taskWaitGroup.done()
        }

        PROCESSOR_LOGGER.fine("#$processorId finished")
    }
}