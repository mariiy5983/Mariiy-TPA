plugins {
    id("net.neoforged.moddev") version "2.0.78"
    java
}

base {
    archivesName.set("Mariiy-TPA-neoforge-1.21")
}

neoForge {
    version = "21.1.133"
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
