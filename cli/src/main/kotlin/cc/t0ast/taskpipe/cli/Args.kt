package cc.t0ast.taskpipe.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.io.File

class Args(parser: ArgParser) {
    val pipelineDirectory by parser.storing(
        "-p", "--pipeline",
        help = "the directory to read the pipeline configuration from; will default to the current directory"
    ) { File(this) }
        .default { File(System.getProperty("user.dir")) }

    val outputDirectory by parser.storing(
        "-o", "--output",
        help = "the directory to put the output files in; will default to the current directory"
    ) { File(this) }
        .default { pipelineDirectory.resolve("run_%time%".timeStamped()) }

    val isVerbose by parser.flagging(
        "-v", "--verbose",
        help = "enables verbose mode; In verbose mode all output from the pipeline runner is printed to the console."
    )
}