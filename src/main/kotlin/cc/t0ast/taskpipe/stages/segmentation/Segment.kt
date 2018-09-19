package cc.t0ast.taskpipe.stages.segmentation

import cc.t0ast.taskpipe.stages.parsing.Job

class Segment(val startIndex: Int, val jobs: List<Job>) {

    val isGroupSegment
        get() = this.jobs.isEmpty() || this.jobs[0].module.isGroupModule

    constructor(startIndex: Int): this(startIndex, ArrayList())

    override fun toString() = "S$startIndex-${this.startIndex + this.jobs.size}"
}