package app.tizentube.patches.backminiplayer

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.patch
import app.tizentube.patches.shared.Constants.COMPATIBILITY_TIZENTUBE_COBALT

private const val SCRIPT_RESOURCE = "tizentube/back-to-mini-player.js"

@Suppress("unused")
val backButtonMiniPlayerPatch = patch(
    name = "Back button opens Mini Player",
    description = "Pressing Back on the remote while a video plays full screen opens TizenTube's " +
        "Mini Player: the previous screen is shown and the video keeps playing in the corner.",
    default = true
) {
    compatibleWith(COMPATIBILITY_TIZENTUBE_COBALT)

    execute {
        val script = smaliStringLiteral(loadScript())

        val evaluate = CobaltEvaluateJavaScriptFingerprint.method
        val evaluateReference = "${evaluate.definingClass}->${evaluate.name}(Ljava/lang/String;)V"

        CobaltOnKeyDownFingerprint.method.apply {
            // v0 is used at index 0, so it must be a local register and not a parameter.
            // onKeyDown(int, KeyEvent) has 3 parameter registers (this, keyCode, event).
            val registerCount = implementation!!.registerCount
            val parameterRegisters = 1 + parameters.size
            if (registerCount - parameterRegisters < 1) {
                throw PatchException("onKeyDown has no free register (registers: $registerCount)")
            }

            // Install the Back key handler in the page on every key press. The script
            // guards itself, so it only installs once per page load, and it comes back
            // automatically if the page reloads.
            addInstructions(
                0,
                """
                    const-string v0, "$script"
                    invoke-virtual {p0, v0}, $evaluateReference
                """
            )
        }
    }
}

private fun loadScript(): String {
    val stream = object {}.javaClass.classLoader.getResourceAsStream(SCRIPT_RESOURCE)
        ?: throw PatchException("Missing bundled resource $SCRIPT_RESOURCE")
    val source = stream.bufferedReader().use { it.readText() }

    // Drop block comments and indentation to keep the string small,
    // since it is sent to the page on every key press.
    return source
        .replace(Regex("""/\*[\s\S]*?\*/"""), "")
        .lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString("\n")
}

/** Escapes text for use inside a smali "..." string literal. */
private fun smaliStringLiteral(text: String): String = buildString {
    for (c in text) {
        when {
            c == '\\' -> append("\\\\")
            c == '"' -> append("\\\"")
            c == '\'' -> append("\\'")
            c == '\n' -> append("\\n")
            c == '\r' -> {}
            c == '\t' -> append("\\t")
            c.code < 0x20 || c.code > 0x7e -> append("\\u%04x".format(c.code))
            else -> append(c)
        }
    }
}
