package cc.t0ast.taskpipe.stages.parsing

import cc.t0ast.taskpipe.Pipeline

class ParsedPipeline(name: String, val jobs: List<Job>) : Pipeline(name)