package app.tizentube.patches.backminiplayer

import app.morphe.patcher.Fingerprint

/**
 * Every remote key press goes through this method before it is handed to the web page.
 * Cobalt's dev.cobalt.coat classes are not obfuscated in TizenTube Cobalt builds.
 */
internal object CobaltOnKeyDownFingerprint : Fingerprint(
    definingClass = "Ldev/cobalt/coat/CobaltActivity;",
    name = "onKeyDown",
    returnType = "Z",
    parameters = listOf("I", "Landroid/view/KeyEvent;"),
)

/**
 * Runs JavaScript in the page that hosts YouTube and TizenTube.
 * Matching it here makes patching fail cleanly if a future build drops the method,
 * instead of producing an app that crashes on the first key press.
 */
internal object CobaltEvaluateJavaScriptFingerprint : Fingerprint(
    definingClass = "Ldev/cobalt/coat/CobaltActivity;",
    name = "evaluateJavaScript",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;"),
)
