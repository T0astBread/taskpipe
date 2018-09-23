package cc.t0ast.taskpipe.utils.concurrency

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.sync.Mutex
import java.util.concurrent.atomic.AtomicInteger

class WaitGroup(initialValue: Int = 0) {
    private var counter = AtomicInteger(initialValue)
    private val counterMutex = Mutex()
    private val waitChannel = Channel<Unit>()

    suspend fun add(amount: Int = 1) {
//        this.counterMutex.withLock {
            val counterValue = this.counter.addAndGet(amount)
            when {
                counterValue == 0 ->
                    this.waitChannel.send(Unit)
                counterValue < 0 ->
                    throw IllegalStateException("Counter value below zero!")
            }
//        }
    }

    suspend fun done() = add(-1)

    suspend fun wait() {
        this.waitChannel.receive()
    }
}