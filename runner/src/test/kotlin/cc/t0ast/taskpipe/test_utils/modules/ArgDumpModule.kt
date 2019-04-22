package cc.t0ast.taskpipe.test_utils.modules

import cc.t0ast.taskpipe.OperationMode
import cc.t0ast.taskpipe.modules.Module
import cc.t0ast.taskpipe.stages.running.RunContext
import java.io.File
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArgDumpModule : Module() {
    override val name = "ArgDump"
    override val supportedOperationModes: Array<OperationMode> = arrayOf(OperationMode.INDIVIDUAL)

    override suspend fun run(runContext: RunContext, workingDirectory: File, arguments: Map<String, Any>) {
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