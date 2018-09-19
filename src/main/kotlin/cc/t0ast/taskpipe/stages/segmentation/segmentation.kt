package cc.t0ast.taskpipe.stages.segmentation

import cc.t0ast.taskpipe.stages.parsing.ParsedPipeline

fun segment(pipeline: ParsedPipeline): SegmentedPipeline {
    val segments: MutableList<Segment> = ArrayList()
    var openSegment: Segment? = null

    pipeline.jobs.forEachIndexed { i, job ->
        if(openSegment == null) {
            openSegment = Segment(i)
        }
        else if(openSegment!!.isGroupSegment != job.module.isGroupModule) {
            segments.add(openSegment!!)
            openSegment = Segment(i)
        }
        (openSegment!!.jobs as MutableList).add(job)
    }
    if(openSegment != null) segments.add(openSegment!!)

    return SegmentedPipeline(pipeline.name, segments)
}