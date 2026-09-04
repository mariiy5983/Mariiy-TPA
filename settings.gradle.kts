pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Mariiy-TPA"

/*
 * -Pplatforms=paper,fabric,neoforge (default)
 * fabric → both fabric-1.20.1 + fabric-1.21.1
 * neoforge → neoforge-1.21.1 (1.20.1: -Pplatforms=neoforge-1.20.1)
 * forge → needs Gradle 8: -Pplatforms=forge-1.20.1 or forge-1.21.1
 */
val platformsProp = (providers.gradleProperty("platforms").orNull
    ?: "paper,fabric,neoforge")
    .split(",")
    .map { it.trim().lowercase() }
    .filter { it.isNotEmpty() }
    .toSet()

fun want(name: String): Boolean =
    platformsProp.contains("all") || platformsProp.contains(name)

include("common")
if (want("paper")) include("paper")

if (want("fabric") || want("fabric-1.20") || want("fabric-1.20.1")) {
    include("fabric-1.20.1")
}
if (want("fabric") || want("fabric-1.21") || want("fabric-1.21.1")) {
    include("fabric-1.21.1")
}

if (want("neoforge-1.20") || want("neoforge-1.20.1") || want("all")) {
    include("neoforge-1.20.1")
}
if (want("neoforge") || want("neoforge-1.21") || want("neoforge-1.21.1")) {
    include("neoforge-1.21.1")
}

if (want("forge-1.20") || want("forge-1.20.1") || want("all")) {
    include("forge-1.20.1")
}
if (want("forge") || want("forge-1.21") || want("forge-1.21.1")) {
    include("forge-1.21.1")
}
