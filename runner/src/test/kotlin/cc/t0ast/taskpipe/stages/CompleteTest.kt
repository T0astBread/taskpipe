package cc.t0ast.taskpipe.stages

import cc.t0ast.taskpipe.executePipeline
import cc.t0ast.taskpipe.stages.parsing.parsePipeline
import cc.t0ast.taskpipe.stages.running.ParallelPipelineRunner
import cc.t0ast.taskpipe.stages.segmentation.segment
import cc.t0ast.taskpipe.test_utils.AMOUNT_OF_ENTRIES_IN_RUN
import cc.t0ast.taskpipe.test_utils.EXAMPLE_PIPELINE_DIR
import cc.t0ast.taskpipe.test_utils.EXAMPLE_PIPELINE_WITH_BREAKPOINT_DIR
import cc.t0ast.taskpipe.test_utils.createTestDirectory
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.lang.AssertionError
import java.util.stream.IntStream
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CompleteTest {

    @Test
    fun testAllStages() {
        val parsedPipeline = parsePipeline(EXAMPLE_PIPELINE_DIR)
        val segmentedPipeline = segment(parsedPipeline)

        val testDir = createTestDirectory()

        val pipelineRunner = ParallelPipelineRunner(testDir)
        runBlocking { pipelineRunner.run(segmentedPipeline) }

        checkGeneratedFilesystem(testDir)
    }

    @Test
    fun testAllStagesFromBreakpoint() {
        val testDir = createTestDirectory()

        runBlocking {
            executePipeline(
                EXAMPLE_PIPELINE_WITH_BREAKPOINT_DIR,
                testDir,
                0
            )

            checkGeneratedFilesystem(testDir, false)

            executePipeline(
                EXAMPLE_PIPELINE_WITH_BREAKPOINT_DIR,
                testDir,
                "test"
            )

            checkGeneratedFilesystem(testDir, true)
        }
    }

    private fun checkGeneratedFilesystem(rootDir: File, checkEntries: Boolean = true) {
        assert(rootDir.exists())
        assert(rootDir.isDirectory)

        val contentDir = File(rootDir, "content")
        assert(contentDir.exists())
        assert(contentDir.isDirectory)

        val entries = contentDir.list()
        assertEquals(AMOUNT_OF_ENTRIES_IN_RUN, entries.size)

        IntStream.rangeClosed(1, AMOUNT_OF_ENTRIES_IN_RUN).forEach { assert(entries.contains("entry$it")) }

        if (checkEntries)
            entries.map { File(contentDir, it) }
                .forEach { checkGeneratedEntryFilesystem(it) }
    }

    private fun checkGeneratedEntryFilesystem(entryRoot: File) {
        assert(entryRoot.exists())
        assert(entryRoot.isDirectory)

        val foldersInEntry = entryRoot.list()
        assertEquals(2, foldersInEntry.size)
        assert(foldersInEntry.contains("content"))
        assert(foldersInEntry.contains("module_data"))

        val contentFolder = File(entryRoot, "content")
        Assertions.assertArrayEquals(arrayOf("hellow.txt"), contentFolder.list())
        val contentFile = File(contentFolder, "hellow.txt")
        assert(contentFile.exists())
        assert(contentFile.isFile)
        assertEquals("Hello world from the echo module!", contentFile.readText().trim())
        assertNotEquals(0, contentFile.length())
    }
}