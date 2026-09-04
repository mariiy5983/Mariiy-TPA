plugins {
    id("fabric-loom") version "1.9.2"
    java
}

base {
    archivesName.set("Mariiy-TPA-fabric-1.21")
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.1")
    mappings("net.fabricmc:yarn:1.21.1+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.115.0+1.21.1")
    implementation(project(":common"))
    include(project(":common"))
}

tasks.processResources {
    val ver = project.version.toString()
    filesMatching("fabric.mod.json") {
        expand("version" to ver)
    }
}
