package cc.t0ast.taskpipe.stages.parsing.dtos

data class JobDTO(
    val module: String,
    val operation_mode: String? =  null,
    val arguments: Map<String, String>
)