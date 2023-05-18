import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.20"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.kunet"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.pgm.fyi/snapshots")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io/")
    //maven("https://repo.hpfxd.com/releases/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("com.github.Querz:NBT:6.1")

    compileOnly("app.ashcon:sportpaper:1.8.8-R0.1-SNAPSHOT")
    //compileOnly("com.hpfxd.pandaspigot:pandaspigot-api:1.8.8-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    withType<ShadowJar> {
        exclude("META-INF/**")
        exclude("DebugProbesKt.bin")

        // Thank you for tr7zw and contributors for this wonderful library
        //relocate("de.tr7zw.changeme.nbtapi", "de.tr7zw.turtleschematic.nbtapi")
    }
}

kotlin {
    jvmToolchain(8)
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.kunet"
            artifactId = "TurtleSchematic"
            version = "0.0.1"

            from(components["java"])
        }
    }
}

