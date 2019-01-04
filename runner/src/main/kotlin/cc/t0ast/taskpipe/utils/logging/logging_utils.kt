package cc.t0ast.taskpipe.utils.logging

import java.util.logging.*

val loggers = mutableSetOf<Logger>()
var shouldOutputLogs = true
    set(value) {
        field = value
        reconfigureLoggers()
    }

class MessageFormatter : Formatter() {
    override fun format(record: LogRecord?): String {
        if(record == null) return "<<NULL MESSAGE>>"

        if(record.thrown == null) {
            return "[${record.loggerName}] ${record.level}: ${record.message}\n"
        }
        else {
            return "[${record.loggerName}] ${record.level}: ${record.message}\nThrown: ${record.thrown}\n"
        }
    }
}

fun getLogger(name: String): Logger {
    val logger = Logger.getLogger(name)
    configure(logger)
    loggers.add(logger)
    return logger
}

private fun reconfigureLoggers() {
    loggers.forEach { configure(it) }
}

private fun configure(logger: Logger) {
    logger.level = if(shouldOutputLogs) Level.ALL else Level.OFF

    var consoleHandler = logger.handlers.find { it is ConsoleHandler }
    if(consoleHandler == null) {
        consoleHandler = ConsoleHandler()
        consoleHandler.formatter = MessageFormatter()
        logger.addHandler(consoleHandler)
    }
    consoleHandler.level = logger.level
}