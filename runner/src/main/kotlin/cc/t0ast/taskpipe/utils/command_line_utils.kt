package cc.t0ast.taskpipe.utils

import java.lang.StringBuilder

fun String.splitIntoArgs(): Array<String> {
    val tokens = split(" ")
    val combinedTokens = mutableListOf<String>()
    var currentTokenString: StringBuilder?

    var i = 0
    while(i < tokens.size) {
        var token = tokens[i]

        if(token.startsWith("\"")) {
            currentTokenString = StringBuilder()
            while(true) {
                currentTokenString!!.append(token).append(" ")
                i++
                if(token.endsWith("\"") || i >= tokens.size) break
                token = tokens[i]
            }
            combinedTokens.add(currentTokenString.toString().substring(1, currentTokenString.length - 2))
        }
        else {
            combinedTokens.add(token)
            i++
        }
    }

    return combinedTokens.toTypedArray()
}
