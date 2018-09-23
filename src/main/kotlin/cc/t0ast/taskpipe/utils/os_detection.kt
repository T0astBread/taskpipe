package cc.t0ast.taskpipe.utils

val OS_CODE = getOSCode()

private fun getOSCode(): String {
    val systemProp = System.getProperty("os.name")
    if(systemProp.contains("Windows", ignoreCase = true)) {
        return "windows"
    }
    if(systemProp.contains(Regex("\\b[Mm]ac"))) {
        return "macos"
    }
    return "linux"
}