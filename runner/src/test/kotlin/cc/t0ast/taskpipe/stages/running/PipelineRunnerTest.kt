package cc.t0ast.taskpipe.stages.running

import cc.t0ast.taskpipe.test_utils.AMOUNT_OF_ENTRIES_IN_RUN
import cc.t0ast.taskpipe.test_utils.createTestDirectory
import cc.t0ast.taskpipe.test_utils.segmentedPipeline
import cc.t0ast.taskpipe.test_utils.segmentedPipelineWithBreakpoint
import kotlinx.coroutines.runBlocking
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

        checkGeneratedFilesystem(testDir) { entryRoot ->
            checkBasicEntryFilesystem2(entryRoot)
            checkEchoJobOutput(entryRoot)
            checkArgDumpJobOutput(entryRoot)
        }
    }

    @Test
    fun testRunWithBreakpoint() = runBlocking {
        val testDir = createTestDirectory()
        val runner = ParallelPipelineRunner(testDir)
        val pipeline = segmentedPipelineWithBreakpoint

        runner.run(pipeline)

        checkGeneratedFilesystem(testDir) { entryRoot ->
            checkBasicEntryFilesystem1(entryRoot)
            checkEchoJobOutput(entryRoot)
        }
    }

    private fun checkGeneratedFilesystem(rootDir: File, checkEntry: (File) -> Unit) {
        println("Checking generated filesystem at ${rootDir.path}")

        assert(rootDir.exists())
        assert(rootDir.isDirectory)

        val contentDir = File(rootDir, "content")
        assert(contentDir.exists())
        assert(contentDir.isDirectory)

        val entries = contentDir.list()
        assertEquals(AMOUNT_OF_ENTRIES_IN_RUN, entries.size)

        IntStream.range(0, AMOUNT_OF_ENTRIES_IN_RUN).forEach { assert(entries.contains("entry$it")) }

        entries.map { File(contentDir, it) }
            .forEach { checkEntry(it) }
    }

    private fun checkBasicEntryFilesystem1(entryRoot: File) {
        assert(entryRoot.exists())
        assert(entryRoot.isDirectory)

        val foldersInEntry = entryRoot.list()
        assertEquals(1, foldersInEntry.size)
        assert(foldersInEntry.contains("content"))
    }

    private fun checkBasicEntryFilesystem2(entryRoot: File) {
        assert(entryRoot.exists())
        assert(entryRoot.isDirectory)

        val foldersInEntry = entryRoot.list()
        assertEquals(2, foldersInEntry.size)
        assert(foldersInEntry.contains("content"))
        assert(foldersInEntry.contains("module_data"))
    }

    private fun checkEchoJobOutput(entryRoot: File) {
        val contentFolder = File(entryRoot, "content")
        Assertions.assertArrayEquals(arrayOf("content.txt"), contentFolder.list())
        val contentFile = File(contentFolder, "content.txt")
        assert(contentFile.exists())
        assert(contentFile.isFile)
        assertNotEquals(0, contentFile.length())
    }

    private fun checkArgDumpJobOutput(entryRoot: File) {
        val argDumpModuleFolder = File(entryRoot, "module_data/ArgDump")
        assert(argDumpModuleFolder.exists())
        assert(argDumpModuleFolder.isDirectory)

        val argDumpFile = File(argDumpModuleFolder, "args")
        assert(argDumpFile.exists())
        assert(argDumpFile.isFile)
        assertNotEquals(0, argDumpFile.length())
    }
}