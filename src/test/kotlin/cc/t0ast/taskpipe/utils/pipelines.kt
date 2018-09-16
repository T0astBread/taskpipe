package cc.t0ast.taskpipe.utils

import cc.t0ast.taskpipe.utils.modules.ArgDumpModule
import cc.t0ast.taskpipe.utils.modules.DummyEntryCreatorModule
import cc.t0ast.taskpipe.stages.parsing.Job
import cc.t0ast.taskpipe.stages.segmentation.Segment
import cc.t0ast.taskpipe.stages.segmentation.SegmentedPipeline

const val AMOUNT_OF_ENTRIES_IN_RUN = 32

val segmentedPipeline = SegmentedPipeline("SegmentedTestPipeline", listOf(
        Segment(0, listOf(
                Job(DummyEntryCreatorModule(), hashMapOf(
                        DummyEntryCreatorModule.AMOUNT_OF_ENTRIES to AMOUNT_OF_ENTRIES_IN_RUN
                ))
        )),
        Segment(1, listOf(
                Job(ArgDumpModule(), hashMapOf(
                        "String" to "I'm a string",
                        "Int" to 1234,
                        "Boolean" to true
                ))
        ))
))