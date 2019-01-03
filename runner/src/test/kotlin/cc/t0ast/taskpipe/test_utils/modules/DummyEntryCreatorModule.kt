package cc.t0ast.taskpipe.test_utils.modules

import cc.t0ast.taskpipe.modules.Module
import java.io.File
import java.io.PrintWriter
import java.util.stream.IntStream

class DummyEntryCreatorModule : Module() {
    override val name = "DummyEntryCreator"
    override val isGroupModule = true

    override suspend fun run(workingDirectory: File, arguments: Map<String, Any>) {
        IntStream.range(0, arguments[AMOUNT_OF_ENTRIES] as Int).forEach { entryIndex ->
            val entryDir = File(workingDirectory, "entry$entryIndex")
            entryDir.mkdir()

            val entryContentDir = File(entryDir, "content")
            entryContentDir.mkdir()

            val entryContentFile = File(entryContentDir, "content.txt")
            entryContentFile.createNewFile()

            PrintWriter(entryContentFile).use {
                it.println("Hello world from entry #$entryIndex")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is DummyEntryCreatorModule
    }

    companion object {
        val AMOUNT_OF_ENTRIES = "amount_of_entries"
    }
}