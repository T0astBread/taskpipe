package cc.t0ast.taskpipe.stages.segmentation

import cc.t0ast.taskpipe.OperationMode
import cc.t0ast.taskpipe.stages.parsing.Job

class Segment(val startIndex: Int, val jobs: List<Job>) {

    val isGroupSegment
        get() = this.jobs.isEmpty() || this.jobs[0].operationMode == OperationMode.GROUP

    constructor(startIndex: Int): this(startIndex, ArrayList())

    override fun toString() = "S$startIndex-${this.startIndex + this.jobs.size}"

    override fun equals(other: Any?): Boolean {
        return other is Segment && this.startIndex == other.startIndex && this.jobs == other.jobs
    }
}