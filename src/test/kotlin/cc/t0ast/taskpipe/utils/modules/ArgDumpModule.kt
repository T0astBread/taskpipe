package cc.t0ast.taskpipe.utils.modules

import cc.t0ast.taskpipe.Module
import java.io.File
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArgDumpModule : Module() {
    override val name = "ArgDump"
    override val isGroupModule = false

    override suspend fun run(workingDirectory: File, arguments: Map<String, Any>) {
        val dataDirectory = File(workingDirectory, "module_data/ArgDump")
        dataDirectory.mkdirs()

        val argDataFile = File(dataDirectory, "args")
        argDataFile.createNewFile()

        PrintWriter(argDataFile).use {
            it.println("Dump of arguments. Run at ${DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())}\n")
            arguments.forEach { key, value ->
                it.println("$key -> $value")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is ArgDumpModule
    }
}