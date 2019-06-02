package cc.t0ast.taskpipe.utils

import cc.t0ast.taskpipe.modules.BreakpointModule
import cc.t0ast.taskpipe.stages.parsing.ParsedPipeline

fun ParsedPipeline.findIndexOfBreakpoint(breakpointName: String) =
    this.jobs.indexOfFirst { job ->
        job.module is BreakpointModule &&
        job.arguments.getOrDefault("name", null) == breakpointName
    }
