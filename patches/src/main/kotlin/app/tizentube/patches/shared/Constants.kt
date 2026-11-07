package app.tizentube.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_TIZENTUBE_COBALT = Compatibility(
        name = "TizenTube",
        packageName = "io.gh.reisxd.tizentube.cobalt",
        // TizenTube Cobalt is distributed as a plain APK on its GitHub releases page.
        apkFileType = ApkFileType.APK,
        appIconColor = 0xE62117,
        targets = listOf(
            // Version the patch was developed against (cobalt-arm.apk).
            AppTarget(
                version = "2.0.3"
            ),
            // The patched methods are plain Cobalt code that rarely changes, so newer
            // versions will most likely work too.
            AppTarget(
                version = null,
                isExperimental = true
            )
        )
    )
}
