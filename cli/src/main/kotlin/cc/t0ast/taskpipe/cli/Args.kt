package cc.t0ast.taskpipe.cli

import com.xenomachina.argparser.ArgParser

class Args(parser: ArgParser) {
    val command by parser.positional(
        "COMMAND",
        help = "The command to execute; Possible values: ${Command
            .values()
            .map { it.toString() }
            .map { it.toLowerCase() }
            .joinToString("|")
        }. See \"help\" for more details on each command."
    ) { Command.valueOf(this.toUpperCase()) }

    enum class Command {
        RUN, HELP
    }
}