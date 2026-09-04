plugins {
    // ModDevGradle 1.x targets NeoForge 1.20.1; may need Gradle 8 in some environments.
    id("net.neoforged.moddev") version "1.0.23"
    java
}

base {
    archivesName.set("Mariiy-TPA-neoforge-1.20")
}

neoForge {
    version = "20.1.86"
    mods {
        create("mariiy_tpa") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    implementation(project(":common"))
}

tasks.processResources {
    val ver = project.version.toString()
    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to ver)
    }
}
