package cc.t0ast.taskpipe.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-mm-yyyy_hh.MM") // Windows doesn't allow colons in filenames
val formattedTime: String
    get() = TIME_FORMATTER.format(LocalDateTime.now())