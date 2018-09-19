package cc.t0ast.taskpipe.stages.segmentation

import cc.t0ast.taskpipe.utils.modules.ArgDumpModule
import cc.t0ast.taskpipe.utils.modules.DummyEntryCreatorModule
import cc.t0ast.taskpipe.utils.parsedPipeline
import cc.t0ast.taskpipe.utils.segmentedPipeline
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SegmentationTest {

    @Test
    fun testSegmentation() {
        val segmentedPipeline = segment(parsedPipeline)

        val segments = segmentedPipeline.segments
        assertEquals(2, segments.size)

        val segment0 = segments[0]
        assertEquals(0, segment0.startIndex)
        assert(segment0.isGroupSegment)
        assertEquals(1, segment0.jobs.size)
        assert(segment0.jobs[0].module is DummyEntryCreatorModule)

        val segment1 = segments[1]
        assertEquals(1, segment1.startIndex)
        assert(!segment1.isGroupSegment)
        assertEquals(1, segment1.jobs.size)
        assert(segment1.jobs[0].module is ArgDumpModule)
    }

    @Test
    fun testSegmentAndEquals() {
        var createdSegmentedPipeline = segment(parsedPipeline)
        createdSegmentedPipeline = SegmentedPipeline("SegmentedTestPipeline", createdSegmentedPipeline.segments)
        assertEquals(segmentedPipeline, createdSegmentedPipeline)
    }
}