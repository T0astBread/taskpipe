package cc.t0ast.taskpipe.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH.mm") // Windows doesn't allow colons in filenames
val formattedTime: String
    get() = TIME_FORMATTER.format(LocalDateTime.now())