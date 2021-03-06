import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.40"
    idea
    id("fabric-loom") version "0.2.5-SNAPSHOT"
}

base {
    archivesBaseName = ext["archive-base-name"].toString()
}

val minecraft: String by ext

val modVersion = ext["mod-version"] ?: error("Version was null")
val localBuild = ext["local-build"].toString().toBoolean()
version = "$modVersion+$minecraft" + if (localBuild) "-local" else ""

if (localBuild) {
    println("Note: local build mode enabled in gradle.properties; all dependencies might not work!")
}

allprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
        if (localBuild) {
            mavenLocal()
        }

        maven (url = "https://maven.fabricmc.net/")
        maven(url = "https://minecraft.curseforge.com/api/maven") { name = "CurseForge" }

        // For cotton, polyester and json-factory
        maven(url = "http://server.bbkr.space:8081/artifactory/libs-release")
        maven(url = "http://server.bbkr.space:8081/artifactory/libs-snapshot")

        // For Artifice
        maven (url = "https://maven.swordglowsblue.com" )

        // For LibBlockAttributes
        maven(url = "https://mod-buildcraft.com/maven") { name = "BuildCraft" }

        maven (url = "http://maven.sargunv.s3-website-us-west-2.amazonaws.com/")
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.getByName<ProcessResources>("processResources") {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version
                )
            )
        }
    }
}

minecraft {
}

inline fun DependencyHandler.modCompileAndInclude(str: String, block: ExternalModuleDependency.() -> Unit = {}) {
    modCompile(str, block)
    include(str, block)
}

inline fun DependencyHandler.includedMod(str: String, block: ExternalModuleDependency.() -> Unit = {}) {
    modImplementation(str, block)
    include(str, block)
}

inline fun DependencyHandler.includedMod(group: String, name: String, version: String, block: ExternalModuleDependency.() -> Unit = {}) {
    modImplementation(group, name, version, dependencyConfiguration = block)
    include(group, name, version, dependencyConfiguration = block)
}

dependencies {
    /**
     * Gets a version string with the [key].
     */
    fun v(key: String) = ext[key].toString()

    minecraft("com.mojang:minecraft:"+v("minecraft"))
    mappings("net.fabricmc:yarn:"+v("yarn_mappings"))

    // Fabric
    modCompile("net.fabricmc:fabric-loader:"+v("loader_version"))
    modCompile("net.fabricmc.fabric-api:fabric-api:"+v("fabric_api"))
    modCompile("net.fabricmc:fabric-language-kotlin:"+v("fabric_kotlin"))
    compileOnly("net.fabricmc:fabric-language-kotlin:"+v("fabric_kotlin"))

    // Other mods
    modCompileAndInclude("towelette:Towelette:"+v("towelette_version"))
    modCompileAndInclude("io.github.cottonmc:cotton:"+v("cotton_version"))
    modCompile("alexiil.mc.lib:libblockattributes-all:" + v("libblockattributes_version"))

    // Artifice
    modImplementation("artificemc:artifice:"+v("artifice_version"))
    include("artificemc:artifice:"+v("artifice_version"))

    // Roughly Enough Items
    modCompile("me.shedaniel:RoughlyEnoughItems:"+v("rei_version"))

    // Other libraries
    compileOnly("org.apiguardian:apiguardian-api:1.0.0")
}