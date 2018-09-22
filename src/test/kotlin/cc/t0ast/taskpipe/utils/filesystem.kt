package cc.t0ast.taskpipe.utils

import java.io.File

val EXAMPLE_PIPELINE_DIR = File("src/test/resources/example_pipeline")

fun createTestDirectory(): File {
    val testDir = File("${System.getProperty("user.dir")}/test_runs/$formattedTime")
    testDir.mkdirs()
    return testDir
}