plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.nucleoid.xyz")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")

    mappings("net.fabricmc:yarn:${property("yarn_mappings")}")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation(include("net.kyori:adventure-platform-fabric:5.9.0")!!)
    modImplementation(include("net.kyori:adventure-text-minimessage:4.14.0")!!)

    compileOnly("net.luckperms:api:5.4")
    include(modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")!!)

    modImplementation("eu.pb4:sgui:1.6.1+1.21.1")
    include("eu.pb4:sgui:1.6.1+1.21.1")
}

tasks {

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            // uncomment to publish to the local maven
            // mavenLocal()
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }

}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}