package cc.t0ast.taskpipe.stages.parsing.dtos

data class JobDTO(val module: String, val arguments: Map<String, String>)