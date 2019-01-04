package cc.t0ast.taskpipe.cli

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val filePathDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss-nn")

fun String.timeStamped(dateTimeFormatter: DateTimeFormatter = filePathDateTimeFormatter) =
    this.replace("%time%", LocalDateTime.now().format(dateTimeFormatter))
