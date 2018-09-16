package cc.t0ast.taskpipe.stages.segmentation

import cc.t0ast.taskpipe.Pipeline

class SegmentedPipeline(name: String, val segments: List<Segment>): Pipeline(name)