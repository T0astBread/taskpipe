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
            checkEntryRootDir(entryRoot)

            val foldersInEntry = entryRoot.list()
            assertEquals(2, foldersInEntry.size)
            checkContentFolder(foldersInEntry)
            checkModuleDataFolder(foldersInEntry)

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
            checkEntryRootDir(entryRoot)

            val foldersInEntry = entryRoot.list()
            assertEquals(1, foldersInEntry.size)
            checkContentFolder(foldersInEntry)

            checkEchoJobOutput(entryRoot)
        }
    }

    @Test
    fun testRunFromStartIndex() = runBlocking {
        val testDir = createTestDirectory()
        val runner = ParallelPipelineRunner(testDir)
        val pipeline = segmentedPipeline

        val dummyEntry = File(testDir, "content/entry0")
        dummyEntry.mkdirs()

        runner.run(pipeline, 1)

        checkGeneratedFilesystem(testDir, false) { entryRoot ->
            checkEntryRootDir(entryRoot)

            val foldersInEntry = entryRoot.list()
            assertEquals(1, foldersInEntry.size)
            checkModuleDataFolder(foldersInEntry)

            checkArgDumpJobOutput(entryRoot)
        }
    }

    private fun checkGeneratedFilesystem(rootDir: File, checkAmountOfEntries: Boolean = true, checkEntry: (File) -> Unit) {
        println("Checking generated filesystem at ${rootDir.path}")

        assert(rootDir.exists())
        assert(rootDir.isDirectory)

        val contentDir = File(rootDir, "content")
        assert(contentDir.exists())
        assert(contentDir.isDirectory)

        val entries = contentDir.list()
        if(checkAmountOfEntries) {
            assertEquals(AMOUNT_OF_ENTRIES_IN_RUN, entries.size)
            IntStream.range(0, AMOUNT_OF_ENTRIES_IN_RUN).forEach { assert(entries.contains("entry$it")) }
        }

        entries.map { File(contentDir, it) }
            .forEach { checkEntry(it) }
    }

    private fun checkEntryRootDir(entryRoot: File) {
        assert(entryRoot.exists())
        assert(entryRoot.isDirectory)
    }

    private fun checkContentFolder(foldersInEntry: Array<String>) {
        assert(foldersInEntry.contains("content"))
    }

    private fun checkModuleDataFolder(foldersInEntry: Array<String>) {
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