package cc.t0ast.taskpipe.cli.test_utils

import cc.t0ast.taskpipe.cli.Main
import java.io.*

fun execSelf(vararg args: String) {
    Main.main(arrayOf(*args))
}