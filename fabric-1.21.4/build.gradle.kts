plugins {
    id("fabric-loom") version "1.9.2"
    java
}

base {
    archivesName.set("Mariiy-TPA-fabric-1.21.4")
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.119.2+1.21.4")
    implementation(project(":common"))
    include(project(":common"))
}

tasks.processResources {
    val ver = project.version.toString()
    filesMatching("fabric.mod.json") {
        expand("version" to ver)
    }
}
