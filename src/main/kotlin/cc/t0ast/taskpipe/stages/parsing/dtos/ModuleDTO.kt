package cc.t0ast.taskpipe.stages.parsing.dtos

typealias UnifiedRunCommand = String
typealias SplitRunCommand = Map<String, String>

data class ModuleDto(
        val name: String,
        val description: String,
        val type: String,
        val parameters: List<Parameter>,
        val run_command: Any
)