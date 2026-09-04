plugins {
    id("net.minecraftforge.gradle") version "6.0.36"
    java
}

base {
    archivesName.set("Mariiy-TPA-forge-1.20")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

minecraft {
    mappings(channel = "official", version = "1.20.1")

    runs {
        create("server") {
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args("--nogui")
            mods {
                create("mariiy_tpa") {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    minecraft("net.minecraftforge:forge:1.20.1-47.3.0")
    implementation(project(":common"))
}

tasks.processResources {
    val ver = project.version.toString()
    filesMatching("META-INF/mods.toml") {
        expand("version" to ver)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}
