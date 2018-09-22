package cc.t0ast.taskpipe.stages.parsing

import cc.t0ast.taskpipe.modules.Module
import cc.t0ast.taskpipe.modules.ProcessModule
import cc.t0ast.taskpipe.stages.parsing.dtos.ModuleDto
import cc.t0ast.taskpipe.stages.parsing.dtos.PipelineDTO
import com.google.gson.Gson
import java.io.File

private val GSON = Gson()

fun parsePipeline(pipelineDir: File): ParsedPipeline {
    val pipelineDto = loadPipeline(pipelineDir)
    val moduleDtos = loadModules(pipelineDto, pipelineDir)

    val modules = pipelineDto.jobs.map { jobDto ->
        val module = moduleDtos[jobDto.module] ?: throw RuntimeException("Module ${jobDto.module} was not found")
        return@map Job(module.toRealModule(), jobDto.arguments)
    }
    return ParsedPipeline(pipelineDto.name, modules)
}

private fun loadPipeline(pipelineDir: File): PipelineDTO {
    val pipelineFile = File(pipelineDir, "pipeline.json")
    val pipeline = pipelineFile.reader().use { reader ->
        GSON.fromJson(reader, PipelineDTO::class.java)
    }
    return pipeline
}

private fun loadModules(pipeline: PipelineDTO, pipelineDir: File): Map<String, ModuleDto> {
    val modulesDir = File(pipelineDir, "modules")
    val loadedModules = mutableMapOf<String, ModuleDto>()
    pipeline.jobs.forEach { job ->
        if(!loadedModules.containsKey(job.module)) {
            loadedModules[job.module] = loadModule(modulesDir, job.module)
        }
    }
    return loadedModules
}

private fun loadModule(modulesDir: File, moduleName: String): ModuleDto {
    val moduleFile = File(modulesDir, "$moduleName/module.json")
    val module = moduleFile.reader().use { reader ->
        GSON.fromJson(reader, ModuleDto::class.java)
    }
    return module
}

fun ModuleDto.toRealModule(): Module {
    val usedRunCommand =
            (if(this.run_command is String)
                this.run_command
            else
                (this.run_command as Map<String, String>)["windows"])
                    ?: throw RuntimeException("No module run command found for platform windows")
    return ProcessModule(this.name, this.type == "group", usedRunCommand)
}
