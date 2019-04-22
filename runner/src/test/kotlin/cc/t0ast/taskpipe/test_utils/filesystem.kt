package cc.t0ast.taskpipe.test_utils

import java.io.File

val EXAMPLE_PIPELINE_DIR = File("src/test/resources/example_pipeline")
val EXAMPLE_PIPELINE_WITH_BREAKPOINT_DIR = File("src/test/resources/example_pipeline_with_breakpoint")

fun createTestDirectory(): File {
    val testDir = File("${System.getProperty("user.dir")}/test_runs/$formattedTime")
    testDir.mkdirs()
    return testDir
}