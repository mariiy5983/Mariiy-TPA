plugins {
    java
    id("com.gradleup.shadow") version "9.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.purpurmc.org/snapshots")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks {
    processResources {
        val ver = project.version.toString()
        filesMatching("plugin.yml") {
            expand("version" to ver)
        }
    }

    shadowJar {
        archiveBaseName.set("Mariiy-TPA-paper")
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}
