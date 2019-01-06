package cc.t0ast.taskpipe.stages.parsing

import cc.t0ast.taskpipe.OperationMode
import cc.t0ast.taskpipe.modules.Module
import cc.t0ast.taskpipe.modules.ProcessModule
import cc.t0ast.taskpipe.stages.parsing.dtos.JobDTO
import cc.t0ast.taskpipe.stages.parsing.dtos.ModuleDTO
import cc.t0ast.taskpipe.stages.parsing.dtos.PipelineDTO
import cc.t0ast.taskpipe.utils.OS_CODE
import com.google.gson.Gson
import java.io.File

private val GSON = Gson()

fun parsePipeline(pipelineDir: File): ParsedPipeline {
    val pipelineDTO = loadPipeline(pipelineDir)
    val moduleDTOs = loadModules(pipelineDTO, pipelineDir)
    val jobs = loadJobs(pipelineDTO, moduleDTOs)
    return ParsedPipeline(pipelineDTO.name, jobs)
}

private fun loadPipeline(pipelineDir: File): PipelineDTO {
    val pipelineFile = File(pipelineDir, "pipeline.json")
    val pipeline = pipelineFile.reader().use { reader ->
        GSON.fromJson(reader, PipelineDTO::class.java)
    }
    return pipeline
}

private fun loadModules(pipeline: PipelineDTO, pipelineDir: File): Map<String, ModuleDTO> {
    val modulesDir = File(pipelineDir, "modules")
    val loadedModules = mutableMapOf<String, ModuleDTO>()
    pipeline.jobs.forEach { job ->
        if(!loadedModules.containsKey(job.module)) {
            loadedModules[job.module] = loadModule(modulesDir, job.module)
        }
    }
    return loadedModules
}

private fun loadModule(modulesDir: File, moduleName: String): ModuleDTO {
    try {
        val moduleFile = File(modulesDir, "$moduleName/module.json")
        val module = moduleFile.reader().use { reader ->
            GSON.fromJson(reader, ModuleDTO::class.java)
        }
        module.directory = File(modulesDir, moduleName)
        return module
    } catch (exception: Exception) {
        throw RuntimeException("Failed while parsing module $moduleName")
    }
}

fun ModuleDTO.toRealModule(): Module {
    val supportedOperationModes = this.operation_modes
        .split("|")
        .map { it.toUpperCase() }
        .map { OperationMode.valueOf(it) }
        .toTypedArray()
    val usedRunCommand =
            (if(this.run_command is String)
                this.run_command
            else
                (this.run_command as Map<String, String>)[OS_CODE])
                    ?: throw RuntimeException("No module run command found for platform $OS_CODE")
    return ProcessModule(this.name, supportedOperationModes, usedRunCommand, this.directory)
}

private fun loadJobs(pipelineDTO: PipelineDTO, moduleDTOs: Map<String, ModuleDTO>): List<Job> {
    return pipelineDTO.jobs.map { jobDTO ->
        try {
            val moduleDTO = moduleDTOs[jobDTO.module] ?: throw RuntimeException("Module ${jobDTO.module} was not found")
            val module = moduleDTO.toRealModule()
            val operationMode = getUsedOperationMode(jobDTO, module)
            return@map Job(module, operationMode, jobDTO.arguments)
        } catch (exception: Exception) {
            throw RuntimeException("Failed while parsing job $jobDTO")
        }
    }
}

private fun getUsedOperationMode(jobDTO: JobDTO, module: Module): OperationMode {
    if(jobDTO.operation_mode != null) {
        val operationMode = OperationMode.valueOf(jobDTO.operation_mode.toUpperCase())
        if(module.supportedOperationModes.contains(operationMode)) {
            return operationMode
        } else {
            throw RuntimeException("Operation mode $operationMode is not allowed for module ${module.name} (allowed operation modes: [${module.supportedOperationModes.joinToString(", ")}])")
        }
    } else if(module.supportedOperationModes.size == 1) {
        return module.supportedOperationModes[0]
    } else {
        throw RuntimeException("More than one operation mode is supported by the module ${module.name} but no operation mode is specified in the pipeline.json file")
    }
}
