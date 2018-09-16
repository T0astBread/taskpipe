package cc.t0ast.taskpipe.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME
val formattedTime: String
    get() = TIME_FORMATTER.format(LocalDateTime.now())