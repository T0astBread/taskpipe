package cc.t0ast.taskpipe.cli.test_utils

import cc.t0ast.taskpipe.cli.Main
import java.io.*

fun execSelf(vararg args: String): String {
    val stdOut = System.out

    val capturedOutStream = ByteArrayOutputStream()
    val capturedOut = PrintStream(capturedOutStream)

    System.setOut(capturedOut)
    Main.main(arrayOf(*args))
    System.out.flush()
    System.setOut(stdOut)

    return capturedOutStream.toString()
}