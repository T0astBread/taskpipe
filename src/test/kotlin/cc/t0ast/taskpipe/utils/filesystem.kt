package cc.t0ast.taskpipe.utils

import java.io.File

fun createTestDirectory(): File {
    val testDir = File("${System.getProperty("user.dir")}/test_runs/$formattedTime")
    testDir.mkdirs()
    return testDir
}