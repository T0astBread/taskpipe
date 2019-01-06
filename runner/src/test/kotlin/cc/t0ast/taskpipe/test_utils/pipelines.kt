package cc.t0ast.taskpipe.test_utils

import cc.t0ast.taskpipe.OperationMode
import cc.t0ast.taskpipe.test_utils.modules.ArgDumpModule
import cc.t0ast.taskpipe.test_utils.modules.DummyEntryCreatorModule
import cc.t0ast.taskpipe.stages.parsing.Job
import cc.t0ast.taskpipe.stages.parsing.ParsedPipeline
import cc.t0ast.taskpipe.stages.segmentation.Segment
import cc.t0ast.taskpipe.stages.segmentation.SegmentedPipeline

const val AMOUNT_OF_ENTRIES_IN_RUN = 32

val parsedPipeline = ParsedPipeline("ParsedTestPipeline", listOf(
        Job(DummyEntryCreatorModule(), OperationMode.GROUP, hashMapOf(
                DummyEntryCreatorModule.AMOUNT_OF_ENTRIES to AMOUNT_OF_ENTRIES_IN_RUN
        )),
        Job(ArgDumpModule(), OperationMode.INDIVIDUAL, hashMapOf(
                "String" to "I'm a string",
                "Int" to 1234,
                "Boolean" to true
        ))
))

val segmentedPipeline = SegmentedPipeline("SegmentedTestPipeline", listOf(
        Segment(0, listOf(
                Job(DummyEntryCreatorModule(), OperationMode.GROUP, hashMapOf(
                        DummyEntryCreatorModule.AMOUNT_OF_ENTRIES to AMOUNT_OF_ENTRIES_IN_RUN
                ))
        )),
        Segment(1, listOf(
                Job(ArgDumpModule(), OperationMode.INDIVIDUAL, hashMapOf(
                        "String" to "I'm a string",
                        "Int" to 1234,
                        "Boolean" to true
                ))
        ))
))