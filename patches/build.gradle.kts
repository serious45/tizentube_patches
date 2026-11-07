group = "app.tizentube"

patches {
    about {
        name = "TizenTube Remote Patches"
        description = "Remote-control tweaks for TizenTube Cobalt (Android TV)"
        source = "git@github.com:GITHUB_USER/tizentube-patches.git"
        author = "GITHUB_USER"
        contact = "na"
        website = "https://github.com/GITHUB_USER/tizentube-patches"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
