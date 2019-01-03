package cc.t0ast.taskpipe.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            ArgParser(args).parseInto(::Args).run {
                println(if(this.verbose) "Hello planet earth, horizon and everything we know beyond!" else "Hello world!")
            }
        }
    }
}