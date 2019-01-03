package cc.t0ast.taskpipe.cli

import com.xenomachina.argparser.ArgParser

class Args(parser: ArgParser) {
    val verbose by parser.flagging("-v", help = "verbose mode")
}