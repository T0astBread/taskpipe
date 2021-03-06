package cc.t0ast.taskpipe.cli

import cc.t0ast.taskpipe.executePipeline
import cc.t0ast.taskpipe.utils.logging.shouldOutputLogs
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import kotlinx.coroutines.runBlocking

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            ArgParser(args).parseInto(::Args).run {
                val cliArgs = this
                cliArgs.outputDirectory.mkdirs()
                shouldOutputLogs = cliArgs.isVerbose

                runBlocking {
                    if (cliArgs.startBreakpoint !== null)
                        executePipeline(
                            cliArgs.pipelineDirectory,
                            cliArgs.outputDirectory,
                            cliArgs.startBreakpoint!!
                        )
                    else
                        executePipeline(
                            cliArgs.pipelineDirectory,
                            cliArgs.outputDirectory,
                            cliArgs.startJobIndex
                        )
                }
                println("Done")
            }
        }
    }
}