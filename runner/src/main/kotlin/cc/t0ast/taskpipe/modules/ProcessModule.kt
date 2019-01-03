package cc.t0ast.taskpipe.modules

import cc.t0ast.taskpipe.utils.logging.getLogger
import cc.t0ast.taskpipe.utils.splitIntoArgs
import java.io.File
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

const val TIMEOUT = 100L
val LOGGER = getLogger(ProcessModule::class.java.simpleName)

class ProcessModule(
        override val name: String,
        override val isGroupModule: Boolean,
        val runCommand: String,
        val moduleDir: File
): Module() {

    override suspend fun run(workingDirectory: File, arguments: Map<String, Any>) {
        val expandedArgs = arguments.toMutableMap()
        expandedArgs["module_dir"] = this.moduleDir.absolutePath

        val bakedRunCommand = this.runCommand.bakeWith(expandedArgs)
        val bakedRunCommandArgs = bakedRunCommand.splitIntoArgs()

        val processBuilder = ProcessBuilder()
        processBuilder.command(*bakedRunCommandArgs)
        processBuilder.directory(workingDirectory)

        LOGGER.finer("Starting module process with command: $bakedRunCommand")
        val process = processBuilder.start()
        LOGGER.fine("Started module process. (PID: ${process.pid()}, Command: $bakedRunCommand)")
        process.waitFor(TIMEOUT, TimeUnit.SECONDS)

        val exitValue = process.exitValue()
        if(exitValue != 0) {
            val errorMsg = "Module process with PID ${process.pid()} finished with non-zero exit value $exitValue"
            LOGGER.severe(errorMsg)
            LOGGER.severe("Error stream dump:\n" + process.errorStream.readAllBytes().toString(Charset.forName("utf-8")))
            throw RuntimeException(errorMsg)
        }
        else {
            LOGGER.fine("Module process with PID ${process.pid()} finished with exit value zero")
        }
    }

    private fun String.bakeWith(arguments: Map<String, Any>): String {
        var baked = this
        arguments.forEach { key, value ->
            baked = baked
                    .replace(Regex("(?<!\\\\)\\{$key}"), value.toString()) // Fill in args
                    .replace("\\{", "{") // Un-escape opening brackets
                    .replace("\\}", "}") // Un-escape closing brackets
        }
        return baked
    }
}