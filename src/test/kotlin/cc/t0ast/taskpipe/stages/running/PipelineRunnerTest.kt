package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.test_utils.AMOUNT_OF_ENTRIES_IN_RUN
import cc.t0ast.taskpipe.test_utils.createTestDirectory
import cc.t0ast.taskpipe.test_utils.segmentedPipeline
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.util.stream.IntStream
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PipelineRunnerTest {

    @Test
    fun testRun() = runBlocking {
        val testDir = createTestDirectory()
        val runner = ParallelPipelineRunner(testDir)
        val pipeline = segmentedPipeline

        runner.run(pipeline)

        checkGeneratedFilesystem(testDir)
    }

    private fun checkGeneratedFilesystem(rootDir: File) {
        assert(rootDir.exists())
        assert(rootDir.isDirectory)

        val entries = rootDir.list()
        assertEquals(AMOUNT_OF_ENTRIES_IN_RUN, entries.size)

        IntStream.range(0, AMOUNT_OF_ENTRIES_IN_RUN).forEach { assert(entries.contains("entry$it")) }

        entries.map { File(rootDir, it) }
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
        Assertions.assertArrayEquals(arrayOf("content.txt"), contentFolder.list())
        val contentFile = File(contentFolder, "content.txt")
        assert(contentFile.exists())
        assert(contentFile.isFile)
        assertNotEquals(0, contentFile.length())

        val argDumpModuleFolder = File(entryRoot, "module_data/ArgDump")
        assert(argDumpModuleFolder.exists())
        assert(argDumpModuleFolder.isDirectory)

        val argDumpFile = File(argDumpModuleFolder, "args")
        assert(argDumpFile.exists())
        assert(argDumpFile.isFile)
        assertNotEquals(0, argDumpFile.length())
    }
}