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

private const val TASK_QUEUE_CAPACITY = 256  // completely arbitrary
private const val AMOUNT_OF_PROCESSOR_COROUTINES = 6  // for the sake of this project these are like threads, so it depends on parallel capabilities of machine; TODO: possible command line arg?

class ParallelPipelineRunner(currentWorkingDirectory: File): PipelineRunner(currentWorkingDirectory) {
    private val taskWaitGroup = WaitGroup()

    override suspend fun run(pipeline: SegmentedPipeline) {
        super.run(pipeline)

        LOGGER.fine("Running pipeline ${pipeline.name}")

        val producer = GlobalScope.launchTaskProducer(pipeline)  // start the producer
        val runContext = ParallelRunContext(producer)  // Create the run context

        val processors = IntStream.range(0, AMOUNT_OF_PROCESSOR_COROUTINES)  // Start the processors with sequential IDs
                .mapToObj { i -> GlobalScope.launchTaskProcessor(producer, i, runContext) }
                .collect(Collectors.toList())
        processors.forEach { runContext.registerProcessor(it) }  // Register the processors in the run context

        for(processor in processors) {  // Wait for all the processors to finish; processors should only finish when the producer is finished
            processor.join()
        }

        LOGGER.fine("Execution of pipeline ${pipeline.name} completed")
    }

    // PRODUCER CODE
    private fun CoroutineScope.launchTaskProducer(pipeline: SegmentedPipeline) = produce(capacity = TASK_QUEUE_CAPACITY) {
        PRODUCER_LOGGER.fine("Started")

        // iterate over segments in pipeline
        for(segment in pipeline.segments) {  // take a segment
            PRODUCER_LOGGER.finer("Took segment $segment")

            if(segment.jobs.isEmpty()) {  // skip (erroneously) empty segment
                PRODUCER_LOGGER.warning("Skipped empty segment $segment. This shouldn't happen. Check if you have any errors in your segmentation code.")
                continue
            }

            if(segment.isGroupSegment) {  // if the operation_mode is group: post a task with the segment in the root folder
                PRODUCER_LOGGER.finer("Enqueueing group segment $segment")
                send(Task(currentWorkingDirectory, segment.jobs))
                taskWaitGroup.add()
            }
            else {  // if the operation_mode is individual: post tasks with the segment for each entry folder
                LOGGER.finer("Enqueueing individual segment $segment")
                contentDirectory.listFiles().forEach { entryDir ->
                    send(Task(entryDir, segment.jobs))
                    taskWaitGroup.add()
                    PRODUCER_LOGGER.finest("Enqueued task chain from segment $segment for entry $entryDir")
                }
            }

            // wait for the segment tasks to finish before posting the next segment
            PRODUCER_LOGGER.finer("Waiting for segment to finish")
            taskWaitGroup.wait()
            PRODUCER_LOGGER.finer("Segment finished")
        }

        // all segments done
        PRODUCER_LOGGER.fine("Finished")
    }

    // PROCESSOR CODE
    private fun CoroutineScope.launchTaskProcessor(
        taskChannel: ReceiveChannel<Task>,
        processorId: Int? = null,
        runContext: ParallelRunContext
    ) = launch {
        PROCESSOR_LOGGER.fine("#$processorId started")

        // task-handler (a for-loop over a RecieveChannel is Kotlin coroutine-speak for "hey this is how you handle messages"
        // it won't exit when the channel is empty, instead it'll block and wait for new messages until the channel is closed (i.e. terminated by the producer)
        for(task in taskChannel) {
            PROCESSOR_LOGGER.finer("#$processorId took a task")
            PROCESSOR_LOGGER.finest("#$processorId took task with modules [${task.jobs.map { job ->
                job.module.name
            }.joinToString(", ")}]")
            task.run(runContext)  // just run the task
            taskWaitGroup.done()  // report that the task is done
        }

        PROCESSOR_LOGGER.fine("#$processorId finished")
    }
}