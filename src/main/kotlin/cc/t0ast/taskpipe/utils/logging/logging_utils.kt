package cc.t0ast.taskpipe.utils.logging

import java.util.logging.*

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
    logger.level = Level.ALL

    if(!logger.handlers.any { it is ConsoleHandler }) {
        val consoleHandler = ConsoleHandler()
        consoleHandler.level = Level.ALL
        consoleHandler.formatter = MessageFormatter()
        logger.addHandler(consoleHandler)
    }

    return logger
}