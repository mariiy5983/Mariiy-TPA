plugins {
    java
}

allprojects {
    group = "com.mariiy"
    version = property("version") as String

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    // Paper + MC 1.20.x loaders: Java 17 bytecode. MC 1.21.x loaders: Java 21.
    val javaRelease = when {
        name == "common" || name == "paper" -> 17
        name.contains("1.20.1") -> 17
        name.contains("1.20") -> 21 // 1.20.5+
        else -> 21
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaRelease))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(javaRelease)
    }
}

tasks.register("buildPaper") {
    dependsOn(":paper:shadowJar")
    group = "build"
    description = "Build Paper plugin (MC 1.20–26.2)"
}

tasks.register("buildFabric") {
    dependsOn(
        ":fabric-1.20.1:remapJar",
        ":fabric-1.20.6:remapJar",
        ":fabric-1.21.1:remapJar",
        ":fabric-1.21.4:remapJar"
    )
    group = "build"
    description = "Build Fabric jars (1.20.1 / 1.20.6 / 1.21.1 / 1.21.4)"
}

tasks.register("buildNeoForge") {
    dependsOn(":neoforge-1.20.1:jar", ":neoforge-1.21.1:jar")
    group = "build"
    description = "Build NeoForge jars for 1.20.x and 1.21.x"
}
