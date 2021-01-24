package com.github.theunlocked.tryitonyourphone.model

data class RunRequest(
    val language: Language,
    val code: String,
    val input: String,
    val cflags: List<String>,
    val options: List<String>,
    val argv: List<String>
) {
    override fun toString() =
        "Vlang\u00001\u0000${language.id}\u0000" +
        (if (language.hasCflags) "VTIO_CFLAGS\u0000${cflags.count()}\u0000${cflags.joinToString("\u0000")}\u0000" else "") +
        (if (language.hasOptions) "VTIO_OPTIONS\u0000${options.count()}\u0000${options.joinToString("\u0000")}\u0000" else "") +
        "F.code.tio\u0000${code.length}\u0000${code}" +
        "F.input.tio\u0000${input.length}\u0000${input}" +
        "Vargs\u0000${argv.count()}\u0000${argv.joinToString("\u0000")}\u0000" +
        "R"
}